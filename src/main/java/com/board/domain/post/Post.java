package com.board.domain.post;

import com.board.domain.BaseTimeEntity;
import com.board.domain.user.User;
import com.board.web.HttpSessionUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Post extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 300, nullable = false)
  private String title;

  @Column(length = 3000, nullable = false)
  private String content;

  @Column(length = 20, nullable = false)
  private String userId;

  @Column
  private Long postId;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "ENUM('Q', 'A')", nullable = false)
  private PostType postType;

  @Builder
  public Post(String title, String content, String userId, PostType postType, Long postId) {
    this.title = title;
    this.content = content;
    this.userId = userId;
    this.postType = postType;
    this.postId = postId;
  }

  public Post update(String title, String content) {
    this.title = title;
    this.content = content;
    return this;
  }

  public User matchAuthor(HttpSession session) {
    if (!HttpSessionUtils.isLoginUser(session)) {
      throw new IllegalStateException("로그인되지 않았습니다.");
    }
    User user = HttpSessionUtils.getUserFromSession(session);
    if (user.matchUserId(this.userId)) {
      return user;
    } else {
      throw new IllegalStateException("본인의 게시물이 아닙니다!");
    }
  }
}
