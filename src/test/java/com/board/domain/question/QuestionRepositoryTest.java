package com.board.domain.question;

import static org.assertj.core.api.Assertions.assertThat;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.web.dto.user.UserRequestDto;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuestionRepositoryTest {

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private UserRepository userRepository;

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

}