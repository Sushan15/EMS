package com.pelatro.signup.serviceImp;



import com.pelatro.signup.entity.Department;
import com.pelatro.signup.repository.DepartmentRepository;
import com.pelatro.signup.service.DepartmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImp implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    // Get all departments
    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll(); // Fetch all departments from the repository
    }

   
}
