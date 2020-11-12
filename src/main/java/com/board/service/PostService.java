package com.board.service;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.domain.post.PostType;
import com.board.domain.post.exception.PostNotFoundException;
import com.board.domain.user.User;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.post.PostRequestDto;
import com.board.web.dto.post.PostResponseDto;
import com.board.web.dto.post.PostUpdateDto;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;

  @Transactional(readOnly = true)
  public List<PostResponseDto> findAllQuestion(Pageable pageable) {
    return postRepository.findByPostType(PostType.Q, pageable)
        .stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PostResponseDto findById(Long id) {
    return postRepository.findById(id).map(PostResponseDto::new).get();
  }

  @Transactional(readOnly = true)
  public Post findByIdToEntity(Long id) {
    return postRepository.findById(id).get();
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> findAnswer(Long postId, Pageable pageable) {
    return postRepository.findByPostId(postId, pageable)
        .stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> findMyPosts(HttpSession session, Pageable pageable) {
    String userId = HttpSessionUtils.getUserFromSession(session).getUserId();
    return postRepository.findByUserIdAndPostType(userId, PostType.Q, pageable)
        .stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> findMyAnswer(HttpSession session, Pageable pageable) {
    String userId = HttpSessionUtils.getUserFromSession(session).getUserId();
    return postRepository.findByUserIdAndPostType(userId, PostType.A, pageable)
        .stream()
        .map(PostResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public Long save(PostRequestDto postRequestDto) {
    Long id = postRepository.save(postRequestDto.toEntity()).getId();
    return id;
  }

  @Transactional
  public Long update(Long id, PostUpdateDto postUpdateDto, HttpSession session) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시물"));
    User user = post.matchAuthor(session);
    post.update(postUpdateDto.getTitle(), postUpdateDto.getContent());
    log.info(user.getUserId() + "님의 id : " + id + " 게시글이 업데이트되었습니다.");
    return id;
  }

  @Transactional
  public void delete(Long id, HttpSession session) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시물"));
    User user = post.matchAuthor(session);
    postRepository.deleteByPostId(id);
    postRepository.deleteById(id);
    log.info(user.getUserId() + "님의 id : " + id + " 게시글이 삭제되었습니다.");
  }


}
