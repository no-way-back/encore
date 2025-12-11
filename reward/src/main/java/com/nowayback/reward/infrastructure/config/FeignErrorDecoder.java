package com.nowayback.reward.infrastructure.config;

import org.springframework.http.HttpStatus;

import com.nowayback.reward.domain.exception.RewardException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());

        log.error("Feign 에러 - method: {}, url: {}, status: {}",
                methodKey, response.request().url(), response.status());

        if (httpStatus == null) {
            return new RewardException(PROJECT_SERVICE_UNAVAILABLE);
        }

        return switch (httpStatus) {
            case BAD_REQUEST -> new RewardException(PROJECT_BAD_REQUEST);
            case NOT_FOUND -> new RewardException(PROJECT_NOT_FOUND);
            case CONFLICT -> new RewardException(PROJECT_CONFLICT);
            case SERVICE_UNAVAILABLE, BAD_GATEWAY ->
                    new RewardException(PROJECT_SERVICE_UNAVAILABLE);
            case GATEWAY_TIMEOUT, REQUEST_TIMEOUT ->
                    new RewardException(PROJECT_SERVICE_TIMEOUT);
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}