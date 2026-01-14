package com.daouoffice.daouoffice.controller;

import com.daouoffice.daouoffice.dto.UserSearchDto;
import com.daouoffice.daouoffice.repository.DeptMemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final DeptMemberRepository memberRepo;

    public SearchController(DeptMemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    /**
     * 이름 또는 ID로 사용자 검색
     * @param keyword 검색 키워드 (이름 또는 ID)
     * @return 검색된 사용자 목록
     */
    @GetMapping("/users")
    public List<UserSearchDto> searchUsers(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return memberRepo.searchUsersByNameOrId(keyword.trim());
    }
}