package com.board.web;

import com.board.domain.user.User;
import com.board.web.dto.user.UserResponseDto;
import javax.servlet.http.HttpSession;

public class HttpCookieUtils {

  public static final String USER_COOKIE_KEY = "loginCookie";
  //인스턴스 생성 방지
  private HttpCookieUtils() {
  }



}
