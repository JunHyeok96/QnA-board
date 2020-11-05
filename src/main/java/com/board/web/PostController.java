package com.board.web;

import com.board.service.PostService;
import com.board.web.dto.PostRequestDto;
import com.board.web.dto.PostResponseDto;
import com.board.web.dto.PostUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;

  @GetMapping("/post/list")
  public List<PostResponseDto> postList() {
    return postService.findAll();
  }

  @GetMapping("/post/{id}")
  public PostResponseDto postOne(@PathVariable Long id) {
    return postService.findById(id);
  }

  @PostMapping("/post/")
  public void save(@RequestBody PostRequestDto postRequestDto) {
    postService.save(postRequestDto);
  }

  @PutMapping("/post/{id}")
  public void update(@PathVariable Long id, @RequestBody PostUpdateDto postUpdateDto) {
    postService.update(id, postUpdateDto);
  }
}
