package com.controller;

import com.model.Salaire;
import com.model.SalaireExtra;
import com.util.SecurityUtil;
import com.repository.SalaireRepository;
import com.repository.SalaireExtraRepository;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

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
    public String listSalaires(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acc\u00e8s refus\u00e9");
            return "redirect:/permissionDenied";
        }
        List<Salaire> salaires = salaireRepository.findAll();
        model.addAttribute("salaires", salaires);
        return "salaire";
    }

    @PostMapping("/SalaireServlet")
    public String createSalaire(
            @RequestParam Integer employeeId,
            @RequestParam double salaire,
            @RequestParam String date,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }

        Salaire sal = new Salaire();
        sal.setEmployeeId(employeeId);
        sal.setSalaire(salaire);
        sal.setDate(Date.valueOf(date));

        salaireRepository.save(sal);
        redirectAttributes.addFlashAttribute("message", "Salaire enregistré avec succès");
        return "redirect:/salaire/list";
    }

    @GetMapping("/extra/form")
    public String showSalaireExtraForm(@RequestParam(required = false) Integer employeeId, Model model) {
        model.addAttribute("selectedEmployeeId", employeeId);
        return "addSalaireExtra";
    }

    @PostMapping("/SalaireExtraServlet")
    public String createSalaireExtra(
            @RequestParam Integer employeeId,
            @RequestParam double montant,
            @RequestParam(required = false) String motif,
            @RequestParam String date,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!SecurityUtil.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé");
            return "redirect:/permissionDenied";
        }

        // Vérifier que l'employé existe
        if (employeeRepository.findById(employeeId).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employé non trouvé");
            return "redirect:/employee/list";
        }

        // Valider le montant et la date
        if (montant == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le montant ne peut pas être zéro");
            return "redirect:/salaire/extra/form?employeeId=" + employeeId;
        }

        SalaireExtra extra = new SalaireExtra();
        extra.setEmployeeId(employeeId);
        extra.setMontant(montant);
        extra.setMotif(motif != null && !motif.trim().isEmpty() ? motif.trim() : "Sans description");
        extra.setDate(Date.valueOf(date));

        salaireExtraRepository.save(extra);
        
        String typeExtra = montant > 0 ? "bonus" : "malus";
        redirectAttributes.addFlashAttribute("message", 
            String.format("%s enregistré avec succès (%.2f €)", typeExtra, Math.abs(montant)));
        return "redirect:/employee/list";
    }
}
