package com.demo.sharewave.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.sharewave.dto.ReceivedFileResponse;
import com.demo.sharewave.entity.Transfer;
import com.demo.sharewave.entity.User;
import com.demo.sharewave.exception.NotFoundException;
import com.demo.sharewave.exception.UnauthorizedException;
import com.demo.sharewave.repository.TransferRepository;
import com.demo.sharewave.repository.UserRepository;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FileStorageService fileStorageService;

    // Single Upload Method
    public Map<String, String> upload(String senderEmail, String receiverEmail,
                                       String roomId, int chunkIndex,
                                       int totalChunks,
                                       MultipartFile chunk) throws IOException {

        // 1. Sender dhundo
        Optional<User> senderOpt = userRepo.findByEmail(senderEmail);
        if (senderOpt.isEmpty()) {
            throw new RuntimeException("Sender not found!");
        }
     // Storage limit check
        User sender = senderOpt.get();
        long newStorageUsed = sender.getStorageUsed() + chunk.getSize();
        if (newStorageUsed > sender.getStorageLimit()) {
            throw new RuntimeException(
                "Storage limit exceeded! Upgrade to paid plan.");
        }
        sender.setStorageUsed(newStorageUsed);
        userRepo.save(sender);

        // 2. File info automatically lo
        String fileName = chunk.getOriginalFilename();
        Long fileSize = chunk.getSize();
        String fileType = chunk.getContentType();

        Transfer transfer;

        // 3. Pehla chunk — Room banao
        if (chunkIndex == 0) {
            roomId = UUID.randomUUID()
                         .toString()
                         .replace("-", "")
                         .substring(0, 10);

            transfer = new Transfer();
            transfer.setSender(senderOpt.get());
            transfer.setRoomId(roomId);
            transfer.setFileName(fileName);
            transfer.setFileSize(fileSize);
            transfer.setFileType(fileType);
            transfer.setReceiverEmail(receiverEmail);
            transfer.setStatus(Transfer.Status.PENDING);
            transferRepo.save(transfer);

        } else {
            // Aage ke chunks — roomId se dhundo
            transfer = transferRepo.findByRoomId(roomId);
            if (transfer == null) {
                throw new RuntimeException("Room not found!");
            }
        }

        // 4. Chunk save karo
        fileStorageService.saveChunk(roomId, chunkIndex, chunk);

        // 5. Last chunk? — Assemble karo
        if (chunkIndex == totalChunks - 1) {
            fileStorageService.assembleFile(roomId, fileName, totalChunks);
            transfer.setStatus(Transfer.Status.DONE);
            transferRepo.save(transfer);
        }

        return Map.of(
            "roomId", roomId,
            "message", "Chunk " + chunkIndex + " uploaded successfully!"
        );
    }

    // File Download
//    public Path getFile(String roomId) {
//        Transfer transfer = transferRepo.findByRoomId(roomId);
//        if (transfer == null) {
//            throw new RuntimeException("Room not found!");
//        }
//        return fileStorageService.getFilePath(
//            roomId,
//            transfer.getFileName()
//        );
//    }

    // Receiver ki files
    public List<ReceivedFileResponse> getReceivedFiles(String receiverEmail) {
        List<Transfer> transfers = transferRepo.findByReceiverEmail(receiverEmail);
        
        return transfers.stream()
            .map(t -> new ReceivedFileResponse(
                t.getRoomId(),
                t.getFileName(),
                t.getFileSize(),
                t.getFileType(),
                t.getSender().getEmail(),
                t.getStatus().toString()
            ))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public Path getFile(String roomId, String receiverEmail) throws IOException {
        Transfer transfer = transferRepo.findByRoomId(roomId);

        if (transfer == null)
            throw new NotFoundException("Room not found!");

        if (!transfer.getReceiverEmail().equals(receiverEmail))
            throw new UnauthorizedException("Access denied!");

        // Download count update karo
        transfer.setDownloadCount(transfer.getDownloadCount() + 1);
        transfer.setDownloadedAt(LocalDateTime.now());
        transfer.setStatus(Transfer.Status.DOWNLOADED);
        transferRepo.save(transfer);

        return fileStorageService.getFilePath(
            roomId,
            transfer.getFileName()
        );
    }
    
    public List<ReceivedFileResponse> getSentFiles(String senderEmail) {
        Optional<User> senderOpt = userRepo.findByEmail(senderEmail);
        if (senderOpt.isEmpty()) {
            throw new NotFoundException("User not found!");
        }

        List<Transfer> transfers = transferRepo
            .findBySender(senderOpt.get());

        return transfers.stream()
            .map(t -> new ReceivedFileResponse(
                t.getRoomId(),
                t.getFileName(),
                t.getFileSize(),
                t.getFileType(),
                t.getReceiverEmail(),
                t.getStatus().toString()
            ))
            .collect(Collectors.toList());
    }
}