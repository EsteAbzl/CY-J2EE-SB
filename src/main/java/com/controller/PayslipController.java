package com.controller;

import com.model.Payslip;
import com.model.Employee;
import com.model.Salaire;
import com.model.SalaireExtra;
import com.model.PayslipDTO;
import com.repository.PayslipRepository;
import com.repository.EmployeeRepository;
import com.repository.SalaireRepository;
import com.repository.SalaireExtraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/payslip")
public class PayslipController {

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private SalaireRepository salaireRepository;
    
    @Autowired
    private SalaireExtraRepository salaireExtraRepository;

    @GetMapping("/list")
    public String listPayslips(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        
        List<Payslip> payslips = payslipRepository.findAll();
        
        // Filtrer par ID d'employé si fourni
        if (employeeId != null && employeeId > 0) {
            payslips = payslips.stream()
                    .filter(p -> p.getEmployeeId() == employeeId)
                    .collect(Collectors.toList());
        }
        
        // Appliquer les filtres de recherche par nom/prénom/email/ID
        if (query != null && !query.trim().isEmpty()) {
            String searchQuery = query.trim().toLowerCase();
            payslips = payslips.stream()
                    .filter(p -> {
                        // Chercher par ID d'employé (numérique)
                        try {
                            int searchId = Integer.parseInt(searchQuery);
                            if (p.getEmployeeId() == searchId) {
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            // N'est pas un nombre, chercher par nom/prénom
                        }
                        
                        Optional<Employee> emp = employeeRepository.findById(p.getEmployeeId());
                        if (emp.isPresent()) {
                            Employee e = emp.get();
                            String fullName = (e.getLastName() + " " + e.getFirstName()).toLowerCase();
                            return fullName.contains(searchQuery);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }
        
        if (year != null && year > 0) {
            payslips = payslips.stream()
                    .filter(p -> p.getPeriodYear() == year)
                    .collect(Collectors.toList());
        }
        
        if (month != null && month > 0 && month <= 12) {
            payslips = payslips.stream()
                    .filter(p -> p.getPeriodMonth() == month)
                    .collect(Collectors.toList());
        }
        
        // Enrichir avec les noms des employés
        List<PayslipDTO> payslipDTOs = payslips.stream()
            .map(p -> {
                Optional<Employee> emp = employeeRepository.findById(p.getEmployeeId());
                String employeeName = emp.isPresent() ? 
                    emp.get().getLastName() + " " + emp.get().getFirstName() : 
                    "Employé supprimé";
                return new PayslipDTO(p.getId(), p.getEmployeeId(), employeeName, 
                    p.getPeriodYear(), p.getPeriodMonth(), p.getBaseSalary(), 
                    p.getBonuses(), p.getDeductions(), p.getNetPay(), p.getGeneratedAt());
            })
            .collect(Collectors.toList());
        
        model.addAttribute("payslips", payslipDTOs);
        model.addAttribute("searchQuery", query);
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        
        return "payslipList";
    }

    @GetMapping("/{id}")
    public String viewPayslip(@PathVariable Integer id, Model model) {
        Optional<Payslip> payslip = payslipRepository.findById(id);
        if (payslip.isPresent()) {
            model.addAttribute("payslip", payslip.get());
            return "payslipDetail";
        }
        return "redirect:/payslip/list";
    }

    @GetMapping("/create/form")
    public String createForm(@RequestParam(required = false) Integer employeeId, Model model) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("payslip", new Payslip());
        
        // Si employeeId est fourni, pré-remplir l'employé
        if (employeeId != null) {
            model.addAttribute("selectedEmployeeId", employeeId);
        }
        
        return "generatePayslip";
    }
    
    @GetMapping("/api/data")
    @ResponseBody
    public ResponseEntity<?> getPayslipData(
            @RequestParam Integer employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Vérifier que l'employé existe
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (!employee.isPresent()) {
                response.put("success", false);
                response.put("message", "Employé non trouvé");
                return ResponseEntity.ok(response);
            }
            
            // Créer une date à partir de l'année et du mois (dernier jour du mois)
            Date targetDate = Date.valueOf(String.format("%d-%02d-%02d", year, month, 
                    getDaysInMonth(month, year)));
            
            // Chercher le salaire le plus proche dans le passé
            List<Salaire> salaires = salaireRepository.findMostRecentSalaireBefore(employeeId, targetDate);
            
            if (salaires.isEmpty()) {
                response.put("success", false);
                response.put("message", "Aucun salaire trouvé pour cet employé avant cette date");
                return ResponseEntity.ok(response);
            }
            
            double baseSalary = salaires.get(0).getSalaire();
            
            // Chercher les bonus et déductions pour ce mois/année
            List<SalaireExtra> extras = salaireExtraRepository.findByEmployeeAndYearMonth(employeeId, year, month);
            
            double bonuses = 0;
            double deductions = 0;
            
            for (SalaireExtra extra : extras) {
                if (extra.getMontant() > 0) {
                    bonuses += extra.getMontant();
                } else {
                    deductions += Math.abs(extra.getMontant());
                }
            }
            
            double netPay = baseSalary + bonuses - deductions;
            
            response.put("success", true);
            response.put("baseSalary", baseSalary);
            response.put("bonuses", bonuses);
            response.put("deductions", deductions);
            response.put("netPay", netPay);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    private int getDaysInMonth(int month, int year) {
        switch(month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
            default:
                return 28;
        }
    }

    @PostMapping("/PayslipCreateServlet")
    public String createPayslip(
            @RequestParam Integer employeeId,
            @RequestParam int periodYear,
            @RequestParam int periodMonth,
            @RequestParam double baseSalary,
            @RequestParam(defaultValue = "0") double bonuses,
            @RequestParam(defaultValue = "0") double deductions,
            @RequestParam double netPay,
            RedirectAttributes redirectAttributes) {

        Payslip payslip = new Payslip();
        payslip.setEmployeeId(employeeId);
        payslip.setPeriodYear(periodYear);
        payslip.setPeriodMonth(periodMonth);
        payslip.setBaseSalary(baseSalary);
        payslip.setBonuses(bonuses);
        payslip.setDeductions(deductions);
        payslip.setNetPay(netPay);
        payslip.setGeneratedAt(new Timestamp(System.currentTimeMillis()));

        payslipRepository.save(payslip);
        redirectAttributes.addFlashAttribute("message", "Bulletin de paie créé avec succès");
        return "redirect:/payslip/list";
    }

    @GetMapping("/{id}/print")
    public String printPayslip(@PathVariable Integer id, Model model) {
        Optional<Payslip> payslip = payslipRepository.findById(id);
        if (payslip.isPresent()) {
            model.addAttribute("payslip", payslip.get());
            return "payslipPrint";
        }
        return "redirect:/payslip/list";
    }

    @GetMapping("/{id}/view")
    public String viewPayslipDetail(@PathVariable Integer id, Model model) {
        Optional<Payslip> payslip = payslipRepository.findById(id);
        if (payslip.isPresent()) {
            Payslip p = payslip.get();
            
            // Récupérer l'employé
            Optional<Employee> employee = employeeRepository.findById(p.getEmployeeId());
            String employeeName = employee.isPresent() ? 
                employee.get().getLastName() + " " + employee.get().getFirstName() : 
                "Employé supprimé";
            
            // Récupérer les détails des bonus et déductions
            List<SalaireExtra> extras = salaireExtraRepository.findByEmployeeAndYearMonth(
                p.getEmployeeId(), p.getPeriodYear(), p.getPeriodMonth());
            
            List<Map<String, Object>> bonusList = new java.util.ArrayList<>();
            List<Map<String, Object>> deductionsList = new java.util.ArrayList<>();
            
            for (SalaireExtra extra : extras) {
                Map<String, Object> item = new java.util.HashMap<>();
                if (extra.getMontant() > 0) {
                    item.put("montant", String.format("%.2f", extra.getMontant()));
                    item.put("motif", extra.getMotif() != null ? extra.getMotif() : "");
                    bonusList.add(item);
                } else {
                    item.put("montant", String.format("%.2f", Math.abs(extra.getMontant())));
                    item.put("motif", extra.getMotif() != null ? extra.getMotif() : "");
                    deductionsList.add(item);
                }
            }
            
            model.addAttribute("payslip", p);
            model.addAttribute("employeeName", employeeName);
            model.addAttribute("bonuses", bonusList);
            model.addAttribute("deductions", deductionsList);
            
            return "payslipDetail";
        }
        return "redirect:/payslip/list";
    }
}
