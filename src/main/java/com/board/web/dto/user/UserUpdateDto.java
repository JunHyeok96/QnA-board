package com.board.web.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserUpdateDto {

  private String password;
  private String email;
  private String name;

  @Builder
  public UserUpdateDto(String password, String email, String name) {
    this.password = password;
    this.email = email;
    this.name = name;
  }

}
