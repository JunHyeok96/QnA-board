package com.board.web.controller;

import com.board.config.Auth;
import com.board.domain.answer.Answer;
import com.board.domain.question.Question;
import com.board.service.AnswerService;
import com.board.service.QuestionService;
import com.board.web.HttpSessionUtils;
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
  private final int CONTENT_SIZE = 5;

  @GetMapping("/")
  public String index(Model model, HttpSession session) {
    Page<Question> questions = questionService
        .findQuestionAll(PageRequest.of(0, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) questions.getTotalPages();
    model.addAttribute("questions",
        questions.stream().map(QuestionPagingDto::new).collect(Collectors.toList()));
    PageUtils.pagingComponent(model, 1, maxPage);
    model.addAttribute("link", "/page?no=");
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
    PageUtils.pagingComponent(model, page, maxPage);
    model.addAttribute("link", "/page?no=");
    return "index";
  }

}