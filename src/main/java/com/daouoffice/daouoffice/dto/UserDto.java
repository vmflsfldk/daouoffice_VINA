package com.daouoffice.daouoffice.dto;

public class UserDto {
    private Long userId;
    private String userName;
    private String positionName;
    private String photoUrl;
    private Integer sortOrder;
    private Integer type;
    private Long departmentId;
    private String departmentName;

    // 8개 파라미터 생성자 (기본)
    public UserDto(Long userId,
                   String userName,
                   String positionName,
                   String photoUrl,
                   Integer sortOrder,
                   Integer type,
                   Long departmentId,
                   String departmentName) {
        this.userId = userId;
        this.userName = userName;
        this.positionName = positionName;
        this.photoUrl = photoUrl;
        this.sortOrder = sortOrder;
        this.type = type;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    // 5개 파라미터 생성자 (JPQL용 - 백업)
    public UserDto(Long userId,
                   String userName,
                   String positionName,
                   String photoUrl,
                   Integer sortOrder) {
        this(userId,
             userName,
             positionName,
             photoUrl,
             sortOrder,
             1,
             null,
             null);
    }

    // getters & setters
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