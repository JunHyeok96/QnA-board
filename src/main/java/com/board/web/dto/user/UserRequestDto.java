package com.board.web.dto.user;

import com.board.domain.user.User;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserRequestDto implements Serializable {

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

  public boolean isVaild() {
    if (this.userId.isEmpty() || this.password.isEmpty()) {
      return false;
    }
    return true;
  }


}
