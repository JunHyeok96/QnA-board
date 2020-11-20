package com.board.web.dto.user;

import com.board.domain.user.User;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserResponseDto implements Serializable {

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
    this.name = entry.getName();
  }

    public User toEntity() {
    return User.builder()
        .id(this.id)
        .userId(this.userId)
        .email(this.email)
        .name(this.name)
        .password(this.password)
        .build();
  }

}
