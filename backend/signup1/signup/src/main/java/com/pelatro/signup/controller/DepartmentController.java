package com.pelatro.signup.controller;

import com.pelatro.signup.response.ApiResponse;
import com.pelatro.signup.entity.Department;
import com.pelatro.signup.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "http://localhost:4200")
//@PreAuthorize("hasRole('USER')") // Ensure only authenticated users with 'USER' role can access
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // Get all departments
    @GetMapping
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        ApiResponse<List<Department>> response = new ApiResponse<>(
                departments,
                "Departments fetched successfully",
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Other department-related endpoints can be added here as needed
}
