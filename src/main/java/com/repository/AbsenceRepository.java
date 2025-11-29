package com.repository;

import com.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Integer> {
    List<Absence> findByEmployeeId(Integer employeeId);
}
