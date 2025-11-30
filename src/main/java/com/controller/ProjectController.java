package com.controller;

import com.model.Project;
import com.model.Department;
import com.model.Employee;
import com.model.ProjectAssignment;
import com.service.ProjectService;
import com.service.EmployeeService;
import com.repository.ProjectRepository;
import com.repository.DepartmentRepository;
import com.repository.EmployeeRepository;
import com.repository.ProjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectAssignmentRepository projectAssignmentRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    public String listProjects(Model model) {
        List<Project> projects = projectRepository.findAll();
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("projects", projects);
        model.addAttribute("departments", departments);
        return "projectsList";
    }

    @GetMapping("/create/form")
    public String createForm(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("project", new Project());
        return "addProject";
    }

    @GetMapping("/delete/form")
    public String deleteForm(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        return "deleteProject";
    }

    @GetMapping("/assign/form")
    public String assignForm(Model model) {
        List<Project> projects = projectRepository.findAll();
        List<Employee> employees = employeeService.getActiveEmployees();
        model.addAttribute("projects", projects);
        model.addAttribute("employees", employees);
        return "assignEmployee";
    }

    @GetMapping("/{id:\\d+}/assign")
    public String assignToProjectForm(@PathVariable Integer id, Model model) {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            return "redirect:/project/list";
        }
        
        List<Employee> employees = employeeService.getActiveEmployees();
        model.addAttribute("project", project.get());
        model.addAttribute("employees", employees);
        return "assignEmployeeToProject";
    }

    @PostMapping("/assign")
    public String assignEmployeeToProject(
            @RequestParam Integer project_id,
            @RequestParam Integer employee_id,
            @RequestParam String role_in_project,
            RedirectAttributes redirectAttributes) {
        ProjectAssignment assignment = new ProjectAssignment(project_id, employee_id, role_in_project);
        projectAssignmentRepository.save(assignment);
        redirectAttributes.addFlashAttribute("message", "Employé affecté au projet avec succès");
        return "redirect:/project/" + project_id;
    }

    @GetMapping("/remove/{ids}")
    public String deleteMultipleProjects(@PathVariable String ids, RedirectAttributes redirectAttributes) {
        String[] idArray = ids.split(",");
        int deletedCount = 0;
        for (String id : idArray) {
            try {
                projectRepository.deleteById(Integer.parseInt(id.trim()));
                deletedCount++;
            } catch (NumberFormatException e) {
                // Ignore invalid IDs
            }
        }
        redirectAttributes.addFlashAttribute("message", deletedCount + " projet(s) supprimé(s) avec succès");
        return "redirect:/project/list";
    }

    @GetMapping("/{id:\\d+}")
    public String viewProject(@PathVariable Integer id, Model model) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            
            // Récupérer les assignations du projet
            List<ProjectAssignment> assignments = projectAssignmentRepository.findByProjectId(id);
            
            // Récupérer les employés assignés
            List<Employee> members = assignments.stream()
                    .map(a -> employeeRepository.findById(a.getEmployeeId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            
            // Créer une map pour associer l'ID de l'employé à son rôle
            Map<Integer, String> projectAssignments = new java.util.LinkedHashMap<>();
            assignments.forEach(a -> projectAssignments.put(a.getEmployeeId(), a.getRoleInProject()));
            
            model.addAttribute("members", members);
            model.addAttribute("projectAssignments", projectAssignments);
            return "projectDetail";
        }
        return "redirect:/project/list";
    }

    @PostMapping("/ProjectCreateServlet")
    public String createProject(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) Integer department_id,
            RedirectAttributes redirectAttributes) {

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);
        
        projectService.saveProject(project, department_id, start_date, end_date);
        redirectAttributes.addFlashAttribute("message", "Projet créé avec succès");
        return "redirect:/project/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            List<Department> departments = departmentRepository.findAll();
            model.addAttribute("project", project.get());
            model.addAttribute("departments", departments);
            return "editProject";
        }
        return "redirect:/project/list";
    }

    @PostMapping("/{id}/update")
    public String updateProject(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) Integer department_id,
            RedirectAttributes redirectAttributes) {

        Optional<Project> projectOpt = projectRepository.findById(id);
        if (!projectOpt.isPresent()) {
            return "redirect:/project/list";
        }

        Project project = projectOpt.get();
        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);
        
        projectService.saveProject(project, department_id, start_date, end_date);
        redirectAttributes.addFlashAttribute("message", "Projet mis à jour avec succès");
        return "redirect:/project/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        projectRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Projet supprimé avec succès");
        return "redirect:/project/list";
    }
}
