package com.techstart.jobportal.controller;

import com.techstart.jobportal.dto.ResumeData;
import com.techstart.jobportal.service.ResumeParsingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Collections;



@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeParsingService resumeParsingService;

    public ResumeController(ResumeParsingService resumeParsingService) {
        this.resumeParsingService = resumeParsingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeData> uploadResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(errorResponse("Empty file", "Please upload a PDF file"));
        }

        if (!"application/pdf".equals(file.getContentType())) {
            return ResponseEntity.badRequest().body(errorResponse("Invalid file type", "Only PDF files are allowed"));
        }

        File tempFile = null;
        try {
            // Create temp file with proper PDF extension
            tempFile = File.createTempFile("resume_", ".pdf");

            // Use buffered transfer for better large file handling
            try (InputStream is = file.getInputStream();
                 OutputStream os = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            // Validate PDF structure
            if (!isValidPDF(tempFile)) {
                return ResponseEntity.badRequest().body(errorResponse("Invalid PDF", "The file is not a valid PDF"));
            }

            // Parse and process
            ResumeData parsedData = resumeParsingService.parseResume(tempFile);
            System.out.println(ResponseEntity.ok(parsedData));
            return ResponseEntity.ok(parsedData);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(errorResponse("Processing error", e.getMessage()));
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private boolean isValidPDF(File file) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            if (raf.length() < 5) return false;
            byte[] header = new byte[5];
            raf.read(header);
            return new String(header).startsWith("%PDF-");
        }
    }

    private ResumeData errorResponse(String error, String message) {
        return new ResumeData("Error", error,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), message);
    }
}