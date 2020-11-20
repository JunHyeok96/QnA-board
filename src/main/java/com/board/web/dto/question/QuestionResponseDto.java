package com.board.web.dto.question;

import com.board.domain.question.Question;
import com.board.web.dto.user.UserResponseDto;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class QuestionResponseDto implements Serializable {

  private Long id;
  private String userId;
  private String title;
  private String content;
  private LocalDateTime createDate;
  private LocalDateTime modifiedDate;

  @Builder
  public QuestionResponseDto(Question entry) {
    this.id = entry.getId();
    this.userId = entry.getUser() == null ? "guest" : entry.getUser().getUserId();
    this.title = entry.getTitle();
    this.content = entry.getContent();
    this.createDate = entry.getCreateDate();
    this.modifiedDate = entry.getModifiedDate();
  }

}
