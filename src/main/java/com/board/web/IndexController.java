package com.board.web;

import com.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class IndexController {

  private final PostService postService;

  @GetMapping("/")
  public String index(Model model){
    model.addAttribute("posts", postService.findAllQuestion());
    return "index";
  }





}
