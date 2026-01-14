package com.daouoffice.daouoffice.entity;

import javax.persistence.*;

@Entity
@Table(name = "go_domain_codes")
public class DomainCode {
    @Id
    private Long id;

    @Column(name = "ko_name")
    private String koName;

    @Column(name = "sort_order")      // ← 이 컬럼 매핑
    private Integer sortOrder;

    public DomainCode() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKoName() { return koName; }
    public void setKoName(String koName) { this.koName = koName; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
