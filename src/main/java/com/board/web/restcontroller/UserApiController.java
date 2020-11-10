package com.board.web.restcontroller;

import com.board.domain.user.User;
import com.board.service.UserService;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserUpdateDto;
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
      HttpSession session, HttpServletResponse response) {
    try {
      userService.update(userId, updateUser, session);
    } catch (Exception e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }
}
