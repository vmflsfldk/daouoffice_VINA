package com.daouoffice.daouoffice.repository;

import com.daouoffice.daouoffice.dto.UserDetailsDto;
import com.daouoffice.daouoffice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT new com.daouoffice.daouoffice.dto.UserDetailsDto(
            u.id,
            u.name,
            CONCAT('https://gw.sghitech.co.kr/thumb/user/small/', af.id, '-', af.length),
            d.name,
            dc.koName,
            CASE
              WHEN dm.id.departmentId IN (60,35) THEN ''
              ELSE CONCAT(u.loginId, '@sghitech.co.kr')
            END,
            CASE WHEN LENGTH(up.directTel) = 3 THEN '' ELSE up.directTel END,
            CASE WHEN LENGTH(up.directTel) = 3 THEN up.directTel ELSE '' END,
            up.mobileNo,
            CASE
              WHEN hr.hireDate IS NOT NULL
                THEN CAST(FUNCTION('to_char', hr.hireDate, 'YYYY-MM-DD') AS string )
              ELSE NULL
            END
        )
        FROM User u
        JOIN DeptMember dm ON dm.user.id = u.id
        JOIN dm.department d
        LEFT JOIN u.profile up
        LEFT JOIN up.positionCode dc
        LEFT JOIN com.daouoffice.daouoffice.entity.AttachFile af ON af.id = up.photoId
        LEFT JOIN com.daouoffice.daouoffice.entity.HrCardBasic hr ON hr.userId = u.id
        WHERE dm.id.departmentId = :deptId
          AND u.id = :userId
    """)
    UserDetailsDto findUserDetailsByDeptAndUser(
            @Param("deptId") Long deptId,
            @Param("userId") Long userId
    );
}