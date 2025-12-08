package com.nowayback.user.domain.repository;

public interface TokenBlacklistRepository {
    void addToBlacklist(String token, long expirationTime);
}
