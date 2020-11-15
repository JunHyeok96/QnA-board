package com.board.config;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionUser implements Serializable {

  private String userId;
  private String email;
  private String name;
  private String password;

  @Builder
  public SessionUser(String userId, String email, String name, String password) {
    this.userId = userId;
    this.email = email;
    this.name = name;
    this.password = password;
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
