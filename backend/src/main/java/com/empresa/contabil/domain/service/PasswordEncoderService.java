package com.empresa.contabil.domain.service;

public interface PasswordEncoderService {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}