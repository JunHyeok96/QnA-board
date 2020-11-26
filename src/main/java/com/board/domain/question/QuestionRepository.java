package com.board.domain.question;


import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  @EntityGraph(attributePaths = {"user"})
  Optional<Question> findById(Long id);

  @EntityGraph(attributePaths = {"user"})
  Page<Question> findByUserId(Long userId, Pageable pageable);

  @EntityGraph(attributePaths = {"user"})
  @Query("select q from Question q \n"
      + "inner join "
      + "User u "
      + "on q.user = u "
      + "where u.userId = ?1")
  Page<Question> findByUserId(String userId, Pageable pageable);

  @EntityGraph(attributePaths = {"user"})
  Page<Question> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"answers", "user"})
  List<Question> findAll();

  @EntityGraph(attributePaths = {"user"})
  @Query(
      "SELECT distinct q FROM Question q  WHERE q.content LIKE %?1% or q.title LIKE %?1%"
          + "order by CASE WHEN q.title LIKE %?1% THEN 1 ELSE 2 END"
          + "        , CASE WHEN q.content LIKE %?1%    THEN 1 ELSE 2 END")
  Page<Question> search(String keyword, Pageable pageable);

}
