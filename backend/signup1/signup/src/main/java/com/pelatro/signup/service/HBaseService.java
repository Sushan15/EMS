package com.pelatro.signup.service;

import java.io.IOException;
import java.util.Map;

public interface HBaseService {
	
	public String addEmployeeData(Map<String, Object> requestData) throws IOException;
	public Map<String, Double> getEmployeePerformance(String filePath) throws IOException;

}










//import com.pelatro.signup.dto.WorkDetails;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.TableName;
//import org.apache.hadoop.hbase.client.*;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//public class HBaseService {
//
//    private final Configuration configuration;
//
//    public HBaseService(Configuration configuration) {
//        this.configuration = configuration;
//    }
//
//    public WorkDetails getEmployeeWorkDetails(String employeeId) throws IOException {
//        try (Connection connection = ConnectionFactory.createConnection(configuration);
//             Table table = connection.getTable(TableName.valueOf("employee"))) {
//
//            Get get = new Get(Bytes.toBytes(employeeId));
//            Result result = table.get(get);
//
//            String startTime = Bytes.toString(result.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("start_time")));
//            String endTime = Bytes.toString(result.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("end_time")));
//            String date = Bytes.toString(result.getValue(Bytes.toBytes("work_details"), Bytes.toBytes("date")));
//
//            return new WorkDetails(startTime, endTime, date);
//        }
//    }
//}