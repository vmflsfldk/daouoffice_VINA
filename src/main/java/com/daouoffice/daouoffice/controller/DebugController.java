package com.daouoffice.daouoffice.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final JdbcTemplate jdbc;

    public DebugController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 전체 직급 테이블 내용 확인 */
    @GetMapping("/domain-codes")
    public List<Map<String, Object>> allDomainCodes() {
        return jdbc.queryForList("SELECT * FROM go_domain_code ORDER BY id");
    }

    /** 전체 프로필 테이블 내용 확인 */
    @GetMapping("/user-profiles")
    public List<Map<String, Object>> allUserProfiles() {
        return jdbc.queryForList("SELECT * FROM go_user_profiles ORDER BY id");
    }

    /** 부서별 go_dept_members 전체 행 확인 */
    @GetMapping("/dept-members")
    public List<Map<String, Object>> allDeptMembers(@RequestParam Long deptId) {
        return jdbc.queryForList(
                "SELECT user_id, department_id, sort_order " +
                        "FROM go_dept_members " +
                        "WHERE department_id = ? " +
                        "ORDER BY sort_order NULLS LAST, user_id",
                deptId
        );
    }
}
