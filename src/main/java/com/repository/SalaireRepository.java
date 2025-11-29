package com.repository;

import com.model.Salaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaireRepository extends JpaRepository<Salaire, Integer> {
    List<Salaire> findByEmployeeId(Integer employeeId);
}
