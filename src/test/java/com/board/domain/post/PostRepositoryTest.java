package com.board.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.web.dto.post.PostRequestDto;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Test
  public void 게시물정보불러오기() {

    String userId = "test-user";
    String title = "질문 입니다.";
    String content = "질문 내용입니다.";
    String post_type = "Q";
    String postId = null;
    LocalDateTime now = LocalDateTime.now();

    PostRequestDto postRequestDto = PostRequestDto.builder()
        .content(content)
        .title(title)
        .userId(userId)
        .postType(post_type)
        .build();

    postRepository.save(postRequestDto.toEntity());

    //when
    List<Post> posts = postRepository.findAll();
    Post post = posts.get(0);

    //then
    assertThat(title).isEqualTo(post.getTitle());
    assertThat(content).isEqualTo(post.getContent());
    assertThat(userId).isEqualTo(post.getUserId());
    assertThat(post_type).isEqualTo(post.getPostType().toString());
    assertThat(post.getCreateDate()).isAfter(now);
    assertThat(post.getModifiedDate()).isAfter(now);
  }

  @Test(expected = AssertionError.class)
  public void 잘못된게시글타입() {

    //when
    String userId = "test-user";
    String title = "질문 입니다.";
    String content = "질문 내용입니다.";
    String post_type = "K";  //잘못된 형식

    //then
    PostRequestDto.builder()
        .content(content)
        .title(title)
        .userId(userId)
        .postType(post_type)
        .build();
  }


}