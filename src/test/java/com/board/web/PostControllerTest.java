package com.board.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.assertj.core.api.Assertions.assertThat;

import com.board.domain.post.Post;
import com.board.domain.post.PostRepository;
import com.board.web.dto.post.PostRequestDto;
import com.board.web.dto.post.PostResponseDto;
import com.board.web.dto.post.PostUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PostControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private PostRepository postRepository;

  private MockMvc mvc;

  private ObjectMapper mapper;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setup() {
    this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
  }

  @After
  public void tearDown() throws Exception {
    postRepository.deleteAll();
  }

  @Test
  public void 단일게시물조회() throws Exception {
    //given
    String title = "title";
    String content = "content";
    String postType = "Q";
    String userId = "test-user";
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
    String title = "title";
    String content = "content";
    String postType = "Q";
    String userId = "test-user";

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
    String title = "title";
    String content = "content";
    String postType = "Q";
    String userId = "test-user";

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

    Long id = postRepository.save(postRequestDto.toEntity()).getId();

    String url = "http://localhost:" + port + "/post/" + id.toString();

    mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(postUpdateDto)))
        .andExpect(status().isOk());

    Post post = postRepository.findById(id).get();

    assertThat(updateContent).isEqualTo(post.getContent());
    assertThat(updateTitle).isEqualTo(post.getTitle());
    assertThat(postType).isEqualTo(post.getPostType().toString());
    assertThat(userId).isEqualTo(post.getUserId());

  }

  @Test
  public void 게시물저장() throws Exception {
    String title = "title";
    String content = "content";
    String postType = "Q";
    String userId = "test-user";

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
  public void 게시글삭제() throws Exception {

    String title = "title";
    String content = "content";
    String postType = "Q";
    String userId = "test-user";

    PostRequestDto postRequestDto = PostRequestDto.builder()
        .title(title)
        .content(content)
        .postType(postType)
        .userId(userId)
        .build();

    Long id = postRepository.save(postRequestDto.toEntity()).getId();

    assertThat(postRepository.findAll().size()).isEqualTo(1);

    String url = "http://localhost:" + port + "/post/" + id.toString();

    mvc.perform(MockMvcRequestBuilders.delete(url)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    assertThat(postRepository.findAll().size()).isEqualTo(0);

  }
}