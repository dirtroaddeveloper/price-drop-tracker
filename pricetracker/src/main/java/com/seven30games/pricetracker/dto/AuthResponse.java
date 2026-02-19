package com.seven30games.pricetracker.dto;

public record AuthResponse(String accessToken, String refreshToken, String email) {}
