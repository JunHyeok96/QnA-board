package com.board.domain.user.exception;

public class UserMismatchException extends IllegalStateException {

  public UserMismatchException(String msg) {
    super(msg);
  }
}
