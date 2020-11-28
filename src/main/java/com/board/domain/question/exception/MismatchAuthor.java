package com.board.domain.question.exception;

public class MismatchAuthor extends IllegalStateException {

  public MismatchAuthor(String msg) {
    super(msg);
  }
}
