package com.board.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.web.dto.UserRequestDto;
import java.util.List;
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

    UserRequestDto userRequestDto = UserRequestDto.builder()
        .userId(id)
        .name(name)
        .password(password)
        .email(email)
        .build();

    userRepository.save(userRequestDto.toEntity());

    //when
    List<User> userAll = userRepository.findAll();
    User user = userAll.get(0);

    //then
    assertThat(user.getName()).isEqualTo(name);
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUserId()).isEqualTo(id);
    assertThat(user.getPassword()).isEqualTo(password);
  }

}