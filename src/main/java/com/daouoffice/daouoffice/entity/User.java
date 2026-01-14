package com.daouoffice.daouoffice.entity;

import javax.persistence.*;

@Entity
@Table(name = "go_users")
public class User {
    @Id
    private Long id;

    private String name;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "employee_number")
    private String employeeNumber;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private HrCardBasic hrCardBasic;

    // getters & setters
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

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public HrCardBasic getHrCardBasic() {
        return hrCardBasic;
    }

    public void setHrCardBasic(HrCardBasic hrCardBasic) {
        this.hrCardBasic = hrCardBasic;
    }
}
