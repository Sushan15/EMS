package com.pelatro.signup.mapreduce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class PerformanceFileCreator {

    public void createPerformanceFile(String localResultFilePath, Path hdfsOutputPath, Configuration config) throws IOException {
        // Create a new file or overwrite if it exists
        File resultFile = new File(localResultFilePath);
        if (resultFile.exists()) {
            resultFile.delete();  // Delete existing file to overwrite
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, false))) {
            FileSystem fs = FileSystem.get(config);
            Path resultFilePathInHDFS = new Path(hdfsOutputPath, "part-r-00000");

            if (fs.exists(resultFilePathInHDFS)) {
                try (FSDataInputStream inputStream = fs.open(resultFilePathInHDFS)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        // Extract the required fields (id and performance percentage)
                        String[] fields = line.split(",");
                        if (fields.length >= 7) {
                            // Extract ID from the first field
                            String id = fields[0].trim();

                            // Extract Performance percentage from the third field
                            String performanceField = fields[6].trim();
                            String performance = extractPerformanceValue(performanceField);

                            if (id != null && performance != null) {
                                // Write in the format: id, performance
                                writer.write(id + "," + performance);
                                writer.newLine();
                            }
                        }
                    }
                }
            } else {
                System.out.println("The result file does not exist in HDFS.");
            }
        } catch (IOException e) {
            System.err.println("Error while writing to the local file: " + e.getMessage());
            throw e;
        }
    }

    // Helper method to extract the numeric value of performance from the string
    private String extractPerformanceValue(String text) {
        if (text.contains("Performance:")) {
            // Extract the numeric part after "Performance:"
            return text.substring(text.indexOf("Performance:") + "Performance:".length()).trim().replace("%", "");
        }
        return null;  // Return null if the performance value cannot be extracted
    }
}
