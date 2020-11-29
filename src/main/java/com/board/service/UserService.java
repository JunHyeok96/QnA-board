package com.board.service;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.domain.user.exception.AlreadyExistUser;
import com.board.domain.user.exception.LoginException;
import com.board.domain.user.exception.UserMismatchException;
import com.board.domain.user.exception.UserNotFoundException;
import com.board.web.HttpCookieUtils;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserLoginRequestDto;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public Long save(UserRequestDto dto) {
    if (!dto.isVaild()) {
      throw new IllegalStateException("유효하지 않은 값 입니다.");
    }
    User user = userRepository.findByUserId(dto.getUserId());
    if (user == null) {
      Long id = userRepository.save(dto.toEntity()).getId();
      return id;
    } else {
      throw new AlreadyExistUser("이미 등록된 유저입니다.");
    }
  }

  @Transactional
  public long update(UserRequestDto updateUser, HttpSession session) {
    UserResponseDto sessionUser = HttpSessionUtils.getUserFromSession(session);
    User user = userRepository.findByUserId(updateUser.getUserId());
    if (user == null) {
      throw new UserNotFoundException("해당 유저가 없습니다. id=" + updateUser.getUserId());
    }
    if (!user.matchUserId(sessionUser.getUserId())) {
      throw new UserMismatchException("수정 권한이 없습니다.");
    }
    long id = user.update(updateUser).getId();
    HttpSessionUtils.updateUser(user.makeSessionValue(), session);
    return id;
  }

  @Transactional(readOnly = true)
  public UserResponseDto findByUserId(String userId) {
    User entity = userRepository.findByUserId(userId);
    return new UserResponseDto(entity);
  }

  @Transactional
  public boolean login(UserLoginRequestDto requestDto, HttpSession session) {
    if (!requestDto.isVaild()) {
      throw new LoginException("유효하지 않은 값 입니다.");
    }
    if (HttpSessionUtils.isLoginUser(session)) {
      return true;
    }
    User user = userRepository.findByUserId(requestDto.getUserId());
    if (user == null) {
      log.info("id : " + requestDto.getUserId() + "는 존재하지 않는 유저입니다.");
      throw new LoginException("로그인 정보를 확인해주세요");
    } else if (!user.matchPassword(requestDto.getPassword())) {
      log.info("id : " + requestDto.getUserId() + "님이 다른 비밀번호로 접근했습니다.");
      throw new LoginException("로그인 정보를 확인해주세요");
    } else {
      session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user.makeSessionValue());
      user.updateSessionId(session.getId());
      log.info("id : " + requestDto.getUserId() + "님이 로그인 하셨습니다.");
      return true;
    }
  }

  @Transactional(readOnly = true)
  public UserResponseDto findBySessionId(String sessionId) {
    return userRepository.findBySessionId(sessionId).makeSessionValue();
  }

  @Transactional
  public void deleteSessionId(String sessionId) {
    try {
      User user = userRepository.findBySessionId(sessionId);
      user.updateSessionId(null);
    } catch (NullPointerException e) {
      log.error("세션 정보가 존재하지 않습니다. " + sessionId);
      return;
    }
  }

  @Transactional
  public long updateByAdmin(UserRequestDto updateUser, HttpSession session) {
    User user = userRepository.findByUserId(updateUser.getUserId());
    long id = user.update(updateUser).getId();
    HttpSessionUtils.updateUser(user.makeSessionValue(), session);
    return id;
  }

}
