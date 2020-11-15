package com.board.web;

import com.board.domain.post.exception.MissmatchAuthor;
import com.board.domain.user.exception.AlreadyExistUser;
import com.board.domain.user.exception.LoginException;
import com.board.domain.user.exception.UserMismatchException;
import com.board.domain.user.exception.UserNotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(value = {LoginException.class})
  public ResponseEntity handleLoginException(HttpServletRequest request,
      Exception e) {
    log.error(String.valueOf(request.getMethod()));
    log.error(String.valueOf(request.getRequestURL()));
    return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(value = {UserMismatchException.class, MissmatchAuthor.class})
  public ResponseEntity handleUserMismatchException(HttpServletRequest request,
      Exception e) {
    log.error(String.valueOf(request.getMethod()));
    log.error(String.valueOf(request.getRequestURL()));
    return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = UserNotFoundException.class)
  public ResponseEntity handleUserNotFoundException(HttpServletRequest request,
      UserNotFoundException e) {
    log.error(String.valueOf(request.getMethod()));
    log.error(String.valueOf(request.getRequestURL()));
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = AlreadyExistUser.class)
  public ResponseEntity handleAlreadyExistUser(HttpServletRequest request,
      AlreadyExistUser e) {
    log.error(String.valueOf(request.getMethod()));
    log.error(String.valueOf(request.getRequestURL()));
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

}
