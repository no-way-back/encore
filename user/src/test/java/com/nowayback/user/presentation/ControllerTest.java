package com.nowayback.user.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowayback.user.infrastructure.auditing.AuthHeaders;
import com.nowayback.user.presentation.exception.UserExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

@Import(UserExceptionHandler.class)
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ResultActions performWithAuth(MockHttpServletRequestBuilder builder, String role) throws Exception {
        UUID userId = UUID.randomUUID();
        String username = "test";

        return mockMvc.perform(
                builder
                        .header(AuthHeaders.USER_ID, userId.toString())
                        .header(AuthHeaders.USERNAME, username)
                        .header(AuthHeaders.USER_ROLE, role)
        );
    }

    protected ResultActions performWithAuth(MockHttpServletRequestBuilder builder) throws Exception {
        return performWithAuth(builder, "MASTER");
    }
}