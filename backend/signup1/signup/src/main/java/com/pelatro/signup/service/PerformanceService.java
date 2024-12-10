package com.pelatro.signup.service;

import org.springframework.stereotype.Service;

import com.pelatro.signup.DTO.PerformanceDTO;

@Service
public class PerformanceService {

    public PerformanceDTO getPerformanceByEmployeeId(String employeeId) {
        // Fetch the performance data for the given employee from HBase, file, or database
        // Return the data in the form of a PerformanceDTO
        return new PerformanceDTO(employeeId, "Overall Performance Score");
    }
}

