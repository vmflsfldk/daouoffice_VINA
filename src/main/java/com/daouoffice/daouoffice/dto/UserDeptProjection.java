package com.daouoffice.daouoffice.dto;

public interface UserDeptProjection {
    Long getUserId();

    String getUserName();

    String getPositionName();

    String getPhotoUrl();

    Integer getSortOrder();

    Integer getType();

    Long getDepartmentId();

    String getDepartmentName();
}
