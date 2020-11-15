package com.board.domain.user.exception;

public class UserNotFoundException extends IllegalStateException{
  public UserNotFoundException(String msg) {
    super(msg);
  }
}
