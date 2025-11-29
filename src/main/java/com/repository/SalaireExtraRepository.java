package com.repository;

import com.model.SalaireExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaireExtraRepository extends JpaRepository<SalaireExtra, Integer> {
    List<SalaireExtra> findByEmployeeId(Integer employeeId);
}
