package com.board.domain.answer;

import com.board.domain.question.Question;
import com.board.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

  Page<Answer> findByUser(User user, Pageable pageable);

  Page<Answer> findByUserId(long userId, Pageable pageable);

  Page<Answer> findByQuestion(Question question, Pageable pageable);

  Page<Answer> findByQuestionId(long questionId, Pageable pageable);

  long countAnswerByQuestion_Id(long questionId);

  @EntityGraph(attributePaths = {"user"})
  List<Answer> findByQuestionId(long postId);

  void deleteByQuestion(Question question);

}
