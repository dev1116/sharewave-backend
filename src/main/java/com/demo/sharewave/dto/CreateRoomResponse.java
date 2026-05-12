package com.demo.sharewave.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateRoomResponse {
    private String roomId;
    private String fileName;
    private Long fileSize;
    private String status;
    private String shareLink;
}