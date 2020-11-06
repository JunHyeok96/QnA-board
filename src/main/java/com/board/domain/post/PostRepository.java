package com.board.domain.post;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByPostType(PostType postType);
  List<Post> findByPostId(Long id);
}
