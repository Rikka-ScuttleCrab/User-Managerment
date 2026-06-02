package com.example.ManagerApp.domain;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.example.ManagerApp.domain.enumeration.Gender;
import com.example.ManagerApp.security.SecurityService;

public class User {

    private Long id;

    private String username;

    private String email;

    private String password;

    private Gender gender;

    private String nickname;

    private Set<Role> roles = new HashSet<>();

    private String refreshToken;

    private Instant createdAt;

    private String createdBy;

    private Instant updatedAt;

    private String updatedBy;

    private Boolean active;

    // Constructor rỗng
    public User() {
    }

    // Constructor đầy đủ
    public User(Long id,
                String username,
                String email,
                String password,
                Gender gender,
                String nickname,
                String refreshToken,
                Instant createdAt,
                String createdBy,
                Instant updatedAt,
                String updatedBy,
                Boolean active) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.nickname = nickname;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.active = active;
    }

    // =========================
    // HANDLE CREATE
    // =========================

    public void handleBeforeCreate() {

        this.createdAt = Instant.now();

        this.createdBy =
                SecurityService
                        .getCurrentUserLogin()
                        .orElse("");

        if (this.active == null) {
            this.active = true;
        }
    }

    // =========================
    // HANDLE UPDATE
    // =========================

    public void handleBeforeUpdate() {

        this.updatedAt = Instant.now();

        this.updatedBy =
                SecurityService
                        .getCurrentUserLogin()
                        .orElse("");
    }

    // =========================
    // GETTER SETTER
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}