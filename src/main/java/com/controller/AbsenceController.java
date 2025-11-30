package com.controller;

import com.model.Absence;
import com.model.Employee;
import com.service.EmployeeService;
import com.repository.AbsenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/absence")
public class AbsenceController {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    public String listAbsences(
            @RequestParam(required = false) Integer employeeId,
            Model model) {
        
        List<Absence> absences = (employeeId != null && employeeId > 0) 
            ? absenceRepository.findByEmployeeId(employeeId)
            : absenceRepository.findAll();
        
        absences.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        List<Employee> employees = employeeService.getActiveEmployees();

        model.addAttribute("absences", absences);
        model.addAttribute("employees", employees);
        model.addAttribute("selectedEmployeeId", employeeId);
        return "absencesList";
    }

    @GetMapping("/create/form")
    public String createForm(Model model) {
        model.addAttribute("absence", new Absence());
        return "absences";
    }

    @PostMapping("/AbsenceServlet")
    public String createAbsence(
            @RequestParam Integer employeeId,
            @RequestParam String date,
            @RequestParam String type,
            @RequestParam(defaultValue = "8") int hours,
            RedirectAttributes redirectAttributes) {

        Absence absence = new Absence();
        absence.setEmployeeId(employeeId);
        absence.setDate(Date.valueOf(date));
        absence.setType(type);
        absence.setHours(hours);

        absenceRepository.save(absence);
        redirectAttributes.addFlashAttribute("message", "Absence enregistrée avec succès");
        return "redirect:/absence/list";
    }

    @GetMapping("/{id}")
    public String viewAbsence(@PathVariable Integer id, Model model) {
        Optional<Absence> absence = absenceRepository.findById(id);
        if (absence.isPresent()) {
            model.addAttribute("absence", absence.get());
            return "absenceDetail";
        }
        return "redirect:/absence/list";
    }
}
