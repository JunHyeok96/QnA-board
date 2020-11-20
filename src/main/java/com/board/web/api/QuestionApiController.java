package com.board.web.api;

import com.board.config.Auth;
import com.board.service.QuestionService;
import com.board.web.dto.question.QuestionRequestDto;
import com.board.web.dto.question.QuestionResponseDto;
import com.board.web.dto.question.QuestionUpdateDto;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class QuestionApiController {

  private final QuestionService questionService;

  @PostMapping("/api/v1/questions")
  public void save(@RequestBody QuestionRequestDto questionRequestDto, HttpSession session) {
    questionService.save(questionRequestDto, session);
  }

  @GetMapping("/api/v1/questions/{id}")
  public QuestionResponseDto questionOne(@PathVariable Long id) {
    return questionService.findById(id);
  }

  @Auth
  @PutMapping("/api/v1/questions/{id}")
  public void update(@PathVariable Long id, @RequestBody QuestionUpdateDto questionUpdateDto,
      HttpSession session) {
    questionService.update(id, questionUpdateDto, session);
  }

  @Auth
  @DeleteMapping("/api/v1/questions/{id}")
  public void delete(@PathVariable Long id, HttpSession session, HttpServletResponse response) {
    questionService.delete(id, session);
  }

  @GetMapping("/api/v1/questions/list")
  public List<QuestionResponseDto> questions() {
    return questionService.findQuestionAll();
  }

}
