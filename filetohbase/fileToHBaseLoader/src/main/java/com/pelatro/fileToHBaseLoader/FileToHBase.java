package com.pelatro.fileToHBaseLoader;


import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class FileToHBase {

    public static void insertDataFromFileToHBase(String filePath, String tableName) throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");

        try (Connection connection = ConnectionFactory.createConnection(config);
             Table table = connection.getTable(org.apache.hadoop.hbase.TableName.valueOf(tableName));
             BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 6) {  // Ensure correct format: id,name,date,task_assigned,task_completed,ontime_task_completed
                    System.err.println("Invalid data format: " + line);
                    continue;
                }

                // Parse the fields
                String empId = fields[0].trim();
                String name = fields[1].trim();
                String date = fields[2].trim();
                String taskAssigned = fields[3].trim();
                String taskCompleted = fields[4].trim();
                String ontimeTaskCompleted = fields[5].trim();

                // Row key: empId_date
                String rowKey = empId + "_" + date;

                // Create a Put instance for the row key
                Put put = new Put(Bytes.toBytes(rowKey));

                // Add to "employee_details" column family
                put.addColumn(Bytes.toBytes("employee_details"), Bytes.toBytes("id"), Bytes.toBytes(empId));
                put.addColumn(Bytes.toBytes("employee_details"), Bytes.toBytes("name"), Bytes.toBytes(name));

                // Add to "work_details" column family
                put.addColumn(Bytes.toBytes("work_details"), Bytes.toBytes("task_assigned"), Bytes.toBytes(taskAssigned));
                put.addColumn(Bytes.toBytes("work_details"), Bytes.toBytes("task_completed"), Bytes.toBytes(taskCompleted));
                put.addColumn(Bytes.toBytes("work_details"), Bytes.toBytes("ontime_task_completed"), Bytes.toBytes(ontimeTaskCompleted));

                // Insert the Put into the HBase table
                table.put(put);
            }
            System.out.println("Data successfully inserted from file: " + filePath);
        }
    }

    public static void processFiles(String inputDir, String completedDir, String tableName) throws IOException {
        File inputDirectory = new File(inputDir);
        File completedDirectory = new File(completedDir);

        // Ensure completed directory exists
        if (!completedDirectory.exists()) {
            completedDirectory.mkdirs();
        }

        // Get all files with "_readytoexecute" suffix
        File[] files = inputDirectory.listFiles((dir, name) -> name.endsWith("_readytoexecute.txt"));

        if (files == null || files.length == 0) {
            System.out.println("No files with suffix '_readytoexecute' found in directory: " + inputDir);
            return;
        }

        for (File file : files) {
            String filePath = file.getAbsolutePath();
            System.out.println("Processing file: " + filePath);

            try {
                // Insert data into HBase
                insertDataFromFileToHBase(filePath, tableName);

                // Rename file with "_done" suffix and move to completed directory
                File completedFile = new File(completedDir, file.getName().replace("_readytoexecute", "_done"));
                if (file.renameTo(completedFile)) {
                    System.out.println("File moved to completed folder: " + completedFile.getAbsolutePath());
                } else {
                    System.err.println("Failed to move file: " + filePath);
                }
            } catch (Exception e) {
                System.err.println("Error processing file: " + filePath);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
    	// Step 1: Define the directories and HBase table name
        String inputDir = "/home/pelatro/Hbasedatastoragefile/"; // Directory containing files to process
        String completedDir = "/home/pelatro/Hbasedatastoragefile/completed/"; // Directory for completed files
        String tableName = "employee"; // Name of the HBase table


        try {
        	while(true) {
            processFiles(inputDir, completedDir, tableName);
            Thread.sleep( 60000 );
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
}
