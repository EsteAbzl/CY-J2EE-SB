package com.controller;

import com.model.Project;
import com.model.Department;
import com.repository.ProjectRepository;
import com.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/list")
    public String listProjects(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        return "projectsList";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable Integer id, Model model) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "projectDetail";
        }
        return "redirect:/project/list";
    }

    @GetMapping("/create/form")
    public String createForm(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("project", new Project());
        return "addProject";
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
        project.setDepartmentId(department_id);

        if (start_date != null && !start_date.isBlank()) {
            project.setStartDate(Date.valueOf(start_date));
        }
        if (end_date != null && !end_date.isBlank()) {
            project.setEndDate(Date.valueOf(end_date));
        }

        projectRepository.save(project);
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
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setName(name);
            project.setDescription(description);
            project.setStatus(status);
            project.setDepartmentId(department_id);

            if (start_date != null && !start_date.isBlank()) {
                project.setStartDate(Date.valueOf(start_date));
            }
            if (end_date != null && !end_date.isBlank()) {
                project.setEndDate(Date.valueOf(end_date));
            }

            projectRepository.save(project);
            redirectAttributes.addFlashAttribute("message", "Projet mis à jour avec succès");
        }

        return "redirect:/project/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        projectRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Projet supprimé avec succès");
        return "redirect:/project/list";
    }
}
