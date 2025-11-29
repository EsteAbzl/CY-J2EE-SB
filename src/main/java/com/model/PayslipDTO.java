package com.model;

import java.sql.Timestamp;

public class PayslipDTO {
    private Integer id;
    private int employeeId;
    private String employeeName;
    private int periodYear;
    private int periodMonth;
    private double baseSalary;
    private double bonuses;
    private double deductions;
    private double netPay;
    private Timestamp generatedAt;

    public PayslipDTO(Integer id, int employeeId, String employeeName, int periodYear, int periodMonth,
                      double baseSalary, double bonuses, double deductions, double netPay, Timestamp generatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.periodYear = periodYear;
        this.periodMonth = periodMonth;
        this.baseSalary = baseSalary;
        this.bonuses = bonuses;
        this.deductions = deductions;
        this.netPay = netPay;
        this.generatedAt = generatedAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(int periodYear) {
        this.periodYear = periodYear;
    }

    public int getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(int periodMonth) {
        this.periodMonth = periodMonth;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public double getBonuses() {
        return bonuses;
    }

    public void setBonuses(double bonuses) {
        this.bonuses = bonuses;
    }

    public double getDeductions() {
        return deductions;
    }

    public void setDeductions(double deductions) {
        this.deductions = deductions;
    }

    public double getNetPay() {
        return netPay;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    public Timestamp getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Timestamp generatedAt) {
        this.generatedAt = generatedAt;
    }
}
