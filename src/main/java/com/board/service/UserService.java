package com.board.service;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
  public Long update(Long id, UserRequestDto dto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + id));
    user.update(dto.getUserId(), dto.getPassword(), dto.getName(), dto.getEmail());
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


}
