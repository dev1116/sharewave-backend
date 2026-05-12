package com.demo.sharewave.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceivedFileResponse {
    private String roomId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String senderEmail;
    private String status;
}