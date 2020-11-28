package com.board.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.board.domain.answer.Answer;
import com.board.domain.answer.AnswerRepository;
import com.board.domain.question.QuestionRepository;
import com.board.domain.question.exception.MismatchAuthor;
import com.board.domain.user.User;
import com.board.web.dto.user.UserResponseDto;
import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AnswerServiceTest {

  @Mock
  private AnswerRepository answerRepository;

  @Mock
  private QuestionRepository questionRepository;

  @Test
  @DisplayName("답변 수정")
  public void 답변_수정() {
    //given
    AnswerService answerService = new AnswerService(answerRepository, questionRepository);
    String updateContent = "수정된 답변";
    Answer answer = mock(Answer.class);
    Optional<Answer> mockOptional = mock(Optional.class);
    User user = mock(User.class);
    UserResponseDto userDto = mock(UserResponseDto.class);
    when(answerRepository.findById(1L)).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(answer);
    when(answer.getUser()).thenReturn(user);
    when(userDto.getUserId()).thenReturn("user id");
    when(user.matchUserId(any())).thenReturn(true);
    //when
    answerService.update(1L, updateContent, userDto);
    //then
    verify(answer).update(updateContent);
  }

  @Test(expected = MismatchAuthor.class)
  @DisplayName("타인 답변 수정")
  public void 타인답변_수정() {
    //given
    AnswerService answerService = new AnswerService(answerRepository, questionRepository);
    String updateContent = "수정된 답변";
    Answer answer = mock(Answer.class);
    Optional<Answer> mockOptional = mock(Optional.class);
    User user = mock(User.class);
    UserResponseDto userDto = mock(UserResponseDto.class);
    when(answerRepository.findById(1L)).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(answer);
    when(answer.getUser()).thenReturn(user);
    when(userDto.getUserId()).thenReturn("user id");
    when(user.matchUserId(any())).thenReturn(false);
    //when
    answerService.update(1L, updateContent, userDto);
    //then
    verify(answer, never()).update(updateContent);
  }

  @Test
  @DisplayName("답변 삭제")
  public void 답변_삭제() {
    //given
    AnswerService answerService = new AnswerService(answerRepository, questionRepository);
    Answer answer = mock(Answer.class);
    Optional<Answer> mockOptional = mock(Optional.class);
    User user = mock(User.class);
    UserResponseDto userDto = mock(UserResponseDto.class);
    when(answerRepository.findById(1L)).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(answer);
    when(answer.getUser()).thenReturn(user);
    when(userDto.getUserId()).thenReturn("user id");
    when(user.matchUserId(any())).thenReturn(true);
    //when
    answerService.delete(1L, userDto);
    //then
    verify(answerRepository).deleteById(1L);
  }

  @Test(expected = MismatchAuthor.class)
  @DisplayName("타인 답변 삭제")
  public void 타인답변_삭제() {
    //given
    AnswerService answerService = new AnswerService(answerRepository, questionRepository);
    Answer answer = mock(Answer.class);
    Optional<Answer> mockOptional = mock(Optional.class);
    User user = mock(User.class);
    UserResponseDto userDto = mock(UserResponseDto.class);
    when(answerRepository.findById(1L)).thenReturn(mockOptional);
    when(mockOptional.orElseThrow(any())).thenReturn(answer);
    when(answer.getUser()).thenReturn(user);
    when(userDto.getUserId()).thenReturn("user id");
    when(user.matchUserId(any())).thenReturn(false);
    //when
    answerService.delete(1L, userDto);
    //then
    verify(answerRepository, never()).deleteById(1L);
  }


}