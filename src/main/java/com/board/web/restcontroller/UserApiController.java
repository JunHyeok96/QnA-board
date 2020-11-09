package com.board.web.restcontroller;

import com.board.domain.user.User;
import com.board.service.UserService;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserUpdateDto;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {

  private final UserService userService;

  @PutMapping("/user/{id}/update")
  public void update(@PathVariable Long id, @RequestBody UserUpdateDto updateUser,
      HttpSession session) {
    if (!HttpSessionUtils.isLoginUser(session)) {
      throw new IllegalStateException("로그인이 필요합니다.");
    } else {
      User user = HttpSessionUtils.getUserFromSession(session);
      userService.update(user.getId(), updateUser);
    }
  }

}
