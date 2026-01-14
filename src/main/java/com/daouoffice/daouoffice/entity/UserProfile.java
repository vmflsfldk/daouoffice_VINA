package com.daouoffice.daouoffice.entity;

import javax.persistence.*;

@Entity
@Table(name = "go_user_profiles")
public class UserProfile {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "photo_id")
    private Long photoId;

    @Column(name = "direct_tel")
    private String directTel;

    @Column(name = "mobile_no")
    private String mobileNo;

    // User와의 관계 설정
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    // DomainCode와의 관계 설정
    @ManyToOne
    @JoinColumn(name = "position_id", insertable = false, updatable = false)
    private DomainCode positionCode;

    public UserProfile() {}

    // getters & setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPositionId() {
        return positionId;
    }
    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getPhotoId() {
        return photoId;
    }
    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
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

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public DomainCode getPositionCode() {
        return positionCode;
    }
    public void setPositionCode(DomainCode positionCode) {
        this.positionCode = positionCode;
    }
}