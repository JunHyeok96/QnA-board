package com.board.web.dto.user;

import com.board.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserRequestDto {

  private String userId;
  private String name;
  private String password;
  private String email;

  @Builder
  public UserRequestDto(String userId, String name, String password, String email) {
    this.email = email;
    this.userId = userId;
    this.password = password;
    this.name = name;
  }

  public User toEntity() {
    return User.builder()
        .userId(this.userId)
        .email(this.email)
        .name(this.name)
        .password(this.password)
        .build();
  }

  @Override
  public String toString() {
    return "User{" +
        "userId='" + userId + '\'' +
        ", name='" + name + '\'' +
        ", password='" + password + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
