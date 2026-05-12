package com.demo.sharewave.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
@Data
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(unique = true, nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long fileSize;
    
    private String receiverEmail;

    private String fileType;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        PENDING, ACTIVE, DONE, FAILED
    }
}