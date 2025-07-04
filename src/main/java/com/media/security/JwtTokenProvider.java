package com.media.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    private Key key;

    @PostConstruct
    protected void init() {
        try {
            // 打印调试信息
            System.out.println("JWT密钥原始值: " + jwtSecret);
            System.out.println("JWT密钥长度: " + jwtSecret.length());
            
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            System.out.println("Base64解码成功，密钥长度: " + keyBytes.length);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            // 打印详细错误信息
            System.err.println("JWT密钥配置错误，使用临时密钥: " + e.getMessage());
            System.err.println("错误密钥值: " + jwtSecret);
            e.printStackTrace();
            // 如果Base64解码失败，生成一个临时密钥（仅用于开发环境）
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            // 日志记录异常
            return false;
        }
    }
}