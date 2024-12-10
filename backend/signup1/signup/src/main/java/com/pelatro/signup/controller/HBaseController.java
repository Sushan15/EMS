package com.pelatro.signup.controller;


import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pelatro.signup.response.ApiResponse;
import com.pelatro.signup.serviceImp.HBaseServiceImp;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/workhours")
public class HBaseController {
	
	@Autowired
	private HBaseServiceImp hBaseServiceImp;
	
    @PostMapping
    public ResponseEntity<ApiResponse<String>> addEmployeeData(@RequestBody Map<String, Object> requestData) throws IOException {
    	
    	try {
            String result = hBaseServiceImp.addEmployeeData(requestData);
            ApiResponse<String> response = new ApiResponse<String>( "pass", result );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ApiResponse<String> response = new ApiResponse<String>( "fail", null );
            return ResponseEntity.status(500).body(response);
        }
    	
    }
    
    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getEmployeePerformance(@RequestParam(name = "filePath") String filePath) {
    	
        try {
        	
            Map<String, Double> employeePerformance = hBaseServiceImp.getEmployeePerformance(filePath);
            ApiResponse<Map<String, Double>> response = new ApiResponse<Map<String, Double>>( "pass", employeePerformance );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ApiResponse<Map<String, Double>> response = new ApiResponse<Map<String, Double>>( "fail", null );
            return ResponseEntity.status(500).body(response);
        }
    }

    
    

}
