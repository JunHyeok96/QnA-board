package com.board.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.user.UserLoginRequestDto;
import com.board.web.dto.user.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  private final String name = "이준혁";
  private final String id = "j005580";
  private final String email = "j005580@naver.com";
  private final String password = "abcde";

  private final UserRequestDto userRequestDto = UserRequestDto.builder()
      .userId(id)
      .name(name)
      .password(password)
      .email(email)
      .build();
  ;

  MockHttpSession session = new MockHttpSession();

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
    //given
    String url = "http://localhost:" + port + "/user/create";
    //when
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(userRequestDto)))
        .andExpect(status().is3xxRedirection());

    List<User> userAll = userRepository.findAll();
    User user = userAll.get(userAll.size() - 1);
    //then
    assertThat(user.getName()).isEqualTo(name);
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUserId()).isEqualTo(id);
    assertThat(user.getPassword()).isEqualTo(password);
  }

  @Test
  public void 사용자정보수정() throws Exception {
    //given
    String updateName = "제이그래머";
    String updatePassword = "wpdlrmfoaj";
    User sessionUser = User.builder()
        .name(name)
        .userId(id)
        .password(password)
        .email(email)
        .build();
    String saveId = userRepository.save(sessionUser).getUserId();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, sessionUser.makeSessionValue());
    UserRequestDto updateDto = UserRequestDto.builder()
        .name(updateName)
        .password(updatePassword)
        .email(email)
        .userId(id)
        .build();
    String url = "http://localhost:" + port + "/api/v1/user/" + saveId;
    System.out.println(HttpSessionUtils.getUserFromSession(session));
    //when
    this.mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateDto))
        .session(session))
        .andExpect(status().isOk());
    //then
    User user = userRepository.findByUserId(saveId);
    assertThat(user.getName()).isEqualTo(updateName);
    assertThat(user.getPassword()).isEqualTo(updatePassword);
  }

  @Test
  public void 타인의정보수정_실패() throws Exception {
    //given
    String updateName = "제이그래머";
    String updatePassword = "wpdlrmfoaj";
    User sessionUser = User.builder()
        .userId(id + "_2")
        .name(name)
        .password(password)
        .email(email)
        .build();
    String saveId = userRepository.save(sessionUser).getUserId();
    session.setAttribute("sessionedUser", sessionUser.makeSessionValue());
    UserRequestDto updateDto = UserRequestDto.builder()
        .name(updateName)
        .password(updatePassword)
        .email(email)
        .userId(id)
        .build();
    userRepository.save(updateDto.toEntity()).getUserId();
    String url = "http://localhost:" + port + "/api/v1/user/" + saveId;
    //when
    this.mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateDto))
        .session(session))
        .andExpect(status().isForbidden());
    //then
    User user = userRepository.findByUserId(saveId);
    assertThat(user.getName()).isEqualTo(name);
    assertThat(user.getPassword()).isEqualTo(password);
  }

  @Test
  public void 로그인성공() throws Exception {
    //given
    userRepository.save(userRequestDto.toEntity()).getId();
    String url = "http://localhost:" + port + "/user/login";
    //when, then
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            new ObjectMapper().writeValueAsString(new UserLoginRequestDto(id, password, false))))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void 로그인실패_비밀번호() throws Exception {
    //given
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userRequestDto.toEntity());
    String url = "http://localhost:" + port + "/user/login";
    //when, then
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(new UserLoginRequestDto(id, " ", false))))
        .andExpect(status().isForbidden());
  }

  @Test
  public void 로그인실패_아이디() throws Exception {
    //given
    session.setAttribute("sessionedUser", userRequestDto.toEntity());
    String url = "http://localhost:" + port + "/user/login";
    //when, then
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            new ObjectMapper().writeValueAsString(new UserLoginRequestDto(" ", password, false))))
        .andExpect(status().isForbidden());
  }

  @Test
  public void 로그인후_로그인폼접근() throws Exception {
    //given
    session.setAttribute("sessionedUser", userRequestDto.toEntity());
    String url = "http://localhost:" + port + "/user/login";
    //when, then
    this.mvc.perform(MockMvcRequestBuilders.get(url)
        .session(session))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void 로그인후_로그인시도() throws Exception {
    //given
    session.setAttribute("sessionedUser", userRequestDto.toEntity());
    String url = "http://localhost:" + port + "/user/login";
    //when, then
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            new ObjectMapper().writeValueAsString(new UserLoginRequestDto(id, password, false)))
        .session(session))
        .andExpect(status().is3xxRedirection());
  }

}