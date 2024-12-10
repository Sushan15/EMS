package com.pelatro.signup.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Optional;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "http://localhost:4200") // Allow CORS for Angular app
public class PerformanceReportController {

    @GetMapping("/report")
    public ResponseEntity<Resource> getPerformanceReport() {
        try {
            // Path to the directory containing the performance files
            Path dirPath = Paths.get("/home/pelatro/Hbasedatastoragefile/performance");
            
            // Get the most recent file in the directory
            Optional<Path> latestFile = Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().startsWith("final_"))
                    .max(Comparator.comparing(file -> file.getFileName().toString())); // Compare file names based on natural order

            if (latestFile.isPresent()) {
                // Get the latest file based on timestamp in its name
                Path latestFilePath = latestFile.get();
                Resource resource = new UrlResource(latestFilePath.toUri());
                
                if (resource.exists() && resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + latestFilePath.getFileName())
                            .body(resource);
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
//    @GetMapping("/report")
//    public ResponseEntity<Resource> getPerformanceReport() {
//        try {
//            Path path = Paths.get("/home/pelatro/Hbasedatastoragefile/performance/performance_result.txt");
//            Resource resource = new UrlResource(path.toUri());
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=performance_result.txt")
//                        .body(resource);
//            } else {
//                return ResponseEntity.status(404).body(null);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }
}
