package com.daouoffice.daouoffice.dto;

import java.util.ArrayList;
import java.util.List;

public class DeptNodeDto {
    private Long id;
    private String name;
    private Long parentId;
    private long memberCount;
    private List<DeptNodeDto> children = new ArrayList<>();

    public DeptNodeDto() {}

    // --- getters & setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public long getMemberCount() {
        return memberCount;
    }
    public void setMemberCount(long memberCount) {
        this.memberCount = memberCount;
    }

    public List<DeptNodeDto> getChildren() {
        return children;
    }
    public void setChildren(List<DeptNodeDto> children) {
        this.children = children;
    }
}