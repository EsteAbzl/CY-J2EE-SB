package com.controller;

import com.model.Salaire;
import com.model.SalaireExtra;
import com.repository.SalaireRepository;
import com.repository.SalaireExtraRepository;
import com.util.RequirePermission;
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

    @GetMapping("/list")
    public String listSalaires(Model model) {
        List<Salaire> salaires = salaireRepository.findAll();
        model.addAttribute("salaires", salaires);
        return "salaire";
    }

    @PostMapping("/SalaireServlet")
    @RequirePermission(allowedDepartments = {1}, deniedPage = "permissionDenied", notLoggedPage = "Login")
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

    @GetMapping("/extra/list")
    public String listSalaireExtra(Model model) {
        List<SalaireExtra> extras = salaireExtraRepository.findAll();
        model.addAttribute("extras", extras);
        return "salaireExtra";
    }

    @PostMapping("/SalaireExtraServlet")
    @RequirePermission(allowedDepartments = {1}, deniedPage = "permissionDenied", notLoggedPage = "Login")
    public String createSalaireExtra(
            @RequestParam Integer employeeId,
            @RequestParam double montant,
            @RequestParam String motif,
            @RequestParam String date,
            RedirectAttributes redirectAttributes) {

        SalaireExtra extra = new SalaireExtra();
        extra.setEmployeeId(employeeId);
        extra.setMontant(montant);
        extra.setMotif(motif);
        extra.setDate(Date.valueOf(date));

        salaireExtraRepository.save(extra);
        redirectAttributes.addFlashAttribute("message", "Salaire extra enregistré avec succès");
        return "redirect:/salaire/extra/list";
    }
}
