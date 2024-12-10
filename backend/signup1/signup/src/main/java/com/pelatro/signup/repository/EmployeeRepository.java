package com.pelatro.signup.repository;

import com.pelatro.signup.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Find employees by department (example)
    List<Employee> findByDepartmentId(Long departmentId);
    
    @Query("SELECT e FROM Employee e WHERE e.isDeleted = FALSE")
    List<Employee> findAllActiveEmployees();
    // Find an active employee by ID
    @Query("SELECT e FROM Employee e WHERE e.isDeleted = FALSE AND e.id = :id")
    Optional<Employee> findByIdAndIsDeletedFalse(@Param("id") Long id);

    @Query("SELECT e FROM Employee e WHERE e.isDeleted = true")
    List<Employee> findDeletedEmployees();
    
    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Optional<Employee> findById(@Param("id") Long id);
}
