package com.nowayback.user.infrastructure.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Component
public class HeaderAuditorAware implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return Optional.empty();
        }

        String userIdHeader = attributes.getRequest().getHeader(AuthHeaders.USER_ID);

        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return Optional.empty();
        }

        try {
            UUID userId = UUID.fromString(userIdHeader);
            return Optional.of(userId);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}