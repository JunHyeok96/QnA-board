package com.board.domain.post;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByPostTypeOrderByCreateDateDesc(PostType postType);

  List<Post> findByPostId(Long id);

  List<Post> findByUserIdAndPostType(String userId, PostType postType);

}
