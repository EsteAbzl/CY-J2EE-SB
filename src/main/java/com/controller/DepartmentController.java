package com.controller;

import com.model.Department;
import com.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

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
            model.addAttribute("department", department.get());
            return "departmentDetail";
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

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        departmentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Département supprimé avec succès");
        return "redirect:/department/list";
    }

    @GetMapping("/remove/form")
    public String removeForm(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        return "deleteDepartment";
    }

    @GetMapping("/remove/{id}")
    public String removeDepartment(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        try {
            departmentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Département supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du département");
        }
        return "redirect:/dashboard";
    }
}
