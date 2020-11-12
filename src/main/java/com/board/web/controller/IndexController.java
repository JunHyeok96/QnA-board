package com.board.web.controller;

import com.board.config.Auth;
import com.board.service.PostService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

  private final PostService postService;

  @GetMapping("/")
  public String index(Model model, Pageable pageable) {
    model.addAttribute("posts", postService.findAllQuestion(pageable));
    return "index";
  }

  @Auth
  @GetMapping("/post/answer/my-answer")
  public String myAnswerList(HttpSession session, Pageable pageable, Model model) {
    model.addAttribute("posts", postService.findMyAnswer(session, pageable));
    return "index";
  }

  @Auth
  @GetMapping("/post/answer/my-question")
  public String myQuestionList(HttpSession session, Pageable pageable, Model model) {
    model.addAttribute("posts", postService.findMyPosts(session, pageable));
    return "index";
  }

}
