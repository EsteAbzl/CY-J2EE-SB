package com.repository;

import com.model.SalaireExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaireExtraRepository extends JpaRepository<SalaireExtra, Integer> {
    List<SalaireExtra> findByEmployeeId(Integer employeeId);
    
    // Trouver les extras pour un employé dans une période mois/année
    @Query(value = "SELECT * FROM salaire_extra WHERE employee_id = :employeeId " +
                   "AND YEAR(date) = :year AND MONTH(date) = :month " +
                   "ORDER BY date ASC", nativeQuery = true)
    List<SalaireExtra> findByEmployeeAndYearMonth(@Param("employeeId") Integer employeeId, 
                                                   @Param("year") int year, 
                                                   @Param("month") int month);
}
