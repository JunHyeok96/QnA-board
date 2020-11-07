package com.board.web;

import com.board.service.PostService;
import com.board.web.dto.post.PostRequestDto;
import com.board.web.dto.post.PostResponseDto;
import com.board.web.dto.post.PostUpdateDto;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

  @GetMapping("/post/answer/{postId}")
  public List<PostResponseDto> answerList(@PathVariable Long postId) {
    return postService.findAnswer(postId);
  }

  @PostMapping("/post/")
  public void save(@RequestBody PostRequestDto postRequestDto) {
    postService.save(postRequestDto);
  }

  @PutMapping("/post/{id}")
  public void update(@PathVariable Long id, @RequestBody PostUpdateDto postUpdateDto,
      HttpSession session) {
    postService.update(id, postUpdateDto, session);
  }

  @DeleteMapping("/post/{id}")
  public void delete(@PathVariable Long id, HttpSession session) {
    postService.delete(id, session);
  }
}
