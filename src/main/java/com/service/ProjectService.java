package com.service;

import com.model.Project;
import com.model.Department;
import com.repository.ProjectRepository;
import com.repository.DepartmentRepository;
import com.repository.ProjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectAssignmentRepository projectAssignmentRepository;

    /**
     * Crée ou met à jour un projet avec les paramètres fournis
     */
    public void saveProject(Project project, Integer departmentId, String startDate, String endDate) {
        if (departmentId != null) {
            Optional<Department> dept = departmentRepository.findById(departmentId);
            dept.ifPresent(project::setDepartment);
        }

        if (startDate != null && !startDate.isBlank()) {
            project.setStartDate(Date.valueOf(startDate));
        }
        
        if (endDate != null && !endDate.isBlank()) {
            project.setEndDate(Date.valueOf(endDate));
        }

        projectRepository.save(project);
    }

    /**
     * Supprime un employé d'un projet
     */
    @Transactional
    public void removeMemberFromProject(Integer projectId, Integer memberId) {
        projectAssignmentRepository.deleteByProjectIdAndEmployeeId(projectId, memberId);
    }
}
