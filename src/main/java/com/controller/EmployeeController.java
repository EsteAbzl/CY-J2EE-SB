package com.controller;

import com.model.Employee;
import com.model.Department;
import com.repository.EmployeeRepository;
import com.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/list")
    public String listEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer department,
            Model model) {

        List<Employee> employees;
        
        if (query != null || grade != null || position != null || department != null) {
            employees = employeeRepository.search(query, grade, position, department);
        } else {
            employees = employeeRepository.findAll();
        }

        List<String> grades = employeeRepository.findDistinctGrades();
        List<String> positions = employeeRepository.findDistinctPositions();
        List<Department> departments = departmentRepository.findAll();

        model.addAttribute("employees", employees);
        model.addAttribute("grades", grades);
        model.addAttribute("positions", positions);
        model.addAttribute("departments", departments);
        if (query != null) {
            model.addAttribute("searchQuery", query);
        }

        return "employeesList";
    }

    @GetMapping("/{id}")
    public String viewEmployee(@PathVariable Integer id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "employeeDetail";
        }
        return "redirect:/employee/list";
    }

    @GetMapping("/create/form")
    public String createForm(Model model) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("employee", new Employee());
        return "addEmployee";
    }

    @PostMapping("/EmployeeCreateServlet")
    public String createEmployee(
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String email,
            @RequestParam String grade,
            @RequestParam String position_title,
            @RequestParam double base_salary,
            @RequestParam(required = false) Integer department_id,
            @RequestParam(required = false) String hire_day,
            @RequestParam(required = false) String hire_month,
            @RequestParam(required = false) String hire_year,
            RedirectAttributes redirectAttributes) {

        Employee emp = new Employee();
        emp.setFirstName(first_name);
        emp.setLastName(last_name);
        emp.setEmail(email);
        emp.setGrade(grade);
        emp.setPositionTitle(position_title);
        emp.setBaseSalary(base_salary);
        emp.setDepartmentId(department_id != null ? department_id : 0);
        emp.setActive(true);

        employeeRepository.save(emp);

        redirectAttributes.addFlashAttribute("message", "Employé créé avec succès");
        return "redirect:/employee/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            List<Department> departments = departmentRepository.findAll();
            model.addAttribute("employee", employee.get());
            model.addAttribute("departments", departments);
            return "editEmployee";
        }
        return "redirect:/employee/list";
    }

    @PostMapping("/{id}/update")
    public String updateEmployee(
            @PathVariable Integer id,
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String email,
            @RequestParam String grade,
            @RequestParam String position_title,
            @RequestParam double base_salary,
            @RequestParam(required = false) Integer department_id,
            RedirectAttributes redirectAttributes) {

        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get();
            emp.setFirstName(first_name);
            emp.setLastName(last_name);
            emp.setEmail(email);
            emp.setGrade(grade);
            emp.setPositionTitle(position_title);
            emp.setBaseSalary(base_salary);
            emp.setDepartmentId(department_id);

            employeeRepository.save(emp);
            redirectAttributes.addFlashAttribute("message", "Employé mis à jour avec succès");
        }

        return "redirect:/employee/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        employeeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Employé supprimé avec succès");
        return "redirect:/employee/list";
    }
}
