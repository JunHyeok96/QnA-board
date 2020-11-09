package com.board.web.controller;

import com.board.domain.user.User;
import com.board.service.UserService;
import com.board.web.HttpSessionUtils;
import com.board.web.dto.user.UserLoginRequestDto;
import com.board.web.dto.user.UserRequestDto;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
  public String create(UserRequestDto user) {
    userService.save(user);
    return "redirect:/";
  }

  @GetMapping("/user/login")
  public String loginForm(HttpSession session, HttpServletResponse response) {
    return HttpSessionUtils.isLoginUser(session) ? "redirect:/" : "user/login";
  }

  @PostMapping("/user/login")
  public String login(@RequestBody UserLoginRequestDto requestDto , HttpSession session,
      HttpServletResponse response) {
    if (userService.login(requestDto.getUserId(), requestDto.getPassword(), session)) {
      return "redirect:/";
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return null;
    }
  }

  @GetMapping("/user/logout")
  public String logout(HttpSession session) {
    HttpSessionUtils.logout(session);
    return "redirect:/";
  }

  @GetMapping("/user/list")
  public String list(Model model) {
    model.addAttribute("users", userService.findAll());
    return "user/list";
  }

  @GetMapping("/user/{id}/form")
  public String userForm(@PathVariable Long id, Model model, HttpSession session) {
    if (!HttpSessionUtils.isLoginUser(session)) {
      return "redirect:user/login";
    } else {
      User user = HttpSessionUtils.getUserFromSession(session);
      model.addAttribute("user", userService.findById(user.getId()));
      return "user/updateForm";
    }
  }


}
