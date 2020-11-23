package com.board.web.controller;

import com.board.config.Auth;
import com.board.domain.question.Question;
import com.board.domain.question.exception.MissmatchAuthor;
import com.board.domain.user.User;
import com.board.service.AnswerService;
import com.board.service.QuestionService;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.Answer.AnswerResponseDto;
import com.board.web.dto.question.QuestionResponseDto;
import com.board.web.dto.user.UserResponseDto;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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

  @GetMapping("/questions/create")
  public String create(HttpSession session) {
    return "post/create";
  }

  @GetMapping("/questions/{id}")
  public String read(@PathVariable Long id, Pageable pageable, Model model,
      HttpSession session) {
    QuestionResponseDto question = questionService.findById(id);
    model.addAttribute("question", question);
    model.addAttribute("answers", answerService.findByQuestion(id));
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    if (user != null && question.getUserId().equals(user.getUserId())) {
      model.addAttribute("updateButton", true);
    }
    return "post/read";
  }

  @Auth
  @GetMapping("/questions/update/{id}")
  public String update(@PathVariable Long id, HttpSession session,
      Model model) {
    QuestionResponseDto question = questionService.findById(id);
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    if (!question.getUserId().equals(user.getUserId())) {
      throw new MissmatchAuthor("본인 게시물만 수정할 수 있습니다.");
    }
    model.addAttribute("question", question);
    return "post/update";
  }

}
