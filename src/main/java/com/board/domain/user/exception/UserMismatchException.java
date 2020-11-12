package com.board.domain.user.exception;

public class UserMismatchException extends LoginException {

  public UserMismatchException(String msg) {
    super(msg);
  }
}
