package com.example.bookshelf.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final VerificationTokenRepository tokenRepository;

    @Transactional
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

        String verificationUrl = "http://localhost:8080/api/v1/registration/verify?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Подтверждение регистрации");
        mailMessage.setText("Для завершения регистрации перейдите по ссылке: " + verificationUrl);

        mailSender.send(mailMessage);
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный токен подтверждения"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Срок действия токена истёк");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }
}
