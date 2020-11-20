package com.board.web.dto.question;

import com.board.domain.question.Question;
import com.board.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionRequestDto {

  private String title;
  private String content;

  @Builder
  public QuestionRequestDto(String title, String content) {
    this.title = title;
    this.content = content;
  }

  public Question toEntity(User user) {
    return Question.builder()
        .content(content)
        .title(title)
        .user(user)
        .build();
  }

}
