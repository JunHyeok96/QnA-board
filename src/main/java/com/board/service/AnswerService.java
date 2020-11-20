package com.board.service;

import com.board.domain.answer.Answer;
import com.board.domain.answer.AnswerRepository;
import com.board.domain.question.Question;
import com.board.domain.question.QuestionRepository;
import com.board.domain.question.exception.MissmatchAuthor;
import com.board.domain.question.exception.PostNotFoundException;
import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.Answer.AnswerRequsetDto;
import com.board.web.dto.Answer.AnswerResponseDto;
import com.board.web.dto.question.QuestionUpdateDto;
import com.board.web.dto.user.UserResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AnswerService {

  private final AnswerRepository answerRepository;
  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;

  @Transactional
  public long save(AnswerRequsetDto answerRequsetDto, HttpSession session) {
    UserResponseDto sessionUser = HttpSessionUtils.getUserFromSession(session);
    User user = sessionUser != null ? sessionUser.toEntity() : null;
    Question question = questionRepository.findById(answerRequsetDto.getQuestionId()).get();
    Answer answer = answerRequsetDto
        .toEntity(question, user);
    Long id = answerRepository.save(answer).getId();
    question.update(new QuestionUpdateDto(question.getTitle(), question.getContent(), answer));
    return id;
  }


  @Transactional(readOnly = true)
  public Page<Answer> findMyAnswers(HttpSession session, Pageable pageable) {
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    return answerRepository.findByUserId(user.getId(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<Answer> findByQuestion(Long questionId, Pageable pageable) {
    return answerRepository.findByQuestionId(questionId, pageable);
  }

  @Transactional(readOnly = true)
  public List<AnswerResponseDto> findByQuestion(Long questionId) {
    return answerRepository.findByQuestionId(questionId)
        .stream()
        .map(AnswerResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public void update(long id, String content, HttpSession session) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 답변"));
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    if (answer.getQuestion().matchAuthor(user.getUserId())) {
      answer.update(content);
    } else {
      throw new MissmatchAuthor("본인의 답변만 수정할 수 있습니다.");
    }
  }

  @Transactional
  public void delete(long id, HttpSession session) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 답변"));
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    if (answer.getQuestion().matchAuthor(user.getUserId())) {
      answerRepository.deleteById(id);
    } else {
      throw new MissmatchAuthor("본인의 답변만 삭제할 수 있습니다.");
    }
  }

  @Transactional
  public void deleteByQuestion(Question question) {
    answerRepository.deleteByQuestion(question);
  }
}
