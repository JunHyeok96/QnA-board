package com.board.web.dto.question;

import com.board.domain.answer.Answer;
import com.board.domain.question.Question;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
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
  private List<Answer> answers;

  @Builder
  public QuestionResponseDto(Question entry) {
    this.id = entry.getId();
    this.userId = entry.getUser() == null ? "guest" : entry.getUser().getUserId();
    this.title = entry.getTitle();
    this.content = entry.getContent();
    this.createDate = entry.getCreateDate();
    this.modifiedDate = entry.getModifiedDate();
    this.answers = entry.getAnswers();
  }

}