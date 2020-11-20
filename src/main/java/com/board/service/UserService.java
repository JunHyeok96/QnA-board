package com.board.service;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.domain.user.exception.AlreadyExistUser;
import com.board.domain.user.exception.LoginException;
import com.board.domain.user.exception.UserMismatchException;
import com.board.domain.user.exception.UserNotFoundException;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserLoginRequestDto;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
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
  public Long update(UserRequestDto updateUser, HttpSession session) {
    UserResponseDto sessionUser = HttpSessionUtils.getUserFromSession(session);
    User user = userRepository.findByUserId(updateUser.getUserId());
    if (user == null) {
      throw new UserNotFoundException("해당 유저가 없습니다. id=" + updateUser.getUserId());
    }
    if (!user.matchUserId(sessionUser.getUserId())) {
      throw new UserMismatchException("수정 권한이 없습니다.");
    }
    long id = user.update(updateUser).getId();
    HttpSessionUtils.updateUser(user, session);
    return id;
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
      //TODO 쿠키를 이용한 로그인유지
      session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user.makeSessionValue());
      log.info("id : " + requestDto.getUserId() + "님이 로그인 하셨습니다.");
      return true;
    }
  }
}
