package com.board.service;

import com.board.config.SessionUser;
import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.domain.user.exception.UserMismatchException;
import com.board.domain.user.exception.UserNotFoundException;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
import com.board.web.dto.user.UserUpdateDto;
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
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public Long save(UserRequestDto dto) {
    User user = userRepository.findByUserId(dto.getUserId());
    if (user == null) {
      Long id = userRepository.save(dto.toEntity()).getId();
      return id;
    } else {
      throw new IllegalStateException("이미 등록된 유저입니다.");
    }
  }

  @Transactional
  public Long update(String userId, UserUpdateDto dto, HttpSession session) {
    User updateUser = userRepository.findByUserId(userId);
    if (updateUser == null) {
      throw new UserNotFoundException("해당 유저가 없습니다. id=" + userId);
    }
    SessionUser user = HttpSessionUtils.getUserFromSession(session);
    if (!user.matchUserId(userId)) {
      throw new UserMismatchException("수정 권한이 없습니다.");
    }
    updateUser.update(dto.getPassword(), dto.getName(), dto.getEmail()).getId();
    HttpSessionUtils.updateUser(updateUser, session);
    return updateUser.getId();
  }

  @Transactional(readOnly = true)
  public List<UserResponseDto> findAll() {
    return userRepository.findAll().stream()
        .map(UserResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public UserResponseDto findById(Long id) {
    User entity = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("해당 유저가 없습니다. id=" + id));
    return new UserResponseDto(entity);
  }

  @Transactional(readOnly = true)
  public UserResponseDto findByUserId(String userId) {
    User entity = userRepository.findByUserId(userId);
    return new UserResponseDto(entity);
  }

  @Transactional(readOnly = true)
  public boolean login(String userId, String password, HttpSession session) {
    if (HttpSessionUtils.isLoginUser(session)) {
      return true;
    }
    User user = userRepository.findByUserId(userId);
    if (user == null) {
      log.info("id : " + userId + "는 존재하지 않는 유저입니다.");
      return false;
    } else if (!user.matchPassword(password)) {
      log.info("id : " + userId + "님이 다른 비밀번호로 접근했습니다.");
      return false;
    } else {
      session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user.makeSessionUser());
      log.info("id : " + userId + "님이 로그인 하셨습니다.");
      return true;
    }
  }
}
