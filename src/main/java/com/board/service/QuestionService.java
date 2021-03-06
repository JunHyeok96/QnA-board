package com.board.service;

import com.board.domain.question.Question;
import com.board.domain.question.QuestionRepository;
import com.board.domain.question.exception.MismatchAuthor;
import com.board.domain.question.exception.PostNotFoundException;
import com.board.domain.user.User;
import com.board.domain.user.exception.LoginException;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.question.QuestionRequestDto;
import com.board.web.dto.question.QuestionResponseDto;
import com.board.web.dto.question.QuestionUpdateDto;
import com.board.web.dto.user.UserResponseDto;
import org.springframework.data.domain.Page;
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
public class QuestionService {

  private final QuestionRepository questionRepository;
  private final AnswerService answerService;

  @Transactional(readOnly = true)
  public List<QuestionResponseDto> findQuestionAll() {
    return questionRepository.findAll()
        .stream()
        .map(QuestionResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Page<Question> findQuestionAll(Pageable pageable) {
    return questionRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public QuestionResponseDto findById(long id) {
    return questionRepository.findById(id).map(QuestionResponseDto::new).get();
  }

  @Transactional(readOnly = true)
  public Page<Question> findMyPosts(UserResponseDto user, Pageable pageable) {
    return questionRepository.findByUserId(user.getId(), pageable);
  }

  @Transactional(readOnly = true)
  public Page<Question> findByUserId(long userId, Pageable pageable) {
    return questionRepository.findByUserId(userId, pageable);
  }

  @Transactional(readOnly = true)
  public Page<Question> findByUserId(String userId, Pageable pageable) {
    return questionRepository.findByUserId(userId, pageable);
  }

  @Transactional
  public Long save(QuestionRequestDto questionRequestDto, UserResponseDto sessionUser) {
    User user = sessionUser != null ? sessionUser.toEntity() : null;
    Question question = questionRequestDto.toEntity(user);
    Long id = questionRepository.save(question).getId();
    return id;
  }

  @Transactional
  public Long update(Long id, QuestionUpdateDto questionUpdateDto, UserResponseDto user) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시물"));
    if (question.matchAuthor(user.getUserId())) {
      question.update(questionUpdateDto);
    } else {
      throw new MismatchAuthor("본인의 게시물만 수정할 수 있습니다.");
    }
    return id;
  }

  @Transactional
  public void delete(Long id, UserResponseDto user) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시물"));
    if (question.matchAuthor(user.getUserId())) {
      questionRepository.delete(question);
      answerService.deleteByQuestion(question);
    } else {
      throw new MismatchAuthor("본인의 게시물만 삭제할 수 있습니다.");
    }
  }

  @Transactional(readOnly = true)
  public Page<Question> search(String keyword, Pageable pageable) {
    return questionRepository.search(keyword, pageable);
  }

  @Transactional
  public Long updateByAdmin(Long id, QuestionUpdateDto questionUpdateDto) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시물"));
    question.update(questionUpdateDto);
    return id;
  }

  @Transactional
  public void deleteByAdmin(Long id) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시물"));
    questionRepository.delete(question);
    answerService.deleteByQuestion(question);
  }

}
