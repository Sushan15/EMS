package com.pelatro.signup.repository;

import com.pelatro.signup.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Custom query method (example): Find a department by name
    Optional<Department> findByName(String name);
}
