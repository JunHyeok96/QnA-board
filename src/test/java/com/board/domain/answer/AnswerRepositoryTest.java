package com.board.domain.answer;

import static org.junit.Assert.*;

import com.board.web.dto.Answer.AnswerRequsetDto;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AnswerRepositoryTest {

  @Autowired
  private AnswerRepository answerRepository;

  @Test
  @DisplayName("답변 저장하기")
  public void answerTest() {
    //given
    String content = "답변입니다.";
    AnswerRequsetDto answerRequsetDto = AnswerRequsetDto.builder()
        .content(content)
        .build();
    //when
    Long id = answerRepository.save(answerRequsetDto.toEntity(null, null)).getId();
    //then
    Answer answer = answerRepository.findById(id).get();
    assertEquals(answer.getContent(), content);
  }

}