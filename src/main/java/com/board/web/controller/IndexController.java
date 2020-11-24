package com.board.web.controller;

import com.board.config.Auth;
import com.board.domain.answer.Answer;
import com.board.domain.question.Question;
import com.board.service.AnswerService;
import com.board.service.QuestionService;
import com.board.web.PageUtils;
import com.board.web.dto.Answer.AnswerResponseDto;
import com.board.web.dto.question.QuestionPagingDto;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {

  private final QuestionService questionService;
  private final AnswerService answerService;
  private final int CONTENT_SIZE = 5;
  private final int PAGE_SIZE = 10;

  @GetMapping("/")
  public String index(Model model) {
    Page<Question> questions = questionService
        .findQuestionAll(PageRequest.of(0, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    pagingComponent(model, 1, maxPage);
    return "index";
  }

  @GetMapping("/page")
  public String questionPage(Model model, @RequestParam("no") int page) {
    Page<Question> questions = questionService.findQuestionAll(
        PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    if (questions.getContent().size() == 0) {
      return "/";
    }
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    pagingComponent(model, page, maxPage);
    return "index";
  }

  @Auth
  @GetMapping("/my-answer")
  public String myAnswers(@RequestParam("no") int page, HttpSession session, Model model) {
    Page<Answer> answers = answerService
        .findMyAnswers(session,
            PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) answers.getTotalPages();
    model.addAttribute("answers",
        answers.stream().map(AnswerResponseDto::new).collect(Collectors.toList()));
    pagingComponent(model, page, maxPage);
    return "myAnswer";
  }

  @Auth
  @GetMapping("/my-question")
  public String myQuestions(@RequestParam("no") int page, HttpSession session, Model model) {
    Page<Question> questions = questionService
        .findMyPosts(session,
            PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    pagingComponent(model, page, maxPage);
    return "myQuestion";
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
    pagingComponent(model, page, maxPage);
    return "userQuestion";
  }

  @GetMapping("/search")
  public String search(@RequestParam("no") int page, String keyword, Model model) {
    Page<Question> questions = questionService.search(keyword.trim(),
        PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    model.addAttribute("keyword", keyword);
    pagingComponent(model, page, maxPage);
    return "search";
  }

  public Model pagingComponent(Model model, int page, int maxPage) {
    int previousPage = PageUtils.previousPage(page, PAGE_SIZE);
    int nextPage = PageUtils.nextPage(maxPage, page, PAGE_SIZE);
    model.addAttribute("currentPage", page);
    model.addAttribute("page", PageUtils.getPageList(maxPage, page, PAGE_SIZE));
    if (page > PAGE_SIZE) {
      model.addAttribute("previous", previousPage);
    }
    if (!(page == maxPage) && maxPage > PAGE_SIZE && nextPage <= maxPage) {
      model.addAttribute("next", nextPage);
    }
    return model;
  }

}