package com.board.web.dto;

import com.board.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentRequestDto {

  private Long postId;
  private Long userId;
  private Long commentId;
  private String comment;

  @Builder
  public CommentRequestDto(Long postId, Long userId, Long commentId, String comment) {
    this.postId = postId;
    this.userId = userId;
    this.commentId = commentId;
    this.comment = comment;
  }

  public Comment toEntity() {
    return Comment.builder()
        .postId(postId)
        .commentId(commentId)
        .userId(userId)
        .comment(comment)
        .build();
  }
}
