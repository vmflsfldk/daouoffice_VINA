package com.daouoffice.daouoffice.entity;

import javax.persistence.*;

@Entity
@Table(name = "go_attach_files")
public class AttachFile {
    @Id
    private Long id;

    // 실제 컬럼명이 `length` 라면,
    @Column(name = "length")
    private String length;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLength() { return length; }
    public void setLength(String length) { this.length = length; }
}
