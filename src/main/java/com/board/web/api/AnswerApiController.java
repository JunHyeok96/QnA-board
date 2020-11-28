package com.board.web.api;

import com.board.config.Auth;
import com.board.service.AnswerService;
import com.board.web.dto.Answer.AnswerRequsetDto;
import com.board.web.dto.Answer.AnswerResponseDto;
import java.util.List;
import java.util.Map;
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

@Slf4j
@RequiredArgsConstructor
@RestController
public class AnswerApiController {

  private final AnswerService answerService;

  @PostMapping("/api/v1/answers")
  public void save(@RequestBody AnswerRequsetDto answerRequsetDto, HttpSession session) {
    answerService.save(answerRequsetDto, session);
  }

  @GetMapping("/api/v1/answers/{id}")
  public List<AnswerResponseDto> findAnswerByQuestionId(@PathVariable Long id) {
    return answerService.findByQuestion(id);
  }

  @Auth
  @PutMapping("/api/v1/answers/{id}")
  public void update(@PathVariable Long id, @RequestBody Map<String, String> map, HttpSession session) {
    answerService.update(id, map.get("content"), session);
  }

  @Auth
  @DeleteMapping("/api/v1/answers/{id}")
  public void delete(@PathVariable Long id, HttpSession session) {
    answerService.delete(id, session);
  }

}
