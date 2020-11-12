package com.board.web.restcontroller;

import com.board.domain.user.User;
import com.board.domain.user.exception.UserMismatchException;
import com.board.domain.user.exception.UserNotFoundException;
import com.board.service.UserService;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserUpdateDto;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {

  private final UserService userService;

  @PutMapping("/user/{userId}/update")
  public void update(@PathVariable String userId, @RequestBody UserUpdateDto updateUser,
      HttpSession session, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      userService.update(userId, updateUser, session);
      response.sendRedirect("/");
    } catch (UserMismatchException e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    } catch (UserNotFoundException e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.sendRedirect("/user/login");
    } catch (LoginException e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      String referer = request.getHeader("referer");
      response.sendRedirect("referer");
    }
  }
}