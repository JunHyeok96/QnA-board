package com.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

  private final AuthLoginInterceptor authLoginInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authLoginInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/")
        .excludePathPatterns("/post/read/*")
        .excludePathPatterns("/user/login")
        .excludePathPatterns("/user/form");
  }
}