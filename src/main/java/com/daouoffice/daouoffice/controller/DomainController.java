package com.daouoffice.daouoffice.controller;

import com.daouoffice.daouoffice.entity.DomainCode;
import com.daouoffice.daouoffice.repository.DomainCodeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/org")
public class DomainController {
    private final DomainCodeRepository domainRepo;

    public DomainController(DomainCodeRepository domainRepo) {
        this.domainRepo = domainRepo;
    }

    /**
     * 전체 직급 목록을 JSON 으로 반환합니다.
     */
    @GetMapping("/positions")
    public List<DomainCode> getAllPositions() {
        return domainRepo.findAll();
    }
}
