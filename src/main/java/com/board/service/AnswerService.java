package com.board.service;

import com.board.domain.answer.Answer;
import com.board.domain.answer.AnswerRepository;
import com.board.domain.question.Question;
import com.board.domain.question.QuestionRepository;
import com.board.domain.question.exception.MismatchAuthor;
import com.board.domain.question.exception.PostNotFoundException;
import com.board.domain.user.User;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.Answer.AnswerRequsetDto;
import com.board.web.dto.Answer.AnswerResponseDto;
import com.board.web.dto.user.UserResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnswerService {

  private final AnswerRepository answerRepository;
  private final QuestionRepository questionRepository;

  @Transactional
  public long save(AnswerRequsetDto answerRequsetDto, UserResponseDto sessionUser) {
    User user = sessionUser != null ? sessionUser.toEntity() : null;
    Question question = questionRepository.findById(answerRequsetDto.getQuestionId()).get();
    Answer answer = answerRequsetDto
        .toEntity(question, user);
    Long id = answerRepository.save(answer).getId();
    return id;
  }


  @Transactional(readOnly = true)
  public Page<Answer> findMyAnswers(UserResponseDto user, Pageable pageable) {
    return answerRepository.findByUserId(user.getId(), pageable);
  }

  @Transactional(readOnly = true)
  public AnswerResponseDto findById(long id) {
    return new AnswerResponseDto(answerRepository.findById(id).get());
  }

  @Transactional(readOnly = true)
  public List<AnswerResponseDto> findByQuestion(Long questionId) {
    return answerRepository.findByQuestionId(questionId)
        .stream()
        .map(AnswerResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public void update(long id, String content, UserResponseDto user) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 답변"));
    if (answer.getUser().matchUserId(user.getUserId())) {
      answer.update(content);
    } else {
      throw new MismatchAuthor("본인의 답변만 수정할 수 있습니다.");
    }
  }

  @Transactional
  public void delete(long id, UserResponseDto user) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 답변"));
    if (answer.getUser().matchUserId(user.getUserId())) {
      answerRepository.deleteById(id);
    } else {
      throw new MismatchAuthor("본인의 답변만 삭제할 수 있습니다.");
    }
  }

  @Transactional
  public void updateByAdmin(long id, String content) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 답변"));
    answer.update(content);
  }

  @Transactional
  public void deleteByAdmin(long id) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 답변"));
    answerRepository.deleteById(id);
  }

  @Transactional
  public void deleteByQuestion(Question question) {
    answerRepository.deleteByQuestion(question);
  }
}
