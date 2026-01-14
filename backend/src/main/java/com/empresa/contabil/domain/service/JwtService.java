package com.empresa.contabil.domain.service;

import com.empresa.contabil.domain.model.User;

public interface JwtService {

    String generateToken(User user);

    String extractUsername(String token);

    boolean isTokenValid(String token, User user);
}