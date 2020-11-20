package com.board.domain.user;

import com.board.domain.BaseTimeEntity;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 20)
  private String userId;

  @Column(nullable = false, length = 20)
  private String password;

  @Column(length = 20)
  private String name;

  @Column(length = 30)
  private String email;

  @Builder
  public User(Long id, String userId, String password, String name, String email) {
    this.id = id;
    this.userId = userId;
    this.password = password;
    this.name = name;
    this.email = email;
  }

  public User update(UserRequestDto updateDto) {
    this.password = updateDto.getPassword();
    this.name = updateDto.getName();
    this.email = updateDto.getEmail();
    return this;
  }

  public UserResponseDto makeSessionValue() {
    return new UserResponseDto(this);
  }

  public boolean matchPassword(String newPassword) {
    if (newPassword == null) {
      return false;
    }
    return newPassword.equals(password);
  }

  public boolean matchUserId(String newUserId) {
    if (newUserId == null) {
      return false;
    }
    return newUserId.equals(userId);
  }

}
