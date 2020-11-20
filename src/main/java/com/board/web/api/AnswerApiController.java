package com.board.web.api;

import com.board.config.Auth;
import com.board.domain.answer.Answer;
import com.board.service.AnswerService;
import com.board.web.dto.Answer.AnswerRequsetDto;
import com.board.web.dto.Answer.AnswerResponseDto;
import com.board.web.dto.question.QuestionRequestDto;
import com.board.web.dto.question.QuestionResponseDto;
import com.board.web.dto.question.QuestionUpdateDto;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AnswerApiController {

  private final AnswerService answerService;

  @PostMapping("/api/v1/answers")
  public void save(@RequestBody AnswerRequsetDto answerRequsetDto, HttpSession session) {
    answerService.save(answerRequsetDto, session);
  }

  @GetMapping("/api/v1/answers/{id}")
  public List<AnswerResponseDto> findAnwser(@PathVariable Long id) {
    return answerService.findByQuestion(id);
  }

  @Auth
  @PutMapping("/api/v1/answers/{id}")
  public void update(@PathVariable Long id, String content, HttpSession session) {
    answerService.update(id, content, session);
  }

  @Auth
  @DeleteMapping("/api/v1/answers/{id}")
  public void delete(@PathVariable Long id, HttpSession session) {
    answerService.delete(id, session);
  }

}
