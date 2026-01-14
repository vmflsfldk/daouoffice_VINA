package com.daouoffice.daouoffice.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserDetailDto {
    private Long userId;
    private String name;
    private String departmentName;
    private String positionName;
    private String photoUrl;
    private String email;
    private String directTel;
    private String mobileNo;
    private String joinDate;

    private static final DateTimeFormatter JOIN_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserDetailDto(Long userId,
                         String name,
                         String departmentName,
                         String positionName,
                         String photoUrl,
                         String email,
                         String directTel,
                         String mobileNo,
                         LocalDate joinDate) {
        this.userId = userId;
        this.name = name;
        this.departmentName = departmentName;
        this.positionName = positionName;
        this.photoUrl = photoUrl;
        this.email = email;
        this.directTel = directTel;
        this.mobileNo = mobileNo;
        this.joinDate = joinDate != null ? joinDate.format(JOIN_DATE_FORMATTER) : "";
    }

    // getters & setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPositionName() {
        return positionName;
    }
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDirectTel() {
        return directTel;
    }
    public void setDirectTel(String directTel) {
        this.directTel = directTel;
    }

    public String getMobileNo() {
        return mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getJoinDate() {
        return joinDate;
    }
    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}