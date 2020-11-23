package com.board.web.dto.Answer;

import com.board.domain.answer.Answer;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AnswerResponseDto {

  private long id;
  private long questionId;
  private String content;
  private String userId;
  private String createDate;
  private String modifiedDate;

  @Builder
  public AnswerResponseDto(Answer entry) {
    this.id = entry.getId();
    this.content = entry.getContent();
    this.createDate = entry.getCreateDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    this.modifiedDate = entry.getModifiedDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    this.questionId = entry.getQuestion().getId();
    this.userId = entry.getUser() == null ? "guest" : entry.getUser().getUserId();
  }

}
