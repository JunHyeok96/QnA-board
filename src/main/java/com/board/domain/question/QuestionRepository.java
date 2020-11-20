package com.board.domain.question;


import com.board.domain.answer.Answer;
import com.board.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  Page<Question> findByUser(User user, Pageable pageable);

  Page<Question> findByUserId(long userId, Pageable pageable);

}
