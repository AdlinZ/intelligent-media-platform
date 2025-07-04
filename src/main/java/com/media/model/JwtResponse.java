package com.media.model;

public class JwtResponse {
    private String token;
    private String username;
    private String email;

    public JwtResponse(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }

    // Getters
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}