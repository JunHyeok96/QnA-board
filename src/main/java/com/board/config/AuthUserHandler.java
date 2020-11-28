package com.board.config;

import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserResponseDto;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthUserHandler implements HandlerMethodArgumentResolver {

  private final HttpSession session;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    boolean isAuthUserAnnotation = parameter.getParameterAnnotation(AuthUser.class) != null;
    boolean isUserSessionClass = UserResponseDto.class.equals(parameter.getParameterType());
    return isAuthUserAnnotation && isUserSessionClass;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    return HttpSessionUtils.getUserFromSession(session);
  }

}
