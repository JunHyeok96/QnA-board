package com.board.web.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserLoginRequestDto {
  private final String userId;
  private final String password;
}
