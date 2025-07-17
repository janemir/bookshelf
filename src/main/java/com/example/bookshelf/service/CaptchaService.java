package com.example.bookshelf.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String response) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", response);

        GoogleResponse googleResponse = restTemplate.postForObject(
                GOOGLE_RECAPTCHA_VERIFY_URL, params, GoogleResponse.class);

        return googleResponse != null && googleResponse.isSuccess();
    }

    public static class GoogleResponse {
        private boolean success;
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
    }
}