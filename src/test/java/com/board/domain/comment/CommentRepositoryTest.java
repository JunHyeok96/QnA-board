package com.board.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.web.dto.comment.CommentRequestDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CommentRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;

  @Test
  public void 댓글저장_불러오기() {
    String comment_text = "입력된 댓글";
    Long postId = 1234L;
    Long userId = 122L;
    Long commentId = null;

    CommentRequestDto commentRequestDto = CommentRequestDto.builder()
        .comment(comment_text)
        .commentId(commentId)
        .userId(userId)
        .postId(postId)
        .build();

    commentRepository.save(commentRequestDto.toEntity());

    //when
    List<Comment> comments = commentRepository.findAll();
    Comment comment = comments.get(0);

    //then
    assertThat(comment.getComment()).isEqualTo(comment_text);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getPostId()).isEqualTo(postId);
    assertThat(comment.getCommentId()).isEqualTo(commentId);

  }

}