package com.board.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.domain.answer.Answer;
import com.board.domain.answer.AnswerRepository;
import com.board.domain.question.Question;
import com.board.domain.question.QuestionRepository;
import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.Answer.AnswerRequsetDto;
import com.board.web.dto.Answer.AnswerResponseDto;
import com.board.web.dto.question.QuestionRequestDto;
import com.board.web.dto.user.UserResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.stream.Collectors;
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
public class AnswerControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private AnswerRepository answerRepository;

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private UserRepository userRepository;

  private MockMvc mvc;

  private ObjectMapper mapper;

  private MockHttpSession session = new MockHttpSession();

  @Autowired
  private WebApplicationContext webApplicationContext;

  private final String API = "http://localhost:" + port + "/api/v1/answers/";
  private final String ADMIN_API = "http://localhost:" + port + "/api/v1/admin/answers/";

  private final QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
      .title("title")
      .content("content")
      .build();

  private User user = User.builder()
      .userId("user id")
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
    answerRepository.deleteAll();
    questionRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("답변 조회")
  public void 답변조회() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    String content = "abcd";
    AnswerRequsetDto answerRequsetDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    Long id = answerRepository.save(answerRequsetDto.toEntity(question, testUser)).getId();
    String url = API + question.getId();
    //when
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andReturn();
    //then
    String returnContent = result.getResponse().getContentAsString();
    List<AnswerResponseDto> list = mapper
        .readValue(returnContent, new TypeReference<List<AnswerResponseDto>>() {
        });
    Answer answer = answerRepository.findById(id).get();
    assertThat(answer.getContent()).isEqualTo(list.get(0).getContent());
  }

  @Test
  @DisplayName("답변 저장")
  public void 답변저장() throws Exception {
    //given
    User testUser = userRepository.save(user);
    long questionId = questionRepository.save(questionRequestDto.toEntity(testUser)).getId();
    String content = "답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(questionId)
        .build();
    String url = API;
    //when
    mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(requestDto))
        .session(session))
        .andExpect(status().isOk());

    //then
    Answer answer = answerRepository.findAll().get(0);
    assertThat(answer.getContent()).isEqualTo(content);
    assertThat(answer.getQuestion().getId()).isEqualTo(questionId);
  }

  @Test
  @DisplayName("답변 수정")
  public void 답변수정() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(user));
    String content = "답변입니다.";
    String updateContent = "수정된 답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();
    String url = API + answerId;
    AnswerRequsetDto updateRequestDto = AnswerRequsetDto.builder()
        .content(updateContent)
        .questionId(question.getId())
        .build();
    //when
    mvc.perform(MockMvcRequestBuilders.put(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateRequestDto))
        .session(session))
        .andExpect(status().isOk());
    //then
    Answer updateAnswer = answerRepository.findById(answerId).get();
    assertThat(updateAnswer.getContent()).isEqualTo(updateContent);
  }

  @Test
  @DisplayName("타인의 답변 수정")
  public void 타인의_답변_수정() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    User anotherUser = User.builder()
        .email("111")
        .name("2222")
        .password("333")
        .userId("444")
        .build();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(anotherUser));
    String content = "답변입니다.";
    String updateContent = "수정된 답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();

    String url = API + answerId;

    AnswerRequsetDto updateRequestDto = AnswerRequsetDto.builder()
        .content(updateContent)
        .questionId(question.getId())
        .build();

    //when
    mvc.perform(MockMvcRequestBuilders.put(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateRequestDto))
        .session(session))
        .andExpect(status().isForbidden());

    //then
    Answer updateAnswer = answerRepository.findById(answerId).get();
    assertThat(updateAnswer.getContent()).isNotEqualTo(updateContent);

  }

  @Test(expected = IllegalArgumentException.class)
  @DisplayName("답변 삭제")
  public void 답변삭제() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(user));
    String content = "답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();
    String url = API + answerId;
    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .session(session))
        .andExpect(status().isOk());
    //then
    answerRepository.findById(answerId).orElseThrow(() -> new IllegalArgumentException());
  }

  @Test()
  @DisplayName("타인의 답변 삭제")
  public void 타인의_답변_삭제() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    User anotherUser = User.builder()
        .email("111")
        .name("2222")
        .password("333")
        .userId("444")
        .build();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(anotherUser));
    String content = "답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();
    String url = API + answerId;
    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .session(session))
        .andExpect(status().isForbidden());
    //then
    Answer answer = answerRepository.findById(answerId).get();
    assertThat(answer.getContent()).isEqualTo(content);
  }

  @Test
  @DisplayName("관리자 권한 답변 수정")
  public void 관리자권한_답변수정() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    String userId = "admin";
    User admin = User.builder()
        .email("111")
        .name("2222")
        .password("333")
        .userId(userId)
        .build();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(admin));
    String content = "답변입니다.";
    String updateContent = "수정된 답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();
    String url = ADMIN_API + answerId;
    AnswerRequsetDto updateRequestDto = AnswerRequsetDto.builder()
        .content(updateContent)
        .questionId(question.getId())
        .build();
    //when
    mvc.perform(MockMvcRequestBuilders.put(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateRequestDto))
        .session(session))
        .andExpect(status().isOk());
    //then
    Answer updateAnswer = answerRepository.findById(answerId).get();
    assertThat(updateAnswer.getContent()).isEqualTo(updateContent);
  }

  @Test(expected = IllegalArgumentException.class)
  @DisplayName("관리자 권한 답변 삭제")
  public void 관리자_권한_답변삭제() throws Exception {
    //given
    String userId = "admin";
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    User admin = User.builder()
        .email("111")
        .name("2222")
        .password("333")
        .userId(userId)
        .build();
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(admin));
    String content = "답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();
    String url = ADMIN_API + answerId;
    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .session(session))
        .andExpect(status().isOk());
    //then
    answerRepository.findById(answerId).orElseThrow(() -> new IllegalArgumentException());
  }

  @Test
  @DisplayName("일반유저 관리자 권한 삭제호출")
  public void 일반유저_관리자_권한_삭제호출() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(testUser));
    String content = "답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();
    String url = ADMIN_API + answerId;

    //when
    mvc.perform(MockMvcRequestBuilders.delete(url)
        .session(session))
        .andExpect(status().isForbidden());

    //then
    Answer answer = answerRepository.findById(answerId).get();
    assertThat(answer.getContent()).isEqualTo(content);
  }

  @Test
  @DisplayName("일반유저 관리자 권한 수정호출")
  public void 일반유저_관리자_권한_수정호출() throws Exception {
    //given
    User testUser = userRepository.save(user);
    Question question = questionRepository.save(questionRequestDto.toEntity(testUser));
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, new UserResponseDto(testUser));
    String content = "답변입니다.";
    AnswerRequsetDto requestDto = AnswerRequsetDto.builder()
        .content(content)
        .questionId(question.getId())
        .build();
    long answerId = answerRepository.save(requestDto.toEntity(question, testUser)).getId();
    String updateContent = "수정된 답변입니다.";
    AnswerRequsetDto updateRequestDto = AnswerRequsetDto.builder()
        .content(updateContent)
        .questionId(question.getId())
        .build();
    String url = ADMIN_API + answerId;
    //when
    mvc.perform(MockMvcRequestBuilders.put(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(updateRequestDto))
        .session(session))
        .andExpect(status().isForbidden());
    //then
    Answer updateAnswer = answerRepository.findById(answerId).get();
    assertThat(updateAnswer.getContent()).isNotEqualTo(updateContent);
  }
}
