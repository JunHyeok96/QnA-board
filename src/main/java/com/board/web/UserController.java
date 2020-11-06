package com.board.web;

import com.board.domain.user.UserRepository;
import com.board.service.UserService;
import com.board.web.dto.user.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;

  @GetMapping("/user/form")
  public String signUp() {
    return "user/form";
  }

  @PostMapping("/user/create")
  public String create(UserRequestDto user) {
    userService.save(user);
    return "redirect:/user/list";
  }

  @GetMapping("/user/list")
  public String list(Model model){
    model.addAttribute("users", userService.findAll());
    return "user/list";
  }

  @PutMapping("/user/{id}/update")
  public String update(@PathVariable Long id, @RequestBody UserRequestDto user, Model model){
    userService.update(id, user);
    model.addAttribute("user", userService.findById(id));
    return "user/updateForm";
  }


}
