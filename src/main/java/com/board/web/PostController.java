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
public class PostController {

  private final PostService postService;

  @GetMapping("/post/create")
  public String postSave(@RequestParam("post-type") String postType, @RequestParam("post-id") String id, Model model){
    model.addAttribute("postType", postType);
    model.addAttribute("id", id);
    return "post/create";
  }

  @GetMapping("/post/read/{id}")
  public String postRead(@PathVariable Long id, Model model){
    model.addAttribute("post", postService.findById(id));
    model.addAttribute("answer", postService.findAnswer(id));
    return "post/read";
  }

  @GetMapping("/post/update/{id}")
  public String postUpdate(@PathVariable Long id, Model model){
    model.addAttribute("post", postService.findById(id));
    return "post/update";
  }
}
