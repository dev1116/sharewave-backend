package com.demo.sharewave.object;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}