package com.board.web.dto;

import com.board.domain.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
  private Long id;
  private String userId;
  private String name;
  private String password;
  private String email;

  public UserResponseDto(User entry) {
    this.id = entry.getId();
    this.email = entry.getEmail();
    this.userId = entry.getUserId();
    this.password = entry.getPassword();
    this.name = entry.getPassword();
  }

  @Override
  public String toString() {
    return "UserResponseDto{" +
        "id=" + id +
        ", userId='" + userId + '\'' +
        ", name='" + name + '\'' +
        ", password='" + password + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
