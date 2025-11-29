package com.model;

import java.io.Serializable;
import java.util.Objects;

public class ProjectAssignmentId implements Serializable {
    private Integer projectId;
    private Integer employeeId;

    public ProjectAssignmentId() {}

    public ProjectAssignmentId(Integer projectId, Integer employeeId) {
        this.projectId = projectId;
        this.employeeId = employeeId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectAssignmentId that = (ProjectAssignmentId) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, employeeId);
    }
}
