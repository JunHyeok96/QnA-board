package com.board.web.api;

import com.board.service.UserService;
import com.board.web.dto.user.UserRequestDto;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
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

  @PutMapping("/api/v1/user/{userId}")
  public void update(@PathVariable String userId, @RequestBody UserRequestDto updateUser,
      HttpSession session) throws IOException {
    userService.update(updateUser, session);
  }
}