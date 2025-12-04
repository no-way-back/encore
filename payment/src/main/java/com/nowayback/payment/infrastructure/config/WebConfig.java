package com.nowayback.payment.infrastructure.config;

import com.nowayback.payment.infrastructure.auth.role.RoleCheckInterceptor;
import com.nowayback.payment.infrastructure.auth.user.AuthUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthUserArgumentResolver authUserArgumentResolver;
    private final RoleCheckInterceptor roleCheckInterceptor;

    public WebConfig(AuthUserArgumentResolver authUserArgumentResolver, RoleCheckInterceptor roleCheckInterceptor) {
        this.authUserArgumentResolver = authUserArgumentResolver;
        this.roleCheckInterceptor = roleCheckInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUserArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleCheckInterceptor)
                .addPathPatterns("/**");
    }
}
