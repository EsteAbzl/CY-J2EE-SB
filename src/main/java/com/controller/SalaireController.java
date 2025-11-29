package com.controller;

import com.model.Salaire;
import com.model.SalaireExtra;
import com.repository.SalaireRepository;
import com.repository.SalaireExtraRepository;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/salaire")
public class SalaireController {

    @Autowired
    private SalaireRepository salaireRepository;

    @Autowired
    private SalaireExtraRepository salaireExtraRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public String listSalaires(Model model) {
        List<Salaire> salaires = salaireRepository.findAll();
        model.addAttribute("salaires", salaires);
        return "salaire";
    }

    @PostMapping("/SalaireServlet")
    public String createSalaire(
            @RequestParam Integer employeeId,
            @RequestParam double salaire,
            @RequestParam String date,
            RedirectAttributes redirectAttributes) {

        Salaire sal = new Salaire();
        sal.setEmployeeId(employeeId);
        sal.setSalaire(salaire);
        sal.setDate(Date.valueOf(date));

        salaireRepository.save(sal);
        redirectAttributes.addFlashAttribute("message", "Salaire enregistré avec succès");
        return "redirect:/salaire/list";
    }

    @GetMapping("/extra/form")
    public String addExtraForm(@RequestParam(required = false) Integer employeeId, Model model) {
        model.addAttribute("selectedEmployeeId", employeeId);
        return "addSalaireExtra";
    }

    @PostMapping("/SalaireExtraServlet")
    public String createSalaireExtra(
            @RequestParam Integer employeeId,
            @RequestParam double montant,
            @RequestParam(required = false) String motif,
            @RequestParam String date,
            RedirectAttributes redirectAttributes) {

        SalaireExtra extra = new SalaireExtra();
        extra.setEmployeeId(employeeId);
        extra.setMontant(montant);
        extra.setMotif(motif != null ? motif : "");
        extra.setDate(Date.valueOf(date));

        salaireExtraRepository.save(extra);
        redirectAttributes.addFlashAttribute("message", "Extra salarial enregistré avec succès");
        return "redirect:/employee/list";
    }
}
