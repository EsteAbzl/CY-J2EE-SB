package com.service;

import com.model.Employee;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Retourne tous les employés actifs
     */
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findAll().stream()
                .filter(Employee::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les employés actifs d'un département
     */
    public List<Employee> getActiveEmployeesByDepartment(Integer departmentId) {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getDepartmentId() != null && e.getDepartmentId().equals(departmentId) && e.isActive())
                .collect(Collectors.toList());
    }

    /**
     * Recherche employés avec filtres
     */
    public List<Employee> searchEmployees(String query, String grade, String position, Integer department) {
        String queryParam = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        String gradeParam = (grade != null && !grade.trim().isEmpty()) ? grade.trim() : null;
        String positionParam = (position != null && !position.trim().isEmpty()) ? position.trim() : null;

        List<Employee> employees;
        if (queryParam != null || gradeParam != null || positionParam != null || department != null) {
            employees = employeeRepository.search(queryParam, gradeParam, positionParam, department);
        } else {
            employees = employeeRepository.findAll();
        }

        return employees.stream()
                .filter(Employee::isActive)
                .collect(Collectors.toList());
    }
}

