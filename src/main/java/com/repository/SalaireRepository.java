package com.repository;

import com.model.Salaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface SalaireRepository extends JpaRepository<Salaire, Integer> {
    List<Salaire> findByEmployeeId(Integer employeeId);
    
    @Query("SELECT s FROM Salaire s WHERE s.employeeId = :employeeId AND s.date <= :targetDate ORDER BY s.date DESC")
    List<Salaire> findMostRecentSalaireBefore(@Param("employeeId") Integer employeeId, @Param("targetDate") Date targetDate);
    
    @Query("SELECT s FROM Salaire s WHERE s.employeeId = :employeeId AND YEAR(s.date) = :year AND MONTH(s.date) = :month ORDER BY s.date DESC")
    List<Salaire> findByEmployeeAndYearMonth(@Param("employeeId") Integer employeeId, @Param("year") int year, @Param("month") int month);
}
