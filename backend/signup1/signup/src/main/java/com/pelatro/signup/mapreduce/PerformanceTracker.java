package com.pelatro.signup.mapreduce;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
public class PerformanceTracker {
    private static final Logger logger = LogManager.getLogger(PerformanceTracker.class);
 
    public void performanceDriver() throws IOException, ClassNotFoundException, InterruptedException {
        String inputDir = "/home/pelatro/Hbasedatastoragefile/"; // Directory containing the files
        String completedDir = "/home/pelatro/Hbasedatastoragefile/completed/"; // Directory for completed files
        String performanceDir = "/home/pelatro/Hbasedatastoragefile/performance/"; // Directory for performance results
 
        // Step 1: Iterate through files with readytoexecute suffix
        File inputDirectory = new File(inputDir);
        File[] files = inputDirectory.listFiles((dir, name) -> name.endsWith("_readytoexecute.txt"));
        
        if (files != null && files.length > 0) {
            for (File file : files) {
                String localFilePath = file.getAbsolutePath();
                String hbaseTableName = "employee"; // HBase table name
 
                // Step 2: Load file data into HBase
                FileToHBase.insertDataFromFileToHBase(localFilePath, hbaseTableName);
 
                // Step 3: Run the MapReduce job
                Configuration config = new Configuration();
                config.set("fs.defaultFS", "hdfs://localhost:9000");
                config.set("hbase.zookeeper.quorum", "localhost");
                config.set("hbase.zookeeper.property.clientPort", "2181");
 
                Path outputPath = new Path("/user/hadoop/performance_output");
                FileSystem fs = FileSystem.get(config);
 
                if (fs.exists(outputPath)) {
                    fs.delete(outputPath, true);
                    logger.info("Existing output folder deleted.");
                }
 
                Job job = Job.getInstance(config, "Employee Performance Tracker");
                job.setJarByClass(PerformanceTracker.class);
 
                // Configure Mapper and Reducer
                Scan scan = new Scan();
                TableMapReduceUtil.initTableMapperJob(
                        hbaseTableName,         // HBase table name
                        scan,                   // Scan instance
                        PerformanceMapper.class, // Mapper class
                        Text.class,             // Mapper output key
                        Text.class,             // Mapper output value
                        job
                );
 
                job.setReducerClass(PerformanceReducer.class);
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
 
                FileOutputFormat.setOutputPath(job, outputPath);
 
                boolean success = job.waitForCompletion(true);
                if (!success) {
                    logger.error("Job failed for file: " + localFilePath);
                    continue; // Skip to the next file
                }
 
                // Step 4: Change the suffix and move the file to completed directory
                String newFilePath = localFilePath.replace("readytoexecute", "done");
                File completedFile = new File(completedDir, new File(newFilePath).getName());
                boolean moved = file.renameTo(completedFile);
                if (moved) {
                    logger.info("File moved to completed directory: " + completedFile.getAbsolutePath());
                } else {
                    logger.error("Failed to move file: " + localFilePath);
                }
 
                // Step 5: Extract performance and save it to performance directory
                String localResultFilePath = performanceDir + "/performance_result.txt";
                PerformanceFileCreator fileCreator = new PerformanceFileCreator();
                fileCreator.createPerformanceFile(localResultFilePath, outputPath, config);  // Create or overwrite the result file
            }
        } else {
            logger.info("No files with 'readytoexecute' suffix found.");
        }
    }
 
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        PerformanceTracker performanceTracker = new PerformanceTracker();
        while(true) {
        performanceTracker.performanceDriver();
        Thread.sleep( 60000 );
 
        }
    }
}
 
 