package com.board.domain.comment;

import com.board.domain.BaseTimeEntity;
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
public class Comment extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;

  @Column(length = 20, nullable = false)
  private Long userId;

  @Column(length = 300, nullable = false)
  private String comment;

  @Column
  private Long commentId;

  @Builder
  public Comment(Long postId, Long userId, String comment, Long commentId){
    this.postId = postId;
    this.userId = userId;
    this.comment = comment;
    this.commentId = commentId;
  }

}
