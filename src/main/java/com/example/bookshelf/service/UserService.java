package com.example.bookshelf.service;

import com.example.bookshelf.dto.RegisterRequest;
import com.example.bookshelf.entity.UserEntity;
import com.example.bookshelf.entity.VerificationToken;
import com.example.bookshelf.repository.TokenRepository;
import com.example.bookshelf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .build();

        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);

        sendConfirmationEmail(user.getEmail(), token);
    }

    private void sendConfirmationEmail(String to, String token) {
        String link = "http://localhost:8081/api/v1/auth/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Подтверждение регистрации");
        message.setText("Нажмите на ссылку для подтверждения: " + link);
        mailSender.send(message);
    }

    public void confirmToken(String token) {
        VerificationToken vt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный токен"));

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Токен истёк");
        }

        UserEntity user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }
}
