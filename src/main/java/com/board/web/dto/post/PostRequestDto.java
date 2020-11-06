package com.board.web.dto.post;

import com.board.domain.post.Post;
import com.board.domain.post.PostType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
public class PostRequestDto {

  private String userId;
  private String title;
  private String content;
  private String postType;
  private Long postId;

  @Builder
  public PostRequestDto(String userId, String title, String content, String postType, Long postId) {
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.postId = postId;
    try {
      PostType.valueOf(postType);
      this.postType = postType;
    } catch (Exception e) {
      throw new AssertionError("post_type 입력값이 잘못되었습니다.");

    }
  }

  public Post toEntity() {
    return Post.builder()
        .content(content)
        .title(title)
        .userId(userId)
        .postType(PostType.valueOf(postType))
        .postId(postId)
        .build();
  }

}
