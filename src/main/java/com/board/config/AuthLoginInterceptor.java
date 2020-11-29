package com.board.config;

import com.board.service.UserService;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserResponseDto;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthLoginInterceptor extends HandlerInterceptorAdapter {

  private final UserService userService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (handler instanceof HandlerMethod == false) {
      return true;
    }
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
    HttpSession session = request.getSession();
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    Cookie loginCookie = WebUtils.getCookie(request, "loginCookie");
    if (user == null && loginCookie != null) {
      try {
        UserResponseDto cookieUser = userService.findBySessionId(loginCookie.getValue());
        session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, cookieUser);
      } catch (NullPointerException e) {
        loginCookie.setMaxAge(0);
        response.addCookie(loginCookie);
      }
    }
    if (auth == null) {
      return true;
    }
    if (!HttpSessionUtils.isLoginUser(session)) {
      if (request.getMethod().equals("PUT") || request.getMethod().equals("DELETE")) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
      }
      response.setStatus(HttpStatus.SEE_OTHER.value());
      response.setHeader(HttpHeaders.LOCATION, "/user/login");
      return false;
    }
    String role = auth.role().toString();
    if ("ADMIN".equals(role)) {
      if ("admin".equals(user.getUserId())) {
        return true;
      } else {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
      }
    }

    return true;
  }
}
