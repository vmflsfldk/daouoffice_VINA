package com.daouoffice.daouoffice.dto;

public class UserDetailsDto {
    private Long userId;
    private String name;
    private String photoUrl;
    private String departmentName;
    private String positionName;
    private String email;
    private String directTel;
    private String extension;
    private String mobileNo;
    private String joinDate;

    public UserDetailsDto(Long userId,
                          String name,
                          String photoUrl,
                          String departmentName,
                          String positionName,
                          String email,
                          String directTel,
                          String extension,
                          String mobileNo,
                          String joinDate) {
        this.userId = userId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.departmentName = departmentName;
        this.positionName = positionName;
        this.email = email;
        this.directTel = directTel;
        this.extension = extension;
        this.mobileNo = mobileNo;
        this.joinDate = joinDate;
    }
    
    public UserDetailsDto(long userId,
                          String name,
                          String photoUrl,
                          String departmentName,
                          String positionName,
                          String email,
                          String directTel,
                          String extension,
                          String mobileNo,
                          String joinDate) {
        this(Long.valueOf(userId),
             name,
             photoUrl,
             departmentName,
             positionName,
             email,
             directTel,
             extension,
             mobileNo,
             joinDate);
    }

    public UserDetailsDto(Long userId,
                          Object name,
                          Object photoUrl,
                          Object departmentName,
                          Object positionName,
                          Object email,
                          Object directTel,
                          Object extension,
                          Object mobileNo,
                          Object joinDate) {
        this(userId,
             toStringOrNull(name),
             toStringOrNull(photoUrl),
             toStringOrNull(departmentName),
             toStringOrNull(positionName),
             toStringOrNull(email),
             toStringOrNull(directTel),
             toStringOrNull(extension),
             toStringOrNull(mobileNo),
             toStringOrNull(joinDate));
    }

    private static String toStringOrNull(Object value) {
        return value != null ? value.toString() : null;
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

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
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