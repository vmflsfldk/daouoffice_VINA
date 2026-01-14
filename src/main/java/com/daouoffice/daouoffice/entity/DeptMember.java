package com.daouoffice.daouoffice.entity;

import javax.persistence.*;

@Entity
@Table(name = "go_dept_members")
public class DeptMember {
    @EmbeddedId
    private DeptMemberId id;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "type")
    private Integer type;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("departmentId")
    @JoinColumn(name = "department_id")
    private Department department;

    public DeptMember() {}

    public DeptMember(DeptMemberId id, Integer sortOrder, Integer type) {
        this.id = id;
        this.sortOrder = sortOrder;
        this.type = type;
    }

    // getter/setter
    public DeptMemberId getId() {
        return id;
    }

    public void setId(DeptMemberId id) {
        this.id = id;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}