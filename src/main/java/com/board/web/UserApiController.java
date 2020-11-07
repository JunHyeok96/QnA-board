package com.board.web;

import com.board.domain.user.User;
import com.board.service.UserService;
import com.board.web.dto.user.UserUpdateDto;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {

  private final UserService userService;

  @PutMapping("/user/{id}/update")
  public String update(@PathVariable Long id, @RequestBody UserUpdateDto updateUser,
      HttpSession session) {
    if (!HttpSessionUtils.isLoginUser(session)) {
      return "redirect:/user/login";
    } else {
      User user = HttpSessionUtils.getUserFromSession(session);
      userService.update(user.getId(), updateUser);
      return "redirect:/";
    }
  }

}
