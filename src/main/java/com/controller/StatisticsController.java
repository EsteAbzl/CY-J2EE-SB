package com.controller;

import com.model.Employee;
import com.util.SecurityUtil;
import com.repository.EmployeeRepository;
import com.repository.DepartmentRepository;
import com.repository.ProjectRepository;
import com.repository.ProjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectAssignmentRepository projectAssignmentRepository;

    @GetMapping("")
    public String statistics(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(Employee::isActive)
                .collect(Collectors.toList());

        // 1. Employés par département
        Map<String, Integer> employeesByDept = new LinkedHashMap<>();
        departmentRepository.findAll().forEach(dept -> {
            long count = employees.stream()
                    .filter(e -> e.getDepartmentId() != null && e.getDepartmentId().equals(dept.getId()))
                    .count();
            employeesByDept.put(dept.getName(), (int) count);
        });

        // 2. Employés par projet (via project_assignments)
        Map<String, Integer> employeesByProject = new LinkedHashMap<>();
        projectRepository.findAll().forEach(project -> {
            long count = projectAssignmentRepository.findByProjectId(project.getId()).size();
            if (count > 0) {
                employeesByProject.put(project.getName(), (int) count);
            }
        });

        // 3. Employés par grade
        Map<String, Integer> employeesByGrade = employees.stream()
                .filter(e -> e.getGrade() != null && !e.getGrade().isEmpty())
                .collect(Collectors.groupingBy(
                        Employee::getGrade,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        model.addAttribute("employeesByDept", employeesByDept);
        model.addAttribute("employeesByProject", employeesByProject);
        model.addAttribute("employeesByGrade", employeesByGrade);

        // Convertir en JSON pour les charts
        model.addAttribute("deptLabels", new ArrayList<>(employeesByDept.keySet()));
        model.addAttribute("deptData", new ArrayList<>(employeesByDept.values()));

        model.addAttribute("projectLabels", new ArrayList<>(employeesByProject.keySet()));
        model.addAttribute("projectData", new ArrayList<>(employeesByProject.values()));

        model.addAttribute("gradeLabels", new ArrayList<>(employeesByGrade.keySet()));
        model.addAttribute("gradeData", new ArrayList<>(employeesByGrade.values()));

        return "statistics";
    }
}

