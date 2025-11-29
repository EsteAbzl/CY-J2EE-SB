package com.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_assignments")
public class ProjectAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "role_in_project", length = 100)
    private String roleInProject;

    public ProjectAssignment() {}

    public ProjectAssignment(Integer projectId, Integer employeeId, String roleInProject) {
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.roleInProject = roleInProject;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(String roleInProject) {
        this.roleInProject = roleInProject;
    }
}
