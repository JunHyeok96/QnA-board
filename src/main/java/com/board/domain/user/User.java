package com.board.domain.user;

import com.board.config.SessionUser;
import com.board.domain.BaseTimeEntity;
import com.board.web.HttpSessionUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

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
  public User(String userId, String password, String name, String email) {
    this.userId = userId;
    this.password = password;
    this.name = name;
    this.email = email;
  }

  public User update(String password, String name, String email) {
    this.password = password;
    this.name = name;
    this.email = email;
    return this;
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

  public SessionUser makeSessionUser(){
    return new SessionUser(userId, email, name, password);
  }

}
