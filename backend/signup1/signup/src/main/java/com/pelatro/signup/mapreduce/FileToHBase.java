package com.pelatro.signup.mapreduce;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
            System.out.println("Data inserted into HBase table: " + tableName);
        }
    }
 
    public static void splitAndProcessFile(String filePath, String outputDir) throws IOException {
        int fileCounter = 0;
        int lineCounter = 0;
        File outputDirFile = new File(outputDir);
 
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }
 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            FileWriter writer = null;
 
            while ((line = br.readLine()) != null) {
                if (lineCounter % 5 == 0) {
                    if (writer != null) {
                        writer.close();
                    }
                    fileCounter++;
                    String newFilePath = outputDir + "/file_part_" + fileCounter + "_readytoexecute.txt";
                    writer = new FileWriter(newFilePath);
                }
 
                writer.write(line);
                writer.write("\n");
                lineCounter++;
            }
 
            if (writer != null) {
                writer.close();
            }
        }
        System.out.println("File split and processed into directory: " + outputDir);
    }
 
    public static void renameIncompleteFiles(String inputDir) {
        File dir = new File(inputDir);
 
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
 
        long currentTime = System.currentTimeMillis();
 
        for (File file : dir.listFiles((d, name) -> !name.endsWith("readytoexecute.txt") && !name.endsWith("done.txt"))) {
            if (currentTime - file.lastModified() > 3600000) { // 1 hour in milliseconds
                String readyFileName = file.getAbsolutePath().replace(".txt", "_readytoexecute.txt");
                if (file.renameTo(new File(readyFileName))) {
                    System.out.println("Renamed file to readytoexecute: " + file.getName());
                } else {
                    System.err.println("Failed to rename file: " + file.getName());
                }
            }
        }
    }
}
 
 