package com.board.domain.user.exception;

public class UserNotFoundException extends LoginException{
  public UserNotFoundException(String msg) {
    super(msg);
  }
}
