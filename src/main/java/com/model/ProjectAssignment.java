package com.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_assignments")
@IdClass(ProjectAssignmentId.class)
public class ProjectAssignment {
    @Id
    @Column(name = "project_id")
    private Integer projectId;

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "role_in_project", length = 100)
    private String roleInProject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    public ProjectAssignment() {}

    public ProjectAssignment(Integer projectId, Integer employeeId, String roleInProject) {
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.roleInProject = roleInProject;
    }

    // Getters and Setters
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
