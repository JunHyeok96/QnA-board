package com.board.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.web.dto.user.UserRequestDto;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void 유저정보저장_불러오기() {
    String name = "이준혁";
    String id = "j005580";
    String email = "j005580@naver.com";
    String password = "abcde";
    LocalDateTime now = LocalDateTime.now();

    UserRequestDto userRequestDto = UserRequestDto.builder()
        .userId(id)
        .name(name)
        .password(password)
        .email(email)
        .build();

    Long save_id = userRepository.save(userRequestDto.toEntity()).getId();

    //when
    User user = userRepository.findById(save_id).get();

    //then
    assertThat(user.getName()).isEqualTo(name);
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUserId()).isEqualTo(id);
    assertThat(user.getPassword()).isEqualTo(password);
    assertThat(user.getCreateDate()).isAfter(now);
    assertThat(user.getModifiedDate()).isAfter(now);
  }

}