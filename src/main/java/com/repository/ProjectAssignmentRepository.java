package com.repository;

import com.model.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Integer> {
    List<ProjectAssignment> findByEmployeeId(Integer employeeId);
    List<ProjectAssignment> findByProjectId(Integer projectId);
    void deleteByProjectIdAndEmployeeId(Integer projectId, Integer employeeId);
}
