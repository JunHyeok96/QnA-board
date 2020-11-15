package com.board.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.board.config.SessionUser;
import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.domain.post.exception.MissmatchAuthor;
import com.board.domain.user.User;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.post.PostUpdateDto;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PostServiceTest {

  private PostService postService;

  private static MockedStatic<HttpSessionUtils> mockedSessionUtils;
  private MockHttpSession mockHttpSession;

  @Mock
  private PostRepository postRepository;

  @Mock
  private Post mockPost;

  @BeforeClass
  public static void beforeClass() {
    mockedSessionUtils = mockStatic(HttpSessionUtils.class);
  }

  @AfterClass
  public static void afterClass() {
    mockedSessionUtils.close();
  }


  @Test
  @DisplayName("게시글 수정")
  public void updatePost() {
    //given
    postService = new PostService(postRepository);
    Optional<Post> mockOptional = mock(Optional.class);
    SessionUser mockUser = mock(SessionUser.class);
    PostUpdateDto mockPostUpdateDto = mock(PostUpdateDto.class);
    when(postRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockPost);
    when(mockPost.matchAuthor(any())).thenReturn(mockUser);

    //when
    postService.update(0L, mockPostUpdateDto, mockHttpSession);

    //then
    verify(mockPost).update(any(), any());
  }

  @Test(expected = MissmatchAuthor.class)
  @DisplayName("게시글 수정 - 타인의 게시물")
  public void failUpdatePost() {
    //given
    postService = new PostService(postRepository);
    Optional<Post> mockOptional = mock(Optional.class);
    PostUpdateDto mockPostUpdateDto = mock(PostUpdateDto.class);
    when(postRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockPost);
    when(mockPost.matchAuthor(any())).thenThrow(new MissmatchAuthor("로그인 실패"));

    //when
    postService.update(0L, mockPostUpdateDto, mockHttpSession);

    //then
    verify(mockPost, never()).update(any(), any());
  }

  @Test
  @DisplayName("게시글 삭제")
  public void deletePost() {
    //given
    postService = new PostService(postRepository);
    Optional<Post> mockOptional = mock(Optional.class);
    SessionUser mockUser = mock(SessionUser.class);
    when(postRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockPost);
    when(mockPost.matchAuthor(any())).thenReturn(mockUser);

    //when
    postService.delete(0L, mockHttpSession);

    //then
    verify(postRepository).deleteById(any());
  }

  @Test(expected = MissmatchAuthor.class)
  @DisplayName("게시글 삭제 - 타인의 게시물")
  public void failDeletePost() {
    //given
    postService = new PostService(postRepository);
    Optional<Post> mockOptional = mock(Optional.class);
    User mockUser = mock(User.class);
    when(postRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockPost);
    when(mockPost.matchAuthor(any())).thenThrow(new MissmatchAuthor("로그인 실패"));

    //when
    postService.delete(0L, mockHttpSession);

    //then
    verify(postRepository).deleteById(any());
  }

}