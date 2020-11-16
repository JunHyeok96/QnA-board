package com.board.web.controller;

import com.board.config.Auth;
import com.board.config.SessionUser;
import com.board.domain.post.Post;
import com.board.service.PostService;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.post.PostResponseDto;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class PostController {

  private final PostService postService;

  @GetMapping("/post/create")
  public String postSave(@RequestParam("post-type") String postType,
      @RequestParam("post-id") String id, Model model) {
    model.addAttribute("postType", postType);
    model.addAttribute("id", id);
    return "post/create";
  }

  @GetMapping("/post/read/{id}")
  public String postRead(@PathVariable Long id, Pageable pageable, Model model, HttpSession session) {
    PostResponseDto post = postService.findById(id);
    model.addAttribute("post", post);
    model.addAttribute("answer", postService.findAnswer(id, pageable));
    SessionUser user = HttpSessionUtils.getUserFromSession(session);
    if(user != null && user.matchUserId(post.getUserId())){
      model.addAttribute("updateButton", true);
    }

    return "post/read";
  }

  @Auth
  @GetMapping("/post/update/{id}")
  public String postUpdate(@PathVariable Long id, HttpSession session,
      Model model) {
    Post post = postService.findByIdToEntity(id);
    post.matchAuthor(session);
    model.addAttribute("post", postService.findById(id));
    return "post/update";
  }
}
