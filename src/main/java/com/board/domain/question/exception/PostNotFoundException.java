package com.board.domain.question.exception;

public class PostNotFoundException extends IllegalArgumentException {

  public PostNotFoundException(String msg) {
    super(msg);
  }
}
