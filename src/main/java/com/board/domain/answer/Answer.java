package com.board.domain.answer;

import com.board.domain.BaseTimeEntity;
import com.board.domain.question.Question;
import com.board.domain.user.User;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Answer extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  @Lob
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_question", value = ConstraintMode.NO_CONSTRAINT))
  private Question question;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_user", value = ConstraintMode.NO_CONSTRAINT))
  private User user;

  @Builder
  public Answer(String content, Question question, User user) {
    this.content = content;
    this.question = question;
    this.user = user;
  }

  public Answer update(String content) {
    this.content = content;
    return this;
  }
}
