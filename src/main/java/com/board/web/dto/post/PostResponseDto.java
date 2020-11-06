package com.board.web.dto.post;

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
  private String userId;
  private String title;
  private String content;
  private LocalDateTime createDate;
  private LocalDateTime modifiedDate;

  @Builder
  public PostResponseDto(Post entry) {
    this.id = entry.getId();
    this.userId = entry.getUserId();
    this.title = entry.getTitle();
    this.content = entry.getContent();
    this.createDate = entry.getCreateDate();
    this.modifiedDate = entry.getModifiedDate();
  }

}
