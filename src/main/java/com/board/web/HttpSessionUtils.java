package com.board.web;

import com.board.domain.user.User;
import com.board.web.dto.user.UserResponseDto;
import javax.servlet.http.HttpSession;

public class HttpSessionUtils {

  public static final String USER_SESSION_KEY = "sessionedUser";

  //인스턴스 생성 방지
  private HttpSessionUtils() {
  }

  public static boolean isLoginUser(HttpSession session) {
    Object sessionedUser = session.getAttribute(USER_SESSION_KEY);
    return sessionedUser == null ? false : true;
  }

  public static UserResponseDto getUserFromSession(HttpSession session) {
    return isLoginUser(session) ? (UserResponseDto) session.getAttribute(USER_SESSION_KEY) : null;
  }

  public static void logout(HttpSession session) {
    session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);
  }

  public static void updateUser(User user, HttpSession session) {
    session.setAttribute(USER_SESSION_KEY, user);
  }

}
