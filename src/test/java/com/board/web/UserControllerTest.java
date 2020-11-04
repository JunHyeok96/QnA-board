package com.board.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserControllerTest {

  //내장된 서블릿 컨테이너를 구동하고 random port로 리스닝 한다.
  @LocalServerPort
  private int port;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Before
  public void setup() {
    this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @After
  public void tearDown() throws Exception {
    userRepository.deleteAll();
  }

  @Test
  public void 회원가입_DB저장() throws Exception {
    String name = "이준혁";
    String id = "j005580";
    String email = "j005580@naver.com";
    String password = "abcde";

    String url = "http://localhost:" + port + "/user/create";

    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .param("name", name)
        .param("userId", id)
        .param("email", email)
        .param("password", password)).andExpect(status().is3xxRedirection());

    //when
    List<User> userAll = userRepository.findAll();
    User user = userAll.get(0);

    //then
    assertThat(user.getName()).isEqualTo(name);
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUserId()).isEqualTo(id);
    assertThat(user.getPassword()).isEqualTo(password);
  }

  @Test
  public void 사용자목록조회() throws Exception {
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

    String url = "http://localhost:" + port + "/user/list";
    this.mvc.perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(model().attribute("users", IsCollectionWithSize.hasSize(1)));
  }

  @Test
  public void 사용자정보수정() throws Exception {

    String email = "j005580@naver.com";
    String id = "j005580";
    String updateName = "제이그래머";
    String updatePassword = "wpdlrmfoaj";

    UserRequestDto userRequestDto = UserRequestDto.builder()
        .userId(id)
        .name("이준혁")
        .password("asdf1")
        .email(email)
        .build();

    Long saveId = userRepository.save(userRequestDto.toEntity()).getId();

    UserRequestDto updateDto = UserRequestDto.builder()
        .userId(id)
        .name(updateName)
        .password(updatePassword)
        .email(email)
        .build();

    String url = "http://localhost:" + port + "/user/" + saveId + "/update";

    this.mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateDto)))
        .andExpect(status().isOk());

    //then
    User user = userRepository.findById(saveId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 id정보"));

    assertThat(user.getName()).isEqualTo(updateName);
    assertThat(user.getPassword()).isEqualTo(updatePassword);
  }
}