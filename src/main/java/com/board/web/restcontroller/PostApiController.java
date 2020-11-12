package com.board.web.restcontroller;

import com.board.config.Auth;
import com.board.service.PostService;
import com.board.web.dto.post.PostRequestDto;
import com.board.web.dto.post.PostResponseDto;
import com.board.web.dto.post.PostUpdateDto;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostApiController {

  private final PostService postService;

  @GetMapping("/post/list")
  public List<PostResponseDto> postList() {
    return postService.findAllQuestion();
  }

  @GetMapping("/post/{id}")
  public PostResponseDto postOne(@PathVariable Long id) {
    return postService.findById(id);
  }

  @Auth
  @GetMapping("/post/answer/{postId}")
  public List<PostResponseDto> answerList(@PathVariable Long postId) {
    return postService.findAnswer(postId);
  }

  @Auth
  @GetMapping("/post/answer/my-answer")
  public List<PostResponseDto> myAnswerList(HttpSession session, HttpServletResponse response) {
    try {
      return postService.findMyAnswer(session);
    } catch (IllegalStateException e) {
      response.setStatus(401);
      return null;
    }
  }

  @GetMapping("/post/answer/my-question")
  public List<PostResponseDto> myQuestionList(HttpSession session, HttpServletResponse response) {
    try {
      return postService.findMyPosts(session);
    } catch (IllegalStateException e) {
      response.setStatus(401);
      return null;
    }
  }

  @PostMapping("/post/")
  public void save(@RequestBody PostRequestDto postRequestDto) {
    postService.save(postRequestDto);
  }

  @Auth
  @PutMapping("/post/{id}")
  public void update(@PathVariable Long id, @RequestBody PostUpdateDto postUpdateDto,
      HttpSession session, HttpServletResponse response) {
    try {
      postService.update(id, postUpdateDto, session);
    } catch (IllegalStateException e) {
      response.setStatus(401);
    }
  }

  @Auth
  @DeleteMapping("/post/{id}")
  public void delete(@PathVariable Long id, HttpSession session, HttpServletResponse response) {
    try {
      postService.delete(id, session);
    } catch (IllegalStateException e) {
      response.setStatus(401);
    }
  }

}
