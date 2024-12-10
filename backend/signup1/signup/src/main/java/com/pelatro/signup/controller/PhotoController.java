package com.pelatro.signup.controller;





import org.springframework.core.io.Resource;
 
import org.springframework.core.io.UrlResource;
 
import org.springframework.http.HttpHeaders;
 
import org.springframework.http.HttpStatus;
 
import org.springframework.http.MediaType;
 
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.GetMapping;
 
import org.springframework.web.bind.annotation.RequestMapping;
 
import org.springframework.web.bind.annotation.RequestParam;
 
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
 
import java.nio.file.Files;
 
import java.nio.file.Path;
 
import java.nio.file.Paths;
 
import java.net.URLDecoder;
 
import java.nio.charset.StandardCharsets;

@RestController

@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/api/photos")
 
public class PhotoController {

    @GetMapping
 
    public ResponseEntity<?> getPhoto(@RequestParam("profilePicturePath") String profilePicturePath) {
 
        try {
 
            // Decode the URL-encoded profilePicturePath
 
            String decodedPath = URLDecoder.decode(profilePicturePath, StandardCharsets.UTF_8.name());
 
            System.out.println("Requested photo path: " + decodedPath);

            Path filePath = Paths.get(decodedPath).normalize();
 
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
 
                // Determine the file's content type
 
                String contentType = Files.probeContentType(filePath);
 
                if (contentType == null) {
 
                    contentType = "application/octet-stream"; // Default if type is unknown
 
                }

                // Return the image as the response
 
                return ResponseEntity.ok()
 
                        .contentType(MediaType.parseMediaType(contentType))
 
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
 
                        .body(resource);
 
            } else {
 
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
 
                        .body("File not found at path: " + decodedPath);
 
            }
 
        } catch (IOException ex) {
 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
 
                    .body("Error reading the file: " + ex.getMessage());
 
        }
 
    }
 
}
 
 