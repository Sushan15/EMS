package com.pelatro.signup.serviceImp;



//import com.google.common.io.Files;
//import com.jayway.jsonpath.internal.Path;
//import com.pelatro.signup.entity.Employee;
//import com.pelatro.signup.repository.EmployeeRepository;
//import com.pelatro.signup.service.EmployeeService;
//
//import ch.qos.logback.classic.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

//import org.apache.logging.log4j.LogManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.List;
//import java.util.Optional;
//
//import java.util.List;
//import java.util.Optional;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.List;
//import java.util.Optional;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
 
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
import com.pelatro.signup.entity.Employee;
import com.pelatro.signup.repository.EmployeeRepository;
import com.pelatro.signup.service.EmployeeService;
 
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
 
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
 

@Service
public class EmployeeServiceImp implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final Logger logger = LogManager.getLogger(EmployeeServiceImp.class);
    private final String UPLOAD_DIR = "uploads/";
 
   
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EmployeeServiceImp(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        // Fetch only active employees (not deleted)
        return employeeRepository.findAllActiveEmployees();
    }
//    @Override
//    public Employee createEmployee(Employee employee) {
//        return employeeRepository.save(employee); // Save the new employee to the database
//    }
    
    @Override
    public Employee createEmployee(Employee employee, MultipartFile profilePicture) throws Exception {
        try {
            logger.debug("Attempting to create new employee: {}", employee);
 
            // Save the employee first to generate the employee ID
            Employee savedEmployee = employeeRepository.save(employee);
            
            if (profilePicture != null && !profilePicture.isEmpty()) {
                String originalFileName = profilePicture.getOriginalFilename();
                
                // Generate a unique file name using employee's ID
                String uniqueFileName = savedEmployee.getId() + "_" + originalFileName;
 
                Path uploadPath = Paths.get(UPLOAD_DIR);
                savedEmployee.setProfilePicture(UPLOAD_DIR + uniqueFileName);
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Create the directory if it doesn't exist
                }
                
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(profilePicture.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                savedEmployee.setProfilePicture(null);
            }
 
            // Update the employee record with the profile picture path if needed
            savedEmployee = employeeRepository.save(savedEmployee);
 
            logger.info("Employee created successfully with ID: {}", savedEmployee.getId());
            return savedEmployee;
        } catch (Exception e) {
            logger.error("Error creating employee: {}", e.getMessage(), e);
            throw e;
        }
    }
 

 
    @Override
    public Employee getEmployeeById(Long id) {
        Optional<Employee> employee = employeeRepository.findByIdAndIsDeletedFalse(id); // Fetch only active employees
        return employee.orElseThrow(() -> new RuntimeException("Employee not found or has been deleted with id: " + id)); // Return the employee if found, or throw an exception
    }

    @Override
    public Employee getAllEmployeeById(Long id) {
    	System.out.println("hello");
        Optional<Employee> employee = employeeRepository.findById(id); // Fetch employees by ID
        
        return employee.orElseThrow(() -> new RuntimeException("No employees found with ID: " + id)); // Return the list of employees
    }
    
    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        // Fetch the existing employee by id and ensure it is not deleted
        Optional<Employee> existingEmployeeOpt = employeeRepository.findByIdAndIsDeletedFalse(id);

        // Check if the employee exists and is not deleted
        if (!existingEmployeeOpt.isPresent()) {
            throw new RuntimeException("Employee not found or is marked as deleted with id: " + id);
        }

        // Get the existing employee
        Employee existingEmployee = existingEmployeeOpt.get();
        
        //entityManager.refresh(existingEmployee);

        // Update employee fields
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmailId(employee.getEmailId());
        existingEmployee.setRole(employee.getRole());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setPhoneNumber(employee.getPhoneNumber()); // Update phone number if necessary

        // Save and return the updated employee
        return employeeRepository.save(existingEmployee); // Save the updated employee to the database
    }



    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Perform soft delete by setting `isDeleted` to true
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getDeletedEmployees() {
        // Fetch deleted employees using the repository method
        return employeeRepository.findDeletedEmployees();
    }
}
