package com.board.web.controller;

import com.board.config.Auth;
import com.board.service.UserService;
import com.board.web.HttpCookieUtils;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserLoginRequestDto;
import com.board.web.dto.user.UserRequestDto;
import com.board.web.dto.user.UserResponseDto;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.WebUtils;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

  private final UserService userService;

  @GetMapping("/user/form")
  public String signUp() {
    return "user/form";
  }

  @PostMapping("/user/create")
  public String create(@RequestBody UserRequestDto user) {
    userService.save(user);
    return "redirect:/";
  }

  @GetMapping("/user/login")
  public String loginForm(HttpSession session, HttpServletResponse response) {
    return HttpSessionUtils.isLoginUser(session) ? "redirect:/" : "user/login";
  }

  @PostMapping("/user/login")
  public String login(@RequestBody UserLoginRequestDto requestDto, HttpSession session,
      HttpServletResponse response) {
    if (userService.login(requestDto, session)) {
      if (requestDto.isMaintain()) {
        Cookie cookie = new Cookie("loginCookie", session.getId());
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);
      }
      return "redirect:/";
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return null;
    }
  }

  @Auth
  @GetMapping("/user/logout")
  public String logout(HttpSession session, HttpServletRequest request,
      HttpServletResponse response) {
    HttpSessionUtils.logout(session);
    Cookie cookie = WebUtils.getCookie(request, HttpCookieUtils.USER_COOKIE_KEY);
    if (cookie != null) {
      userService.deleteSessionId(cookie.getValue());
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    }
    return "redirect:/";
  }

  @Auth
  @GetMapping("/user/form/update")
  public String userForm(Model model, HttpSession session) {
    UserResponseDto user = HttpSessionUtils.getUserFromSession(session);
    model.addAttribute("user", user);
    return "user/updateForm";
  }
}
