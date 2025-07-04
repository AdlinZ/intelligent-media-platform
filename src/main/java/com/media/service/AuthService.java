package com.media.service;

import com.media.model.User;
import com.media.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import com.media.security.JwtTokenProvider;

@Service
public class AuthService {
    // 添加 Logger 声明
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public String authenticateUser(String usernameOrEmail, String password) {
        try {
            // 尝试通过用户名或邮箱查找用户
            User user = userRepository.findByUsername(usernameOrEmail)
                .orElse(userRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new BadCredentialsException("用户名或密码不正确")));

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return tokenProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            logger.error("登录失败: 用户名或密码不正确 - 登录名: {}", usernameOrEmail);
            throw new RuntimeException("用户名或密码不正确");
        } catch (Exception e) {
            logger.error("登录异常: {}", e.getMessage());
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }

    public User registerUser(String username, String email, String password) {
        // Check if username exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already taken");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton("USER"));

        return userRepository.save(user);
    }
}