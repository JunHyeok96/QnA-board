package com.board.web.controller;

import com.board.config.Auth;
import com.board.config.AuthUser;
import com.board.domain.question.Question;
import com.board.domain.question.exception.MismatchAuthor;
import com.board.service.AnswerService;
import com.board.service.QuestionService;
import com.board.web.HttpSessionUtils;
import com.board.web.PageUtils;
import com.board.web.dto.question.QuestionPagingDto;
import com.board.web.dto.question.QuestionResponseDto;
import com.board.web.dto.user.UserResponseDto;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
public class QuestionController {

  private final QuestionService questionService;
  private final AnswerService answerService;
  private final int CONTENT_SIZE = 5;

  @GetMapping("/questions/create")
  public String create() {
    return "post/create";
  }

  @GetMapping("/questions/{id}")
  public String read(@PathVariable Long id, Model model, @AuthUser UserResponseDto user) {
    QuestionResponseDto question = questionService.findById(id);
    model.addAttribute("question", question);
    model.addAttribute("answers", answerService.findByQuestion(id));
    if (user != null && question.getUserId().equals(user.getUserId())) {
      model.addAttribute("updateButton", true);
    }
    return "post/read";
  }

  @Auth
  @GetMapping("/questions/update/{id}")
  public String update(@PathVariable Long id, @AuthUser UserResponseDto user, Model model) {
    QuestionResponseDto question = questionService.findById(id);
    if (!question.getUserId().equals(user.getUserId())) {
      throw new MismatchAuthor("본인 게시물만 수정할 수 있습니다.");
    }
    model.addAttribute("question", question);
    return "post/update";
  }

  @Auth
  @GetMapping("/my-question")
  public String myQuestions(@RequestParam("no") int page, @AuthUser UserResponseDto user,
      Model model) {
    Page<Question> questions = questionService
        .findMyPosts(user,
            PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    PageUtils.pagingComponent(model, page, maxPage);
    model.addAttribute("link", "/my-question?no=");
    return "index";
  }

  @GetMapping("/search/user")
  public String userQuestions(@RequestParam("no") int page, @RequestParam("id") String userId,
      Model model) {
    Page<Question> questions = questionService
        .findByUserId(userId,
            PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    model.addAttribute("userId", userId);
    model.addAttribute("link", "/search/user?id=" + userId + "&no=");
    PageUtils.pagingComponent(model, page, maxPage);
    return "index";
  }

  @GetMapping("/search")
  public String search(@RequestParam("no") int page, String keyword, Model model) {
    Page<Question> questions = questionService.search(keyword.trim(),
        PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    model.addAttribute("keyword", keyword);
    PageUtils.pagingComponent(model, page, maxPage);
    model.addAttribute("link", "/search?keyword=" + keyword + "&no=");
    return "index";
  }

}
