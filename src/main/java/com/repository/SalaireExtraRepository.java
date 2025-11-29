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
    
    @Query("SELECT s FROM SalaireExtra s WHERE s.employeeId = :employeeId AND YEAR(s.date) = :year AND MONTH(s.date) = :month ORDER BY s.date ASC")
    List<SalaireExtra> findByEmployeeAndYearMonth(@Param("employeeId") Integer employeeId, @Param("year") int year, @Param("month") int month);
}
