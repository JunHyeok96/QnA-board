package com.board.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.board.domain.question.Question;
import com.board.domain.question.QuestionRepository;
import com.board.domain.question.exception.MismatchAuthor;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.question.QuestionUpdateDto;
import com.board.web.dto.user.UserResponseDto;
import java.util.Optional;
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
public class QuestionServiceTest {

  private QuestionService questionService;

  private static MockedStatic<HttpSessionUtils> mockedSessionUtils;
  private MockHttpSession mockHttpSession = new MockHttpSession();

  @Mock
  private QuestionRepository questionRepository;

  @Mock
  private AnswerService answerService;

  @Mock
  private Question mockQuestion;

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
    questionService = new QuestionService(questionRepository, answerService);
    Optional<Question> mockOptional = mock(Optional.class);
    UserResponseDto mockUser = mock(UserResponseDto.class);
    when(HttpSessionUtils.getUserFromSession(any())).thenReturn(mockUser);
    when(mockUser.getUserId()).thenReturn("id");
    QuestionUpdateDto mockQuestionUpdateDto = mock(QuestionUpdateDto.class);
    when(questionRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockQuestion);
    when(mockQuestion.matchAuthor(any())).thenReturn(true);
    //when
    questionService.update(0L, mockQuestionUpdateDto, mockHttpSession);
    //then
    verify(mockQuestion).update(mockQuestionUpdateDto);
  }

  @Test(expected = MismatchAuthor.class)
  @DisplayName("질문 수정 - 타인의 질문")
  public void failUpdatePost() {
    //given
    questionService = new QuestionService(questionRepository, answerService);
    Optional<Question> mockOptional = mock(Optional.class);
    UserResponseDto mockUser = mock(UserResponseDto.class);
    when(HttpSessionUtils.getUserFromSession(any())).thenReturn(mockUser);
    when(mockUser.getUserId()).thenReturn("id");
    QuestionUpdateDto mockQuestionUpdateDto = mock(QuestionUpdateDto.class);
    when(questionRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockQuestion);
    when(mockQuestion.matchAuthor(any())).thenReturn(false);
    //when
    questionService.update(0L, mockQuestionUpdateDto, mockHttpSession);
    //then
    verify(mockQuestion, never()).update(any());
  }

  @Test
  @DisplayName("질문 삭제")
  public void deletePost() {
    //given
    questionService = new QuestionService(questionRepository, answerService);
    Optional<Question> mockOptional = mock(Optional.class);
    UserResponseDto mockUser = mock(UserResponseDto.class);
    when(HttpSessionUtils.getUserFromSession(any())).thenReturn(mockUser);
    when(mockUser.getUserId()).thenReturn("id");
    when(HttpSessionUtils.isLoginUser(any())).thenReturn(true);
    when(questionRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockQuestion);
    when(mockQuestion.matchAuthor(any())).thenReturn(true);
    //when
    questionService.delete(0L, mockHttpSession);
    //then
    verify(questionRepository).delete(any());
    verify(answerService).deleteByQuestion(any());
  }

  @Test(expected = MismatchAuthor.class)
  @DisplayName("질문 삭제 - 타인의 질문")
  public void failDeletePost() {
    //given
    questionService = new QuestionService(questionRepository, answerService);
    Optional<Question> mockOptional = mock(Optional.class);
    UserResponseDto mockUser = mock(UserResponseDto.class);
    when(HttpSessionUtils.getUserFromSession(any())).thenReturn(mockUser);
    when(mockUser.getUserId()).thenReturn("id");
    when(HttpSessionUtils.isLoginUser(any())).thenReturn(true);
    when(questionRepository.findById(any())).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(mockQuestion);
    when(mockQuestion.matchAuthor(any())).thenReturn(false);
    //when
    questionService.delete(0L, mockHttpSession);
    //then
    verify(questionRepository, never()).deleteById(any());
    verify(answerService, never()).deleteByQuestion(any());
  }

}