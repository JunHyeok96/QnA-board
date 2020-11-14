package com.board.domain.post;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByPostType(PostType postType);

  List<Post> findByPostId(Long id, Pageable pageable);

  List<Post> findByUserIdAndPostType(String userId, PostType postType, Pageable pageable);

  Page<Post> findByPostType(PostType postType, Pageable pageable);

  void deleteByPostId(Long postId);

}
