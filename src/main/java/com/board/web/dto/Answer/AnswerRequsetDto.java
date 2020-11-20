package com.board.web.dto.Answer;

import com.board.domain.answer.Answer;
import com.board.domain.question.Question;
import com.board.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AnswerRequsetDto {

  private Long questionId;
  private String content;

  @Builder
  public AnswerRequsetDto(String content, Long questionId) {
    this.content = content;
    this.questionId = questionId;
  }

  public Answer toEntity(Question question, User user) {
    return Answer.builder()
        .content(content)
        .question(question)
        .user(user)
        .build();
  }
}
