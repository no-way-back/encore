package com.nowayback.project.application.outbox.event.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface EventPayload {
    default String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[EventPayload.toJson] EventPayload 직렬화에 실패했습니다.", e);
        }
    }
}
