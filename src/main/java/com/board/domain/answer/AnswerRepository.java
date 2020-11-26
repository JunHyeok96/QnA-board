package com.board.domain.answer;

import com.board.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

  Page<Answer> findByUserId(long userId, Pageable pageable);

  @EntityGraph(attributePaths = {"user"})
  List<Answer> findByQuestionId(long postId);

  void deleteByQuestion(Question question);

}
