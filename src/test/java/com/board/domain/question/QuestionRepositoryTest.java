package com.board.domain.question;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.domain.answer.Answer;
import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.question.QuestionRequestDto;
import com.board.web.dto.user.UserRequestDto;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import javax.persistence.PersistenceUtil;

@DataJpaTest
@RunWith(SpringRunner.class)
public class QuestionRepositoryTest {

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager em;

  PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();

  private final int BATCH_SIZE = 5;

  @Test
  @DisplayName("질문 저장하기")
  public void questionTest() {
    //given
    UserRequestDto user = UserRequestDto.builder()
        .userId("abcd")
        .email("aa@aaa.com")
        .name("jay")
        .password("0000")
        .build();
    userRepository.save(user.toEntity());
    User user1 = userRepository.findByUserId("abcd");
    String title = "질문 입니다.";
    String content = "질문 내용입니다.";
    LocalDateTime now = LocalDateTime.now();
    Question questionRequestDto = Question.builder()
        .content(content)
        .title(title)
        .user(user1)
        .build();
    //when
    questionRepository.save(questionRequestDto);
    //then
    List<Question> questions = questionRepository.findAll();
    Question question = questions.get(0);
    assertThat(title).isEqualTo(question.getTitle());
    assertThat(content).isEqualTo(question.getContent());
    assertThat(question.getCreateDate()).isAfter(now);
    assertThat(question.getModifiedDate()).isAfter(now);

  }

  @Test
  @DisplayName("전체 게시글 조회 즉시 로딩 확인")
  public void 전체게시글_로딩확인() {
    //given
    insertQuestionAndAnswer(BATCH_SIZE * 3);
    //when
    List<Question> questionsAll = questionRepository.findAll();
    //then
    assertThat(questionsAll.size()).isEqualTo(BATCH_SIZE * 3);
    questionsAll.stream().forEach(i -> {
      assertThat(persistenceUtil.isLoaded(i.getUser())).isTrue();
      assertThat(persistenceUtil.isLoaded(i.getAnswers())).isTrue();
    });
  }

  @Test
  @DisplayName("질문글 paging 로딩확인 - 모든 질문글")
  public void 질문글_paging_로딩확인1() {
    //given
    insertQuestionAndAnswer(BATCH_SIZE * 3);
    //when
    PageRequest request = PageRequest.of(0, BATCH_SIZE * 2, Sort.by("createDate").descending());
    List<Question> questionsAll = questionRepository.findAll(request).getContent();
    //then
    assertThat(questionsAll.size()).isEqualTo(BATCH_SIZE * 2);
    questionsAll.stream().forEach(i -> {
      assertThat(persistenceUtil.isLoaded(i.getUser())).isTrue();
      assertThat(persistenceUtil.isLoaded(i.getAnswers())).isFalse();
    });
  }

  @Test
  @DisplayName("질문글 paging 로딩확인 - userID(String)")
  public void 질문글_paging_로딩확인2() {
    //given
    insertQuestionAndAnswer(BATCH_SIZE * 3);
    String userId = userRepository.findByUserId("admin").getUserId();
    //when
    PageRequest request = PageRequest.of(0, BATCH_SIZE * 2, Sort.by("createDate").descending());
    List<Question> questionsAll = questionRepository.findByUserId(userId, request).getContent();
    //then
    assertThat(questionsAll.size()).isEqualTo(BATCH_SIZE * 2);
    questionsAll.stream().forEach(i -> {
      assertThat(persistenceUtil.isLoaded(i.getUser())).isTrue();
      assertThat(persistenceUtil.isLoaded(i.getAnswers())).isFalse();
    });
  }

  @Test
  @DisplayName("질문글 paging 로딩확인 - userID(Long)")
  public void 질문글_paging_로딩확인3() {
    //given
    insertQuestionAndAnswer(BATCH_SIZE * 3);
    Long id = userRepository.findByUserId("admin").getId();
    //when
    PageRequest request = PageRequest.of(0, BATCH_SIZE * 2, Sort.by("createDate").descending());
    List<Question> questionsAll = questionRepository.findByUserId(id, request).getContent();
    //then
    assertThat(questionsAll.size()).isEqualTo(BATCH_SIZE * 2);
    questionsAll.stream().forEach(i -> {
      assertThat(persistenceUtil.isLoaded(i.getUser())).isTrue();
      assertThat(persistenceUtil.isLoaded(i.getAnswers())).isFalse();
    });
  }

  @Test
  @DisplayName("search 로딩확인")
  public void search_로딩확인() {
    //given
    insertQuestionAndAnswer(BATCH_SIZE * 3);
    String keyword = "스프링";
    //when
    PageRequest request = PageRequest.of(0, BATCH_SIZE * 2, Sort.by("createDate").descending());
    List<Question> searchList = questionRepository.search(keyword, request).getContent();
    //then
    assertThat(searchList.size()).isEqualTo(BATCH_SIZE * 2);
    searchList.stream().forEach(i -> {
      assertThat(persistenceUtil.isLoaded(i.getUser())).isTrue();
      assertThat(persistenceUtil.isLoaded(i.getAnswers())).isFalse();
    });
  }

  @DisplayName("질문, 답변 데이터 생성")
  public void insertQuestionAndAnswer(int size) {
    QuestionRequestDto questionRequestDto = QuestionRequestDto.builder()
        .content("스프링관련 질문 내용입니다.")
        .title("스프링관련 질문입니다.")
        .build();
    User user = User.builder()
        .userId("admin")
        .email("aa@aaa.com")
        .name("jay")
        .password("0000")
        .build();
    userRepository.save(user);

    for (int i = 0; i < size; i++) {
      Question question = questionRequestDto.toEntity(user);
      Answer answer = Answer.builder()
          .content("answer" + i)
          .question(question)
          .user(user)
          .build();
      question.getAnswers().add(answer);
      em.persist(question);
      em.persist(answer);
    }
    em.clear();
  }

}