package com.controller;

import com.model.Department;
import com.model.Employee;
import com.repository.DepartmentRepository;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public String listDepartments(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        return "departmentsList";
    }

    @GetMapping("/{id}")
    public String viewDepartment(@PathVariable Integer id, Model model) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent()) {
            // Récupérer tous les employés actifs du département
            List<Employee> employees = employeeRepository.findAll().stream()
                    .filter(e -> e.getDepartmentId() != null && e.getDepartmentId().equals(id) && e.isActive())
                    .collect(Collectors.toList());
            
            model.addAttribute("department", department.get());
            model.addAttribute("employees", employees);
            return "departmentMembers";
        }
        return "redirect:/department/list";
    }

    @GetMapping("/create/form")
    public String createForm(Model model) {
        model.addAttribute("department", new Department());
        return "addDepartment";
    }

    @PostMapping("/DepartmentCreateServlet")
    public String createDepartment(
            @RequestParam String name,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {

        Department dept = new Department();
        dept.setName(name);
        dept.setDescription(description);

        departmentRepository.save(dept);
        redirectAttributes.addFlashAttribute("message", "Département créé avec succès");
        return "redirect:/department/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "editDepartment";
        }
        return "redirect:/department/list";
    }

    @PostMapping("/{id}/update")
    public String updateDepartment(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {

        Optional<Department> departmentOpt = departmentRepository.findById(id);
        if (departmentOpt.isPresent()) {
            Department dept = departmentOpt.get();
            dept.setName(name);
            dept.setDescription(description);

            departmentRepository.save(dept);
            redirectAttributes.addFlashAttribute("message", "Département mis à jour avec succès");
        }

        return "redirect:/department/list";
    }

    @GetMapping("/remove/form")
    public String removeForm(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        return "deleteDepartment";
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        departmentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Département supprimé avec succès");
        return "redirect:/department/list";
    }
}
