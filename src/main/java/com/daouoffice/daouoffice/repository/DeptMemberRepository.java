package com.daouoffice.daouoffice.repository;

import com.daouoffice.daouoffice.entity.DeptMember;
import com.daouoffice.daouoffice.entity.DeptMemberId;
import com.daouoffice.daouoffice.dto.UserDeptProjection;
import com.daouoffice.daouoffice.dto.UserDto;
import com.daouoffice.daouoffice.dto.UserSearchDto;
import com.daouoffice.daouoffice.dto.UserDetailDto;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface DeptMemberRepository extends JpaRepository<DeptMember, DeptMemberId> {

    // 부서별 직속인원 수
    @Query("SELECT dm.id.departmentId, COUNT(dm.id.userId) " +
            "FROM DeptMember dm GROUP BY dm.id.departmentId")
    List<Object[]> findDirectCounts();

    // 부서별 사용자 리스트 - Native Query 사용 (제공해주신 코드)
    @Query(value = """
    WITH RECURSIVE department_tree AS (
      SELECT
        id,
        parent_id,
        0 AS depth,
        LPAD(CAST(id AS VARCHAR(20)), 10, '0') AS path
      FROM go_departments
      WHERE id = :deptId
      UNION ALL
      SELECT
        d.id,
        d.parent_id,
        dt.depth + 1,
        CONCAT(dt.path, '.', LPAD(CAST(d.id AS VARCHAR(20)), 10, '0')) AS path
      FROM go_departments d
      JOIN department_tree dt ON d.parent_id = dt.id
    )
    SELECT
      u.id AS userId,
      u.name AS userName,
      dc.ko_name AS positionName,
      CONCAT('https://gw.sghitech.co.kr/thumb/user/small/', af.id, '-', af.length) AS photoUrl,
      dm.sort_order AS sortOrder,
      dm.type AS type,
      dm.department_id AS departmentId,
      d.name AS departmentName
    FROM
      go_dept_members dm
    JOIN
      department_tree dt ON dm.department_id = dt.id
    JOIN
      go_departments d ON d.id = dm.department_id
    JOIN
      go_users u ON dm.user_id = u.id
    LEFT JOIN
      go_user_profiles up ON up.id = u.id
    LEFT JOIN
      go_domain_codes dc ON dc.id = up.position_id
    LEFT JOIN
      go_attach_files af ON af.id = up.photo_id
    ORDER BY
      dt.depth,
      dt.path,
      CASE
        WHEN dc.ko_name = '수석Ⅲ(사업부장)' THEN 4
        WHEN dc.ko_name = '수석Ⅱ(사업부장)' THEN 5
        ELSE dc.sort_order
      END,
      COALESCE(dm.sort_order, 2147483647),
      u.name
    """, nativeQuery = true)
    List<UserDeptProjection> findUserProjectionsByDepartment(@Param("deptId") Long deptId);

    default List<UserDto> findUsersByDepartment(Long deptId) {
        return findUserProjectionsByDepartment(deptId).stream()
                .map(projection -> new UserDto(
                        projection.getUserId(),
                        projection.getUserName(),
                        projection.getPositionName(),
                        projection.getPhotoUrl(),
                        projection.getSortOrder(),
                        projection.getType(),
                        projection.getDepartmentId(),
                        projection.getDepartmentName()))
                .collect(Collectors.toList());
    }

    // 이름 또는 login_id로 사용자 검색
    @Query("""
    SELECT new com.daouoffice.daouoffice.dto.UserSearchDto(
      u.id,
      u.name,
      dc.koName,
      CASE 
        WHEN af.id IS NOT NULL AND af.length IS NOT NULL 
        THEN CONCAT('https://gw.sghitech.co.kr/thumb/user/small/', af.id, '-', af.length)
        ELSE NULL
      END,
      d.id,
      d.name
    )
    FROM DeptMember dm
    JOIN dm.user u
    JOIN dm.department d
    LEFT JOIN u.profile up
    LEFT JOIN up.positionCode dc
    LEFT JOIN com.daouoffice.daouoffice.entity.AttachFile af ON af.id = up.photoId
    LEFT JOIN com.daouoffice.daouoffice.entity.HrCardBasic hr ON hr.userId = u.id
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY u.name ASC
  """)
    List<UserSearchDto> searchUsersByNameOrId(@Param("keyword") String keyword);

    // 사용자 상세 정보 조회
    @Query("""
    SELECT new com.daouoffice.daouoffice.dto.UserDetailDto(
      u.id,
      u.name,
      COALESCE(d.name, '미배정'),
      COALESCE(dc.koName, ''),
      CASE
        WHEN af.id IS NOT NULL AND af.length IS NOT NULL
        THEN CONCAT('https://gw.sghitech.co.kr/thumb/user/small/', af.id, '-', af.length)
        ELSE NULL
      END,
      CASE
        WHEN dm.id.departmentId IN (60,35) THEN ''
        ELSE CONCAT(COALESCE(u.loginId, ''), '@sghitech.co.kr')
      END,
      CASE WHEN LENGTH(up.directTel) = 3 THEN '' ELSE COALESCE(up.directTel, '') END,
      COALESCE(up.mobileNo, ''),
      hr.hireDate
    )
    FROM DeptMember dm
    JOIN dm.user u
    JOIN dm.department d
    LEFT JOIN u.profile up
    LEFT JOIN up.positionCode dc
    LEFT JOIN com.daouoffice.daouoffice.entity.AttachFile af ON af.id = up.photoId
    LEFT JOIN com.daouoffice.daouoffice.entity.HrCardBasic hr ON hr.userId = u.id
    WHERE u.id = :userId
      AND dm.id.departmentId = :deptId
  """)
    Optional<UserDetailDto> findUserDetailByIdAndDepartment(@Param("userId") Long userId, @Param("deptId") Long deptId);

    @Query("""
    SELECT new com.daouoffice.daouoffice.dto.UserDetailDto(
      u.id,
      u.name,
      COALESCE(d.name, '미배정'),
      COALESCE(dc.koName, ''),
      CASE
        WHEN af.id IS NOT NULL AND af.length IS NOT NULL
        THEN CONCAT('https://gw.sghitech.co.kr/thumb/user/small/', af.id, '-', af.length)
        ELSE NULL
      END,
      CASE
        WHEN dm.id.departmentId IN (60,35) THEN ''
        ELSE CONCAT(COALESCE(u.loginId, ''), '@sghitech.co.kr')
      END,
      CASE WHEN LENGTH(up.directTel) = 3 THEN '' ELSE COALESCE(up.directTel, '') END,
      COALESCE(up.mobileNo, ''),
      hr.hireDate
    )
    FROM DeptMember dm
    JOIN dm.user u
    JOIN dm.department d
    LEFT JOIN u.profile up
    LEFT JOIN up.positionCode dc
    LEFT JOIN com.daouoffice.daouoffice.entity.AttachFile af ON af.id = up.photoId
    LEFT JOIN com.daouoffice.daouoffice.entity.HrCardBasic hr ON hr.userId = u.id
    WHERE u.id = :userId
    ORDER BY dm.type ASC, COALESCE(dm.sortOrder, 2147483647) ASC, d.name ASC
  """)
    List<UserDetailDto> findUserDetailsById(@Param("userId") Long userId);
}
