package com.pelatro.signup.service;
 
import java.io.FileWriter;
import java.io.IOException;
 
import org.springframework.stereotype.Service;
 
import java.io.*;
import org.springframework.stereotype.Service;
 
 
@Service
public class WorkingHoursFileService {
 
    private static final String DIRECTORY_PATH = "/home/pelatro/Hbasedatastoragefile/";
    private static final String READY_TO_EXECUTE_SUFFIX = "_readytoexecute.txt";
 
    public WorkingHoursFileService() {
        ensureDirectoryExists();
    }
 
    public void appendToFile(String record) {
        try {
            // Create a new file with a unique timestamp
            File newFile = createNewFile();
            writeToFile(newFile, record);
 
            // Rename the file with the ready-to-execute suffix
            renameToReadyToExecute(newFile);
        } catch (IOException e) {
            throw new RuntimeException("Error while processing the file", e);
        }
    }
 
    private void ensureDirectoryExists() {
        File dir = new File(DIRECTORY_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + DIRECTORY_PATH);
            }
        }
    }
 
    private File createNewFile() throws IOException {
        String newFileName = DIRECTORY_PATH + "file_" + System.currentTimeMillis() + ".txt";
        File newFile = new File(newFileName);
        if (newFile.createNewFile()) {
            setFilePermissions(newFile);
            return newFile;
        } else {
            throw new IOException("Failed to create new file: " + newFileName);
        }
    }
 
    private void setFilePermissions(File file) {
        // Grant read, write, and execute permissions to all
        if (!file.setReadable(true, false) ||
            !file.setWritable(true, false) ||
            !file.setExecutable(true, false)) {
            throw new RuntimeException("Failed to set permissions for file: " + file.getName());
        }
    }
 
    private void writeToFile(File file, String record) throws IOException {
        try (FileWriter writer = new FileWriter(file)) { // No appending needed as each file is new
            writer.write(record + "\n");
        }
    }
 
    private void renameToReadyToExecute(File file) {
        String newFileName = file.getAbsolutePath().replace(".txt", READY_TO_EXECUTE_SUFFIX);
        File readyFile = new File(newFileName);
 
        if (file.renameTo(readyFile)) {
            System.out.println("File renamed to: " + readyFile.getName());
        } else {
            throw new RuntimeException("Failed to rename file to readytoexecute: " + file.getName());
        }
    }
}
 
 
 
 
 