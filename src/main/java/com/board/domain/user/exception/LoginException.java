package com.board.domain.user.exception;

public class LoginException extends IllegalStateException {
  public LoginException(String msg) {
    super(msg);
  }
}
