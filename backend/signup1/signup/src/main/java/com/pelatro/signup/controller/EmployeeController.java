package com.pelatro.signup.controller;

import com.pelatro.signup.response.ApiResponse;
import com.pelatro.signup.entity.Department;
import com.pelatro.signup.entity.Employee;
import com.pelatro.signup.service.EmployeeService;
import com.pelatro.signup.service.WorkingHoursFileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:4200")
//@PreAuthorize("hasRole('USER')") // Ensure only authenticated users with 'USER' role can access
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    private static final Logger logger = LogManager.getLogger(EmployeeController.class);
    

    // Get all employees
    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        ApiResponse<List<Employee>> response = new ApiResponse<>(
                employees,
                "Employees fetched successfully",
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get an employee by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeById(@PathVariable("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        ApiResponse<Employee> response = new ApiResponse<>(
                employee,
                "Employee fetched successfully",
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Create a new employee
//    @PostMapping
//    public ResponseEntity<ApiResponse<Employee>> createEmployee(@RequestBody Employee employee) {
//        Employee savedEmployee = employeeService.createEmployee(employee);
//        ApiResponse<Employee> response = new ApiResponse<>(
//                savedEmployee,
//                "Employee created successfully",
//                HttpStatus.CREATED.value()
//        );
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> createEmployee(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("emailId") String emailId,
            @RequestParam("role") String role,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("departmentName") String departmentName,// Now department is passed as ID
            @RequestParam("phoneNumber") String phoneNumber,  // Phone number passed from frontend
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture  // Optional profile picture
    ) {
        try {
//        	if (profilePicture != null && !profilePicture.isEmpty()) {
//                logger.info("profile picture is there");
//            } else {
//            	 logger.info("No profile picture");
//            }
            // Fetch the department based on the department ID
            Department department = new Department( departmentId, departmentName);
            
            // If department is not found, return an error response
            if (department == null) {
                ApiResponse<Employee> response = new ApiResponse<>(null, "Department not found", 404);
                return ResponseEntity.status(404).body(response);
            }
 
            // Create a new employee object
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmailId(emailId);
            employee.setRole(role);
            employee.setPhoneNumber(phoneNumber);  // Set the phone number
            employee.setDepartment(department);  // Set the department using departmentId
   
            Employee savedEmployee = employeeService.createEmployee(employee, profilePicture);
            
             
            // Return a success response with the created employee
            ApiResponse<Employee> response = new ApiResponse<>(null, "Employee created successfully", 201);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            // Handle exceptions and return an error response
            System.out.println(e.getMessage());
            ApiResponse<Employee> response = new ApiResponse<>(null, "Internal Server Error", 500);
            return ResponseEntity.status(500).body(response);
        }
    }
 
 
    // Update an existing employee
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(@PathVariable("id") Long id, @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        ApiResponse<Employee> response = new ApiResponse<>(
                updatedEmployee,
                "Employee updated successfully",
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
 // Endpoint to fetch all employees by ID (including active and non-active)
    @GetMapping("/all/{id}")
    public ResponseEntity<ApiResponse<Employee>> getAllEmployeeById(@PathVariable("id") Long id) {
        Employee employee = employeeService.getAllEmployeeById(id); // Call the service method
        ApiResponse<Employee> response = new ApiResponse<>(
                employee,
                "Employee fetched successfully",
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Delete an employee
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        ApiResponse<Void> response = new ApiResponse<>(
                null,
                "Employee deleted successfully",
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<Employee>>> getDeletedEmployees() {
        List<Employee> deletedEmployees = employeeService.getDeletedEmployees();
        ApiResponse<List<Employee>> response = new ApiResponse<>(
                deletedEmployees,
                "Deleted employees fetched successfully",
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Autowired
    private WorkingHoursFileService fileService;
 
//    @PostMapping("/save")
//    public ResponseEntity<String> saveWorkingHours(@RequestBody String record) {
//        fileService.appendToFile(record);
//        return ResponseEntity.ok("Record appended to file successfully.");
//    }
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Void>> saveWorkingHours(@RequestBody String record) {
        logger.debug("Saving working hours record: {}", record);
        fileService.appendToFile(record);
        ApiResponse<Void> response = new ApiResponse<>(
                null,
                "Task Submitted successfully",
                HttpStatus.OK.value()
        );
        logger.info("Record appended to file successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
 
}
