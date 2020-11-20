package com.board.web.dto.question;

import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionUpdateDto {

  private String title;
  private String content;

  @Builder
  public QuestionUpdateDto(String title, String content) {
    this.title = title;
    this.content = content;
  }
}
