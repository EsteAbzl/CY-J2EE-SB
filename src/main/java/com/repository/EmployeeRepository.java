package com.repository;

import com.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmail(String email);
    
    List<Employee> findByDepartmentId(Integer departmentId);
    
    @Query("SELECT DISTINCT e.grade FROM Employee e WHERE e.grade IS NOT NULL")
    List<String> findDistinctGrades();
    
    @Query("SELECT DISTINCT e.positionTitle FROM Employee e WHERE e.positionTitle IS NOT NULL")
    List<String> findDistinctPositions();
    
    @Query("SELECT e FROM Employee e WHERE " +
           "(:query IS NULL OR e.firstName LIKE %:query% OR e.lastName LIKE %:query% OR e.email LIKE %:query% OR CONCAT(e.firstName, ' ', e.lastName) LIKE %:query% OR CONCAT(e.lastName, ' ', e.firstName) LIKE %:query%) AND " +
           "(:grade IS NULL OR e.grade = :grade) AND " +
           "(:position IS NULL OR e.positionTitle = :position) AND " +
           "(:departmentId IS NULL OR e.departmentId = :departmentId) " +
           "ORDER BY e.lastName, e.firstName")
    List<Employee> search(@Param("query") String query, 
                         @Param("grade") String grade, 
                         @Param("position") String position, 
                         @Param("departmentId") Integer departmentId);
    
    boolean existsByEmail(String email);
}
