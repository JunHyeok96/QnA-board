package com.board.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.domain.question.Question;
import com.board.domain.question.QuestionRepository;
import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.question.QuestionRequestDto;
import com.board.web.dto.question.QuestionResponseDto;
import com.board.web.dto.question.QuestionUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class QuestionControllerTest {

  @LocalServerPort
  private int port;

  private MockMvc mvc;

  private ObjectMapper mapper;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockHttpSession session = new MockHttpSession();

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private UserRepository userRepository;

  private final String API = "http://localhost:" + port + "/api/v1/questions/";
  private final String ADMIN_API = "http://localhost:" + port + "/api/v1/admin/questions/";
  private final String title = "title";
  private final String content = "content";
  private final String userId = "test-user";

  private final QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
      .title(title)
      .content(content)
      .build();

  private User user = User.builder()
      .userId(userId)
      .name("a")
      .password("1234")
      .email("aa@22.com")
      .build();

  @Before
  public void setup() {
    this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
  }

  @After
  public void tearDown() throws Exception {
    questionRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void 단일게시물조회() throws Exception {
    //given
    userRepository.save(user);
    Long id = questionRepository.save(questionRequestDto.toEntity(user)).getId();
    String url = API + id.toString();
    //when
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andReturn();
    String returnContent = result.getResponse().getContentAsString();
    QuestionResponseDto responseDto = mapper.readValue(returnContent, QuestionResponseDto.class);
    //then
    Question question = questionRepository.findById(id).get();
    assertThat(question.getId()).isEqualTo(responseDto.getId());
    assertThat(question.getContent()).isEqualTo(responseDto.getContent());
    assertThat(question.getTitle()).isEqualTo(responseDto.getTitle());
    assertThat(question.getUser().getUserId()).isEqualTo(responseDto.getUserId());
  }

  @Test
  public void 게시물리스트조회() throws Exception {
    //given
    userRepository.save(user);
    questionRepository.save(questionRequestDto.toEntity(user));
    questionRepository.save(questionRequestDto.toEntity(user));
    questionRepository.save(questionRequestDto.toEntity(user));
    String url = API + "list";
    //when, then
    mvc.perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  public void 게시글수정() throws Exception {
    //given
    userRepository.save(user);
    String updateContent = "updateContent";
    String updateTitle = "updateTitle";
    QuestionUpdateDto questionUpdateDto = QuestionUpdateDto.builder()
        .title(updateTitle)
        .content(updateContent)
        .build();
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user.makeSessionValue());
    String url = API + question.getId();
    //when
    mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(questionUpdateDto))
        .session(session))
        .andExpect(status().isOk());
    //then
    Question newQuestion = questionRepository.findById(question.getId()).get();
    assertThat(updateContent).isEqualTo(newQuestion.getContent());
    assertThat(updateTitle).isEqualTo(newQuestion.getTitle());
    assertThat(userId).isEqualTo(newQuestion.getUser().getUserId());
  }

  @Test
  public void 게시물저장() throws Exception {
    //given
    String url = API;
    User user = User.builder()
        .userId(userId)
        .name("a")
        .password("1234")
        .email("aa@22.com")
        .build();
    userRepository.save(user);
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user.makeSessionValue());
    //when
    mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(questionRequestDto))
        .session(session))
        .andExpect(status().isOk());
    //then
    List<Question> questions = questionRepository.findAll();
    Question question = questions.get(0);
    assertThat(title).isEqualTo(question.getTitle());
    assertThat(content).isEqualTo(question.getContent());
  }

  @Test
  public void 자신의게시글삭제() throws Exception {
    //given
    userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user.makeSessionValue());
    String url = API + question.getId().toString();
    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session))
        .andExpect(status().isOk());
    //then
    assertThat(questionRepository.findById(question.getId())).isEqualTo(Optional.empty());

  }

  @Test
  public void 타인의게시글삭제() throws Exception {
    //given
    userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    User newUser = User.builder()
        .userId(userId + "_1")
        .name("a")
        .password("1234")
        .email("aa@22.com")
        .build();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, newUser.makeSessionValue());
    String url = API + question.getId().toString();
    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session))
        .andExpect(status().isForbidden());
    //then
    assertThat(questionRepository.findById(question.getId())).isNotEmpty();
  }

  @Test
  public void 타인의게시글수정() throws Exception {
    //given
    userRepository.save(user);
    User newUser = User.builder()
        .userId(userId + "_1")
        .name("a")
        .password("1234")
        .email("aa@22.com")
        .build();
    QuestionUpdateDto questionUpdateDto = QuestionUpdateDto.builder()
        .title("abcd")
        .content("12345")
        .build();
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, newUser.makeSessionValue());
    String url = API + question.getId().toString();
    //when
    mvc.perform(MockMvcRequestBuilders.put(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(questionUpdateDto))
        .session(session))
        .andExpect(status().isForbidden());
    //then
    assertThat(questionRepository.findById(question.getId()).get().getTitle())
        .isEqualTo(questionRequestDto.getTitle());
    assertThat(questionRepository.findById(question.getId()).get().getContent())
        .isEqualTo(questionRequestDto.getContent());
  }

  @Test
  @DisplayName("관리자 권한 수정")
  public void 관리자권한_수정() throws Exception {
    //given
    userRepository.save(user);
    String updateContent = "updateContent";
    String updateTitle = "updateTitle";
    String userId = "admin";
    User admin = User.builder()
        .userId(userId)
        .name("a")
        .password("1234")
        .email("aa@22.com")
        .build();
    QuestionUpdateDto questionUpdateDto = QuestionUpdateDto.builder()
        .title(updateTitle)
        .content(updateContent)
        .build();
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, admin.makeSessionValue());
    String url = ADMIN_API + question.getId();
    //when
    mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(questionUpdateDto))
        .session(session))
        .andExpect(status().isOk());
    //then
    Question newQuestion = questionRepository.findById(question.getId()).get();
    assertThat(updateContent).isEqualTo(newQuestion.getContent());
    assertThat(updateTitle).isEqualTo(newQuestion.getTitle());
  }

  @Test
  @DisplayName("일반 유저 관리자 권한 수정 호출")
  public void 일반유저_관리자_권한_수정() throws Exception {
    //given
    userRepository.save(user);
    String updateContent = "updateContent";
    String updateTitle = "updateTitle";
    QuestionUpdateDto questionUpdateDto = QuestionUpdateDto.builder()
        .title(updateTitle)
        .content(updateContent)
        .build();
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user.makeSessionValue());
    String url = ADMIN_API + question.getId();
    //when
    mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(questionUpdateDto))
        .session(session))
        .andExpect(status().isForbidden());
    //then
    Question newQuestion = questionRepository.findById(question.getId()).get();
    assertThat(content).isEqualTo(newQuestion.getContent());
    assertThat(title).isEqualTo(newQuestion.getTitle());
    assertThat(userId).isEqualTo(newQuestion.getUser().getUserId());

  }

  @Test(expected = IllegalArgumentException.class)
  @DisplayName("관리자 권한 삭제")
  public void 관리자_권한_삭제() throws Exception {
    //given
    userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    String userId = "admin";
    User admin = User.builder()
        .userId(userId)
        .name("a")
        .password("1234")
        .email("aa@22.com")
        .build();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, admin.makeSessionValue());
    String url = ADMIN_API + question.getId().toString();
    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session))
        .andExpect(status().isOk());
    //then
    questionRepository.findById(question.getId()).orElseThrow(() -> new IllegalArgumentException());

  }

  @Test
  @DisplayName("일반 유저 관리자 권한 삭제 호출")
  public void 일반유저_관리자_권한_삭제() throws Exception {
    //given
    userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(user));
    String userId = "user";
    User admin = User.builder()
        .userId(userId)
        .name("a")
        .password("1234")
        .email("aa@22.com")
        .build();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, admin.makeSessionValue());
    String url = ADMIN_API + question.getId().toString();
    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session))
        .andExpect(status().isForbidden());
    //then
    assertThat(questionRepository.findById(question.getId())).isNotEmpty();
  }


}