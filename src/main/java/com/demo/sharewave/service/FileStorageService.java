package com.demo.sharewave.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

@Service
public class FileStorageService {

    @Value("${file.upload.dir}")
    private String uploadDir;

    // Base path get karo — absolute path
    private Path getBasePath() throws IOException {
        Path base = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(base); // agar nahi hai toh banao
        return base;
    }

    // Chunk save karo
    public void saveChunk(String roomId, int chunkIndex,
                           MultipartFile chunk) throws IOException {

        // Room folder banao
        Path roomPath = getBasePath().resolve(roomId);
        Files.createDirectories(roomPath); // ← Yeh important hai!

        // Chunk save karo
        Path chunkPath = roomPath.resolve("chunk_" + chunkIndex);
        chunk.transferTo(chunkPath.toFile());
    }

    // Saare chunks jodo
    public Path assembleFile(String roomId, String fileName,
                              int totalChunks) throws IOException {

        Path roomPath = getBasePath().resolve(roomId);
        Path finalFile = roomPath.resolve(fileName);

        try (FileOutputStream fos = 
                new FileOutputStream(finalFile.toFile())) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkPath = roomPath.resolve("chunk_" + i);
                Files.copy(chunkPath, fos);
                Files.delete(chunkPath);
            }
        }

        return finalFile;
    }

    // File path lo
    public Path getFilePath(String roomId, String fileName) throws IOException {
        return getBasePath().resolve(roomId).resolve(fileName);
    }

    // Room delete karo
    public void deleteRoom(String roomId) throws IOException {
        Path roomPath = getBasePath().resolve(roomId);
        if (Files.exists(roomPath)) {
            Files.walk(roomPath)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try { Files.delete(path); }
                    catch (IOException e) { e.printStackTrace(); }
                });
        }
    }
}