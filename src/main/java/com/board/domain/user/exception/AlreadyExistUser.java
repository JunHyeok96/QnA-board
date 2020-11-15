package com.board.domain.user.exception;

public class AlreadyExistUser extends IllegalArgumentException {

  public AlreadyExistUser(String msg) {
    super(msg);
  }
}
