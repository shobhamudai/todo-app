package com.example.model;

import lombok.Data;

@Data
public class UserBo {
    private String id;
    private String email;
    private String username;
    private Boolean emailVerified;
    private Long createdAt;
}
