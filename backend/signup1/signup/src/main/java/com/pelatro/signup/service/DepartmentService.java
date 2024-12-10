package com.pelatro.signup.service;


import com.pelatro.signup.entity.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    List<Department> getAllDepartments();
//    Optional<Department> getDepartmentById(Long id);  // Add method to fetch department by ID
//    Department createDepartment(Department department);  // Add method to create a new department
//    Department updateDepartment(Long id, Department department);  // Add method to update department
//    void deleteDepartment(Long id);  // Add method to delete a department
}
