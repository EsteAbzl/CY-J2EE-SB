package com.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 30)
    private String username;

    @Column(length = 30)
    private String passwordHash;

    @Column(length = 30)
    private String fullName;

    @Column(length = 30)
    private Integer roleId;

    @Column(length = 30)
    private boolean active;

    @Column(length = 30)
    private Integer employeeId;

    @Column(name = "first_connexion")
    private boolean firstConnexion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isFirstConnexion() {
        return firstConnexion;
    }

    public void setFirstConnexion(boolean firstConnexion) {
        this.firstConnexion = firstConnexion;
    }
}
