package com.board.web.controller;

import com.board.config.Auth;
import com.board.domain.post.Post;
import com.board.service.PostService;
import com.board.web.PageUtils;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {

  private final PostService postService;
  private final int CONTENT_SIZE = 5;
  private final int PAGE_SIZE = 10;

  @GetMapping("/")
  public String index(Model model, HttpSession session) {
    Page<Post> posts = postService
        .findAllQuestion(PageRequest.of(0, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) posts.getTotalPages();
    model.addAttribute("posts", posts.getContent());
    model.addAttribute("currentPage", 1);
    model.addAttribute("page", PageUtils.getPageList(maxPage, 1, PAGE_SIZE));
    int nextPage = PageUtils.nextPage(maxPage, 1, PAGE_SIZE);
    if (maxPage > PAGE_SIZE) {
      model.addAttribute("next", nextPage);
    }
    return "index";
  }


  @GetMapping("/page")
  public String paging(Model model, @RequestParam("no") int page) {
    Page<Post> posts = postService.findAllQuestion(
        PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    if(posts.getContent().size() == 0){
      return "/";
    }
    int maxPage = (int) posts.getTotalPages();
    int previousPage = PageUtils.previousPage(page, PAGE_SIZE);
    int nextPage = PageUtils.nextPage(maxPage, page, PAGE_SIZE);
    model.addAttribute("posts", posts.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("page", PageUtils.getPageList(maxPage, page, PAGE_SIZE));
    if (page > PAGE_SIZE) {
      model.addAttribute("previous", previousPage);
    }
    if (!(page == maxPage) &&maxPage > PAGE_SIZE && nextPage <= maxPage) {
      model.addAttribute("next", nextPage);
    }
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
