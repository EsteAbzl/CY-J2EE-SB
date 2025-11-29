package com.repository;

import com.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Integer> {
    List<Payslip> findByEmployeeId(Integer employeeId);
    
    Optional<Payslip> findByEmployeeIdAndPeriodYearAndPeriodMonth(Integer employeeId, int periodYear, int periodMonth);
}
