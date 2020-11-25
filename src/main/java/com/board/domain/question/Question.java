package com.board.domain.question;

import com.board.domain.BaseTimeEntity;
import com.board.domain.answer.Answer;
import com.board.domain.user.User;
import com.board.web.dto.question.QuestionUpdateDto;
import java.util.LinkedHashSet;
import java.util.Set;
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
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@NoArgsConstructor
@Getter
@Entity
public class Question extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 500, nullable = false)
  private String title;

  @Column(nullable = false)
  @Lob
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_user", value = ConstraintMode.NO_CONSTRAINT))
  private User user;

  @BatchSize(size = 5)
  @OneToMany(mappedBy = "question")
  Set<Answer> answers = new LinkedHashSet<>();

  @Builder
  public Question(String title, String content, User user) {
    this.title = title;
    this.content = content;
    this.user = user;
  }

  public Question update(QuestionUpdateDto dto) {
    this.title = dto.getTitle();
    this.content = dto.getContent();
    return this;
  }

  public boolean matchAuthor(String userId) {
    if (this.user == null || userId.isEmpty()) {
      return false;
    }
    if (userId.equals(this.user.getUserId())) {
      return true;
    } else {
      return false;
    }
  }
}
