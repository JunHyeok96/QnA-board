package com.board.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.assertj.core.api.Assertions.assertThat;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.domain.user.UserRepository;
import com.board.service.PostService;
import com.board.web.dto.post.PostRequestDto;
import com.board.web.dto.post.PostResponseDto;
import com.board.web.dto.post.PostUpdateDto;
import com.board.web.dto.user.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import org.springframework.web.util.NestedServletException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PostApiControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  private MockMvc mvc;

  private ObjectMapper mapper;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private final String title = "title";
  private final String content = "content";
  private final String postType = "Q";
  private final String userId = "test-user";

  MockHttpSession session = new MockHttpSession();

  @Before
  public void setup() {
    this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());

    UserRequestDto userRequestDto = UserRequestDto.builder()
        .userId(userId)
        .name("a")
        .password("1234")
        .email("aa@22.com")
        .build();
    userRepository.save(userRequestDto.toEntity());
  }

  @After
  public void tearDown() throws Exception {
    postRepository.deleteAll();
    userRepository.deleteAll();
    ;
  }

  @Test
  public void 단일게시물조회() throws Exception {
    //given
    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId)
        .build();
    Long id = postRepository.save(postRequestDto.toEntity()).getId();
    String url = "http://localhost:" + port + "/post/" + id.toString();

    //when
    MvcResult result =
        mvc.perform(MockMvcRequestBuilders.get(url))
            .andExpect(status().isOk())
            .andReturn();
    String returnContent = result.getResponse().getContentAsString();
    PostResponseDto responseDto = mapper.readValue(returnContent, PostResponseDto.class);

    //then
    Post post = postRepository.findById(id).get();
    assertThat(post.getId()).isEqualTo(responseDto.getId());
    assertThat(post.getContent()).isEqualTo(responseDto.getContent());
    assertThat(post.getTitle()).isEqualTo(responseDto.getTitle());
    assertThat(post.getUserId()).isEqualTo(responseDto.getUserId());
  }

  @Test
  public void 게시물리스트조회() throws Exception {
    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId)
        .build();

    postRepository.save(postRequestDto.toEntity());
    postRepository.save(postRequestDto.toEntity());
    postRepository.save(postRequestDto.toEntity());

    String url = "http://localhost:" + port + "/post/list";

    mvc.perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  public void 게시글수정() throws Exception {
    String updateTitle = "updateTitle";
    String updateContent = "updateContent";

    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId)
        .build();

    PostUpdateDto postUpdateDto = PostUpdateDto.builder()
        .title(updateTitle)
        .content(updateContent)
        .build();

    Post post = postRepository.save(postRequestDto.toEntity());

    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY,
        userRepository.findByUserId(post.getUserId()));

    String url = "http://localhost:" + port + "/post/" + post.getId();

    mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(postUpdateDto))
        .session(session))
        .andExpect(status().isOk());

    Post newPost = postRepository.findById(post.getId()).get();

    assertThat(updateContent).isEqualTo(newPost.getContent());
    assertThat(updateTitle).isEqualTo(newPost.getTitle());
    assertThat(postType).isEqualTo(newPost.getPostType().toString());
    assertThat(userId).isEqualTo(newPost.getUserId());

  }

  @Test
  public void 게시물저장() throws Exception {
    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId)
        .build();

    String url = "http://localhost:" + port + "/post/";

    mvc.perform(MockMvcRequestBuilders.post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(postRequestDto)))
        .andExpect(status().isOk());

    List<Post> posts = postRepository.findAll();
    Post post = posts.get(0);

    assertThat(title).isEqualTo(post.getTitle());
    assertThat(content).isEqualTo(post.getContent());
    assertThat(postType).isEqualTo(post.getPostType().toString());
    assertThat(userId).isEqualTo(post.getUserId());
  }

  @Test
  public void 자신의게시글삭제() throws Exception {

    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId)
        .build();

    Post post = postRepository.save(postRequestDto.toEntity());
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY,
        userRepository.findByUserId(post.getUserId()));

    String url = "http://localhost:" + port + "/post/" + post.getId().toString();

    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session))
        .andExpect(status().isOk());

    assertThat(postRepository.findById(post.getId())).isEqualTo(Optional.empty());

  }

  @Test(expected = NestedServletException.class)
  public void 타인의게시글삭제() throws Exception {

    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId + "_2")
        .build();

    Post post = postRepository.save(postRequestDto.toEntity());
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY,
        userRepository.findByUserId(post.getUserId()));

    String url = "http://localhost:" + port + "/post/" + post.getId().toString();

    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session))
        .andExpect(status().isOk());

    assertThat(postRepository.findById(post.getId())).isEqualTo(Optional.empty());

  }

  @Test(expected = NestedServletException.class)
  public void 타인의게시글수정() throws Exception {

    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId + "_2")
        .build();

    Post post = postRepository.save(postRequestDto.toEntity());
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY,
        userRepository.findByUserId(post.getUserId()));

    String url = "http://localhost:" + port + "/post/" + post.getId().toString();

    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session))
        .andExpect(status().isUnauthorized());

    assertThat(postRepository.findById(post.getId())).isEqualTo(Optional.empty());

  }

}