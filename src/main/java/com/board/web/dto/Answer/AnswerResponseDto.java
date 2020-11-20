package com.board.web.dto.Answer;

import com.board.domain.answer.Answer;
import com.board.web.dto.user.UserResponseDto;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AnswerResponseDto {

  private long id;
  private long questionId;
  private String content;
  private String userId;
  private LocalDateTime createDate;
  private LocalDateTime modifiedDate;

  @Builder
  public AnswerResponseDto(Answer entry) {
    this.id = entry.getId();
    this.content = entry.getContent();
    this.createDate = entry.getCreateDate();
    this.modifiedDate = entry.getModifiedDate();
    this.questionId = entry.getQuestion().getId();
    this.userId = entry.getUser() == null ? "guest" : entry.getUser().getUserId();
  }

}
