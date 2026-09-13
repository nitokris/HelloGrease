package com.nitokrisalpha.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Entity
@Table(name = "jav_works")
public class JavWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 5000)
    private String magnet;

    @Column
    private String code = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CREATED;

    @Column(length = 5000)
    private String hash;

    @Column
    private String javUid = "";

    public JavWork() {

    }

    public JavWork(String name, String magnet, String code) {
        this.name = name;
        this.magnet = magnet;
        hash = extractHashFromMagnet(magnet);
        this.code = code;
    }

    public String getHash() {
        return hash;
    }

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

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static String extractHashFromMagnet(String magnet) {
        if (magnet == null || magnet.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("urn:btih:([^&]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(magnet);
        return matcher.find() ? matcher.group(1) : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getJavUid() {
        return javUid;
    }

    public void setJavUid(String javUid) {
        this.javUid = javUid;
    }
}
