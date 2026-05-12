package com.demo.sharewave.dto;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String receiverEmail; 
}