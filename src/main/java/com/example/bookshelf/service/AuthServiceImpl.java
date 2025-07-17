package com.example.bookshelf.service;

import com.example.bookshelf.dto.AuthResponse;
import com.example.bookshelf.dto.LoginRequest;
import com.example.bookshelf.dto.RegisterRequest;
import com.example.bookshelf.entity.UserEntity;
import com.example.bookshelf.exception.UserAlreadyExistsException;
import com.example.bookshelf.repository.UserRepository;
import com.example.bookshelf.service.AuthService;
import com.example.bookshelf.service.EmailService;
import com.example.bookshelf.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import com.example.bookshelf.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CaptchaService captchaService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {

        if (!captchaService.verifyCaptcha(request.getRecaptchaResponse())) {
            throw new RuntimeException("Invalid captcha");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        String verificationToken = UUID.randomUUID().toString();

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .verificationToken(verificationToken)
                .isVerified(false)
                .enabled(true)
                .build();

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        UserEntity user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}