package com.controller;

import com.model.Payslip;
import com.model.Employee;
import com.repository.PayslipRepository;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payslip")
public class PayslipController {

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public String listPayslips(Model model) {
        List<Payslip> payslips = payslipRepository.findAll();
        model.addAttribute("payslips", payslips);
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
    public String createForm(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("payslip", new Payslip());
        return "generatePayslip";
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
}
