package com.board.web.api;

import com.board.config.Auth;
import com.board.config.Auth.Role;
import com.board.config.AuthUser;
import com.board.service.UserService;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserApiController {

  private final UserService userService;

  @Auth
  @PutMapping("/api/v1/user/{userId}")
  public void update(@PathVariable String userId, @RequestBody UserRequestDto updateUser,
      HttpSession session) throws IOException {
    userService.update(updateUser, session);
  }

  @GetMapping("/user")
  public String read(@AuthUser UserResponseDto responseDto) {
    log.info(responseDto.getUserId());
    return "post/read";
  }

  @Auth(role = Role.ADMIN)
  @PutMapping("/api/v1/admin/user/{userId}")
  public void updateByAdmin(@PathVariable String userId, @RequestBody UserRequestDto updateUser,
      HttpSession session) throws IOException {
    userService.updateByAdmin(updateUser, session);
  }
}