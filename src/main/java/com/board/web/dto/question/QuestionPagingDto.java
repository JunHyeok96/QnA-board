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
public class QuestionPagingDto implements Serializable {

  private Long id;
  private String userId;
  private String title;
  private String content;
  private LocalDateTime createDate;
  private LocalDateTime modifiedDate;
  private int answerCount;

  @Builder
  public QuestionPagingDto(Question entry) {
    this.id = entry.getId();
    this.userId = entry.getUser() == null ? "guest" : entry.getUser().getUserId();
    this.title = entry.getTitle();
    this.content = entry.getContent();
    this.createDate = entry.getCreateDate();
    this.modifiedDate = entry.getModifiedDate();
    this.answerCount = entry.getAnswers().size();
  }

}
