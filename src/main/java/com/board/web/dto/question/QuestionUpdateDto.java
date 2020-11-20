package com.board.web.dto.question;

import com.board.domain.answer.Answer;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionUpdateDto {

  private String title;
  private String content;
  private Answer answer;

  @Builder
  public QuestionUpdateDto(String title, String content, Answer answer) {
    this.title = title;
    this.content = content;
    this.answer = answer;
  }
}
