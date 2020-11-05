package com.board.web.dto;

import com.board.domain.post.Post;
import com.board.domain.post.PostType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
public class PostRequestDto {
  private Long userId;
  private String title;
  private String content;
  private String postType;

  @Builder
  public PostRequestDto(Long userId, String title, String content, String postType){
    this.userId = userId;
    this.title = title;
    this.content = content;
    try{
      PostType.valueOf(postType);
      this.postType = postType;
    }catch(Exception e){
      throw new AssertionError("post_type 입력값이 잘못되었습니다.");

    }
  }

  public Post toEntity(){
    return Post.builder()
        .content(content)
        .title(title)
        .userId(userId)
        .postType(PostType.valueOf(postType))
        .build();
  }

}
