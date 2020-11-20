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

  @GetMapping("/answers/create")
  public String answerCreate(int id, Model model) {
    model.addAttribute("id", id);
    return "post/answer";
  }


  @GetMapping("/questions/{id}")
  public String read(@PathVariable Long id, Pageable pageable, Model model,
      HttpSession session) {
    Question question = questionService.findByIdToEntity(id);
    model.addAttribute("question", questionService.findById(id));
    model.addAttribute("answers", answerService.findByQuestion(id, pageable).stream().map(
        AnswerResponseDto::new).collect(Collectors.toList()));
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    if (user != null && question.matchAuthor(user.getUserId())) {
      model.addAttribute("updateButton", true);
    }
    return "post/read";
  }

  @Auth
  @GetMapping("/questions/update/{id}")
  public String update(@PathVariable Long id, HttpSession session,
      Model model) {
    Question question = questionService.findByIdToEntity(id);
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    if (!question.matchAuthor(user.getUserId())) {
      throw new MissmatchAuthor("본인 게시물만 수정할 수 있습니다.");
    }
    model.addAttribute("question", new QuestionResponseDto(question));
    return "post/update";
  }
}
