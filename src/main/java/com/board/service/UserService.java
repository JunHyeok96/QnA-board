package com.board.service;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
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
    Long id = userRepository.save(dto.toEntity()).getId();
    return id;
  }

  @Transactional
  public Long update(Long id, UserUpdateDto dto, HttpSession session) {
    if (!HttpSessionUtils.isLoginUser(session)) {
      throw new IllegalStateException("로그인이 필요합니다.");
    }
    User updateUser = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + id));
    User user = HttpSessionUtils.getUserFromSession(session);
    if (!user.getId().equals(id)) {
      throw new IllegalStateException("수정 권한이 없습니다.");
    }

    updateUser.update(dto.getPassword(), dto.getName(), dto.getEmail());
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
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + id));
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
      session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
      log.info("id : " + userId + "님이 로그인 하셨습니다.");
      return true;
    }
  }
}
