package com.board.domain;

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
public class User{

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

  public User update(String userId, String password, String name, String email){
    this.userId = userId;
    this.password = password;
    this.name = name;
    this.email = email;
    return this;
  }
}
