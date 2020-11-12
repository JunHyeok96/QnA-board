package com.board.domain.post.exception;

public class MissmatchAuthor extends IllegalStateException {

  public MissmatchAuthor(String msg) {
    super(msg);
  }
}
