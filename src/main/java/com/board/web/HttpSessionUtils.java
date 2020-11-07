package com.board.web;

import com.board.domain.user.User;
import javax.servlet.http.HttpSession;

public class HttpSessionUtils {

  //인스턴스 생성 방지
  private HttpSessionUtils() {
  }

  public static final String USER_SESSION_KEY = "sessionedUser";

  public static boolean isLoginUser(HttpSession session) {
    Object sessionedUser = session.getAttribute(USER_SESSION_KEY);
    return sessionedUser == null ? false : true;
  }

  public static User getUserFromSession(HttpSession session) {
    return isLoginUser(session) ? (User) session.getAttribute(USER_SESSION_KEY) : null;
  }

  public static void logout(HttpSession session) {
    session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);
  }

}
