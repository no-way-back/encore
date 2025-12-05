package com.nowayback.user.infrastructure.auth.role;

import com.nowayback.user.domain.exception.UserErrorCode;
import com.nowayback.user.domain.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

    private static final String HEADER_USER_ROLE = "X-User-Role";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiredRole requiredRole = handlerMethod.getMethodAnnotation(RequiredRole.class);

        if (requiredRole == null) {
            return true;
        }

        String roleHeader = request.getHeader(HEADER_USER_ROLE);
        if (roleHeader == null) {
            throw new UserException(UserErrorCode.UNAUTHORIZED);
        }

        String[] allowedRoles = requiredRole.value();

        boolean hasRequiredRole = Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equalsIgnoreCase(roleHeader));

        if (!hasRequiredRole) {
            throw new UserException(UserErrorCode.FORBIDDEN);
        }

        return true;
    }
}
