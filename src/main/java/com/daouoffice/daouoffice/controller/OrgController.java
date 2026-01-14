package com.daouoffice.daouoffice.controller;

import com.daouoffice.daouoffice.dto.DeptNodeDto;
import com.daouoffice.daouoffice.dto.UserDto;
import com.daouoffice.daouoffice.dto.UserDetailDto;
import com.daouoffice.daouoffice.repository.DeptMemberRepository;
import com.daouoffice.daouoffice.service.DepartmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/org")
public class OrgController {
    private static final Logger logger = LoggerFactory.getLogger(OrgController.class);

    private final DepartmentService deptService;
    private final DeptMemberRepository memberRepo;

    public OrgController(DepartmentService deptService,
                         DeptMemberRepository memberRepo) {
        this.deptService = deptService;
        this.memberRepo = memberRepo;
    }

    // 조직 트리
    @GetMapping("/tree")
    public List<DeptNodeDto> getTree() {
        return deptService.buildTreeWithTotal();
    }

    // 특정 부서 사용자
    @GetMapping("/departments/{id}/users")
    public List<UserDto> getUsers(@PathVariable("id") Long deptId) {
        return memberRepo.findUsersByDepartment(deptId);
    }

    // 사용자 상세 정보 조회
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailDto> getUserDetail(
            @PathVariable("id") Long userId,
            @RequestParam(value = "departmentId", required = false) Long departmentId) {
        logger.info("사용자 상세 정보 조회 요청: userId = {}, departmentId = {}", userId, departmentId);

        try {
            if (departmentId != null) {
                var detailWithDept = memberRepo.findUserDetailByIdAndDepartment(userId, departmentId);
                if (detailWithDept.isPresent()) {
                    logger.info("사용자 상세 정보 조회 성공 (부서 지정): {}", detailWithDept.get());
                    return ResponseEntity.ok(detailWithDept.get());
                }

                logger.warn("지정한 부서에 사용자 소속 정보가 없음: userId = {}, departmentId = {}", userId, departmentId);
            }

            var userDetails = memberRepo.findUserDetailsById(userId);
            if (!userDetails.isEmpty()) {
                logger.info("사용자 상세 정보 조회 성공 (첫 번째 소속 반환): {}", userDetails.get(0));
                return ResponseEntity.ok(userDetails.get(0));
            }

            logger.warn("사용자를 찾을 수 없음: userId = {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("사용자 상세 정보 조회 중 오류 발생: userId = {}, error = {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}