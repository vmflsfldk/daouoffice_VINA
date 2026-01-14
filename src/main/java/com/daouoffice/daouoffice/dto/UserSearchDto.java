package com.daouoffice.daouoffice.dto;

public class UserSearchDto {
    private Long userId;
    private String userName;
    private String positionName;
    private String photoUrl;
    private Long departmentId;
    private String departmentName;

    public UserSearchDto(Long userId,
                         String userName,
                         String positionName,
                         String photoUrl,
                         Long departmentId,
                         String departmentName) {
        this.userId = userId;
        this.userName = userName;
        this.positionName = positionName;
        this.photoUrl = photoUrl;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    // --- getters & setters ---
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
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

    public Long getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}