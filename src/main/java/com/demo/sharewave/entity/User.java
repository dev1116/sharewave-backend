package com.demo.sharewave.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Plan plan = Plan.FREE;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private Long storageUsed = 0L;
    private Long storageLimit = 2147483648L; // 2GB

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLoginAt;

    public enum Plan { FREE, PAID, B2B }
    public enum Status { ACTIVE, SUSPENDED, DELETED }
}