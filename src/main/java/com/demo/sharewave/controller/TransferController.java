package com.demo.sharewave.controller;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.sharewave.service.TransferService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    // Upload
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("receiverEmail") String receiverEmail,
            @RequestParam(value = "roomId", required = false) String roomId,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("chunk") MultipartFile chunk,
            Authentication auth) {
        try {
            String senderEmail = auth.getName();
            return ResponseEntity.ok(
                transferService.upload(
                    senderEmail, receiverEmail,
                    roomId, chunkIndex, totalChunks, chunk
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Received Files
    @GetMapping("/received-files")
    public ResponseEntity<?> receivedFiles(Authentication auth) {
        try {
            String email = auth.getName();
            return ResponseEntity.ok(
                transferService.getReceivedFiles(email)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Download
    @GetMapping("/download/{roomId}")
    public ResponseEntity<?> downloadFile(
            @PathVariable String roomId,
            Authentication auth) {
        try {
            String receiverEmail = auth.getName();
            Path filePath = transferService.getFile(roomId, receiverEmail);

            Resource resource = new FileSystemResource(filePath);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" +
                    filePath.getFileName().toString() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(filePath))
                .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
 // Sender ki sent files
    @GetMapping("/sent-files")
    public ResponseEntity<?> sentFiles(Authentication auth) {
        try {
            String email = auth.getName();
            return ResponseEntity.ok(
                transferService.getSentFiles(email)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}