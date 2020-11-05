package com.board.service;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.web.dto.PostRequestDto;
import com.board.web.dto.PostResponseDto;
import com.board.web.dto.PostUpdateDto;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;

  @Transactional(readOnly = true)
  public List<PostResponseDto> findAll() {
    return postRepository.findAll()
        .stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PostResponseDto findById(Long id) {
    return postRepository.findById(id).map(PostResponseDto::new).get();
  }

  @Transactional
  public Long save(PostRequestDto postRequestDto) {
    Long id = postRepository.save(postRequestDto.toEntity()).getId();
    return id;
  }

  @Transactional
  public Long update(Long id, PostUpdateDto postUpdateDto) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물"));
    post.update(postUpdateDto.getTitle(), postUpdateDto.getContent());
    return id;
  }
}
