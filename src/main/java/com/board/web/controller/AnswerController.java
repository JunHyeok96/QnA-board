package com.board.web.controller;

import com.board.config.Auth;
import com.board.domain.answer.Answer;
import com.board.domain.question.exception.MissmatchAuthor;
import com.board.domain.user.exception.UserMismatchException;
import com.board.service.AnswerService;
import com.board.web.HttpSessionUtils;
import com.board.web.PageUtils;
import com.board.web.dto.Answer.AnswerResponseDto;
import com.board.web.dto.user.UserResponseDto;
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
public class AnswerController {

  private final AnswerService answerService;
  private final int CONTENT_SIZE = 5;

  @Auth
  @GetMapping("/my-answer")
  public String myAnswers(@RequestParam("no") int page, HttpSession session, Model model) {
    Page<Answer> answers = answerService
        .findMyAnswers(session,
            PageRequest.of(page - 1, CONTENT_SIZE, Sort.by("createDate").descending()));
    int maxPage = (int) answers.getTotalPages();
    model.addAttribute("answers",
        answers.stream().map(AnswerResponseDto::new).collect(Collectors.toList()));
    PageUtils.pagingComponent(model, page, maxPage);
    return "myAnswer";
  }

  @Auth
  @GetMapping("/my-answer/update")
  public String update(@RequestParam long id, HttpSession session, Model model) {
    AnswerResponseDto answer = answerService.findById(id);
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    if (user.getUserId().equals(answer.getUserId())) {
      model.addAttribute("answer", answer);
    } else {
      throw new MissmatchAuthor("본인 답글만 수정할 수 있습니다.");
    }
    return "post/updateAnswer";
  }
}
