package com.controller;

import com.model.Department;
import com.model.Employee;
import com.service.EmployeeService;
import com.util.SecurityUtil;
import com.repository.DepartmentRepository;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

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

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    public String listDepartments(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        return "departmentsList";
    }

    @GetMapping("/{id}")
    public String viewDepartment(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
        Optional<Department> department = departmentRepository.findById(id);
        if (!department.isPresent()) {
            return "redirect:/department/list";
        }
        
        List<Employee> employees = employeeService.getActiveEmployeesByDepartment(id);
        model.addAttribute("department", department.get());
        model.addAttribute("employees", employees);
        return "departmentMembers";
    }

    @GetMapping("/create/form")
    public String createForm(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
        model.addAttribute("department", new Department());
        return "addDepartment";
    }

    @PostMapping("/DepartmentCreateServlet")
    public String createDepartment(
            @RequestParam String name,
            @RequestParam String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }

        Department dept = new Department();
        dept.setName(name);
        dept.setDescription(description);

        departmentRepository.save(dept);
        redirectAttributes.addFlashAttribute("message", "Département créé avec succès");
        return "redirect:/department/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
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
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }

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
    public String removeForm(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        return "deleteDepartment";
    }

    @GetMapping("/remove/{ids}")
    public String deleteMultipleDepartments(@PathVariable String ids, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }
        String[] idArray = ids.split(",");
        int deletedCount = 0;
        
        for (String idStr : idArray) {
            try {
                Integer id = Integer.parseInt(idStr.trim());
                if (deleteDepartmentById(id)) {
                    deletedCount++;
                }
            } catch (NumberFormatException e) {
                // Ignorer les IDs invalides
            }
        }
        
        if (deletedCount > 0) {
            redirectAttributes.addFlashAttribute("message", deletedCount + " département(s) supprimé(s) avec succès");
        } else {
            redirectAttributes.addFlashAttribute("error", "Aucun département n'a pu être supprimé");
        }
        return "redirect:/department/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (deleteDepartmentById(id)) {
            redirectAttributes.addFlashAttribute("message", "Département supprimé avec succès");
        } else {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer le département");
        }
        return "redirect:/department/list";
    }

    /**
     * Helper method pour supprimer un département et désactiver ses employés
     */
    private boolean deleteDepartmentById(Integer id) {
        Optional<Department> departmentOpt = departmentRepository.findById(id);
        if (!departmentOpt.isPresent()) {
            return false;
        }

        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(e -> e.getDepartmentId() != null && e.getDepartmentId().equals(id))
                .collect(Collectors.toList());
        
        employees.forEach(emp -> {
            emp.setActive(false);
            employeeRepository.save(emp);
        });
        
        departmentRepository.deleteById(id);
        return true;
    }
}
