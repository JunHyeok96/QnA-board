package com.board.web.dto;

import com.board.domain.post.Post;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PostResponseDto implements Serializable {
  private Long id;
  private Long userId;
  private String title;
  private String content;
  private String postType;
  private LocalDateTime createDate;
  private LocalDateTime modifiedDate;

  @Builder
  public PostResponseDto(Post entry) {
    this.id = entry.getId();
    this.userId = entry.getUserId();
    this.title = entry.getTitle();
    this.content = entry.getContent();
    this.postType = entry.getPostType().name();
    this.createDate = entry.getCreateDate();
    this.modifiedDate = entry.getModifiedDate();
  }

}
