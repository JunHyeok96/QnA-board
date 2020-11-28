package com.board.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.board.domain.user.User;
import com.board.domain.user.UserRepository;
import com.board.domain.user.exception.AlreadyExistUser;
import com.board.domain.user.exception.LoginException;
import com.board.domain.user.exception.UserMismatchException;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserLoginRequestDto;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserServiceTest {

  private UserService userService;
  private static MockedStatic<HttpSessionUtils> mockedSessionUtils;
  private MockHttpSession mockHttpSession;

  @Mock
  private UserRepository mockUserRepository;

  @Mock
  private User mockUser;

  @BeforeClass
  public static void beforeClass() {
    mockedSessionUtils = mockStatic(HttpSessionUtils.class);
  }

  @AfterClass
  public static void afterClass() {
    mockedSessionUtils.close();
  }

  @Before
  public void setUp() {
    mockHttpSession = new MockHttpSession();
  }

  @Test
  @DisplayName("사용자 - 정보 수정")
  public void updateUser() {
    //given
    UserResponseDto SessionedUser = mock(UserResponseDto.class);
    UserRequestDto updateUser = mock(UserRequestDto.class);

    userService = new UserService(mockUserRepository);

    when(HttpSessionUtils.isLoginUser(any())).thenReturn(true);
    when(HttpSessionUtils.getUserFromSession(any())).thenReturn(SessionedUser);
    when(mockUserRepository.findByUserId(any())).thenReturn(mockUser);
    when(mockUser.matchUserId(any())).thenReturn(true);
    when(mockUser.update(updateUser)).thenReturn(mockUser);
    //when
    Long id = userService.update(updateUser, mockHttpSession);
    //then
    verify(mockUser).update(any());
    assertNotNull(id);

  }

  @Test(expected = UserMismatchException.class)
  @DisplayName("사용자 - 타인의 정보 수정")
  public void failUpdateUser() {
    //given
    userService = new UserService(mockUserRepository);
    UserResponseDto SessionedUser = mock(UserResponseDto.class);
    UserRequestDto updateUser = mock(UserRequestDto.class);
    when(HttpSessionUtils.isLoginUser(any())).thenReturn(true);
    when(HttpSessionUtils.getUserFromSession(any())).thenReturn(SessionedUser);
    when(mockUserRepository.findByUserId(any())).thenReturn(mockUser);
    when(mockUser.matchUserId(any())).thenReturn(false);
    //when, then
    userService.update(updateUser, mockHttpSession);
  }

  @Test
  @DisplayName("사용자 - 등록")
  public void saveUser() {
    //given
    userService = new UserService(mockUserRepository);
    UserRequestDto mockUserRequestDto = mock(UserRequestDto.class);
    when(mockUserRepository.findByUserId(any())).thenReturn(null);
    when(mockUserRepository.save(any())).thenReturn(mockUser);
    when(mockUserRequestDto.isVaild()).thenReturn(true);
    when(mockUser.getId()).thenReturn(0L);
    //when
    Long id = userService.save(mockUserRequestDto);
    //then
    assertNotNull(id);
  }

  @Test(expected = AlreadyExistUser.class)
  @DisplayName("사용자 - 이미 등록된 아이디")
  public void failSaveUser() {
    //given
    userService = new UserService(mockUserRepository);
    UserRequestDto mockUserRequestDto = mock(UserRequestDto.class);
    when(mockUserRepository.findByUserId(any())).thenReturn(mockUser);
    when(mockUserRequestDto.isVaild()).thenReturn(true);
    //when, then
    userService.save(mockUserRequestDto);
  }

  @Test
  @DisplayName("로그인 - 이미 로그인후 다시 로그인 시도")
  public void loginAgain() {
    //given
    userService = new UserService(mockUserRepository);
    UserLoginRequestDto mockLoginDto = mock(UserLoginRequestDto.class);
    when(HttpSessionUtils.isLoginUser(any())).thenReturn(true);
    when(mockLoginDto.isVaild()).thenReturn(true);
    //when
    boolean isLogin = userService.login(mockLoginDto, mockHttpSession);
    //then
    assertTrue(isLogin);
  }

  @Test(expected = LoginException.class)
  @DisplayName("로그인 - 비밀번호 오류")
  public void failLoginByPassword() {
    //given
    userService = new UserService(mockUserRepository);
    UserLoginRequestDto mockLoginDto = mock(UserLoginRequestDto.class);
    when(HttpSessionUtils.isLoginUser(any())).thenReturn(false);
    when(mockUserRepository.findByUserId(any())).thenReturn(mockUser);
    when(mockUser.matchPassword(any())).thenReturn(false);
    //when, then
    boolean isLogin = userService.login(mockLoginDto, mockHttpSession);

  }

  @Test(expected = LoginException.class)
  @DisplayName("로그인 - 존재하지 않는 유저")
  public void failLoginByNotExist() {
    //given
    userService = new UserService(mockUserRepository);
    UserLoginRequestDto mockLoginDto = mock(UserLoginRequestDto.class);
    when(HttpSessionUtils.isLoginUser(any())).thenReturn(false);
    when(mockUserRepository.findByUserId(any())).thenReturn(null);
    //when, then
    boolean isLogin = userService.login(mockLoginDto, mockHttpSession);

  }

  @Test
  @DisplayName("로그인 - 성공")
  public void successLogin() {
    //given
    userService = new UserService(mockUserRepository);
    UserLoginRequestDto mockLoginDto = mock(UserLoginRequestDto.class);
    when(HttpSessionUtils.isLoginUser(any())).thenReturn(false);
    when(mockUserRepository.findByUserId(any())).thenReturn(mockUser);
    when(mockUser.matchPassword(any())).thenReturn(true);
    when(mockLoginDto.isVaild()).thenReturn(true);
    //when
    boolean isLogin = userService.login(mockLoginDto, mockHttpSession);
    //then
    assertTrue(isLogin);
  }
}