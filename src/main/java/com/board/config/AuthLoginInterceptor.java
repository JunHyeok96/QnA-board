package com.board.config;

import com.board.config.Auth;
import com.board.web.HttpSessionUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
@Component
public class AuthLoginInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (handler instanceof HandlerMethod == false) {
      return true;
    }

    HandlerMethod handlerMethod = (HandlerMethod) handler;

    Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

    if (auth == null) {
      return true;
    }

    HttpSession session = request.getSession();
    if (!HttpSessionUtils.isLoginUser(session)) {
      response.setStatus(303);
      response.setHeader(HttpHeaders.LOCATION, "/user/login");
      return false;
    }

    return true;
  }
}
