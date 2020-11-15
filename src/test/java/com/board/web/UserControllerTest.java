package com.board.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.board.config.SessionUser;
import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.user.UserLoginRequestDto;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserUpdateDto;
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

  //내장된 서블릿 컨테이너를 구동하고 random port로 리스닝 한다.
  @LocalServerPort
  private int port;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  //given
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

    String url = "http://localhost:" + port + "/user/create";

    //when
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .param("name", name)
        .param("userId", id)
        .param("email", email)
        .param("password", password)).andExpect(status().is3xxRedirection());

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
    userRepository.save(userRequestDto.toEntity());
    int size = userRepository.findAll().size();

    String url = "http://localhost:" + port + "/user/list";
    this.mvc.perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(model().attribute("users", IsCollectionWithSize.hasSize(size)));
  }

  @Test
  public void 사용자정보수정() throws Exception {

    //given
    String updateName = "제이그래머";
    String updatePassword = "wpdlrmfoaj";

    SessionUser sessionUser = SessionUser.builder()
        .name(name)
        .userId(id)
        .password(password)
        .email(email)
        .build();

    String saveId = userRepository.save(userRequestDto.toEntity()).getUserId();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, sessionUser);

    UserUpdateDto updateDto = UserUpdateDto.builder()
        .name(updateName)
        .password(updatePassword)
        .email(email)
        .build();

    String url = "http://localhost:" + port + "/user/" + saveId + "/update";

    //when
    this.mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateDto))
        .session(session))
        .andExpect(status().is3xxRedirection());

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

    String saveId = userRepository.save(userRequestDto.toEntity()).getUserId();

    UserRequestDto newUser = UserRequestDto.builder()
        .userId(id + "_2")
        .name(name)
        .password(password)
        .email(email)
        .build();

    SessionUser sessionUser = SessionUser.builder()
        .userId(id + "_2")
        .name(name)
        .password(password)
        .email(email)
        .build();

    userRepository.save(newUser.toEntity()).getId();

    session.setAttribute("sessionedUser", sessionUser);

    UserUpdateDto updateDto = UserUpdateDto.builder()
        .name(updateName)
        .password(updatePassword)
        .email(email)
        .build();

    String url = "http://localhost:" + port + "/user/" + saveId + "/update";

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
    userRepository.save(userRequestDto.toEntity()).getId();

    String url = "http://localhost:" + port + "/user/login";

    //when
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(new UserLoginRequestDto(id, password))))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void 로그인실패_비밀번호() throws Exception {
    session.setAttribute("sessionedUser", userRequestDto.toEntity());

    String url = "http://localhost:" + port + "/user/login";

    //when
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(new UserLoginRequestDto(id, " "))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void 로그인실패_아이디() throws Exception {
    session.setAttribute("sessionedUser", userRequestDto.toEntity());

    String url = "http://localhost:" + port + "/user/login";

    //when
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(new UserLoginRequestDto(" ", password))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void 로그인후_로그인폼접근() throws Exception {
    session.setAttribute("sessionedUser", userRequestDto.toEntity());

    String url = "http://localhost:" + port + "/user/login";

    //when
    this.mvc.perform(MockMvcRequestBuilders.get(url)
        .session(session))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void 로그인후_로그인시도() throws Exception {
    session.setAttribute("sessionedUser", userRequestDto.toEntity());

    String url = "http://localhost:" + port + "/user/login";

    //when
    this.mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(new UserLoginRequestDto(id, password)))
        .session(session))
        .andExpect(status().is3xxRedirection());
  }

}