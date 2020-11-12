package com.board.domain.post.exception;

public class PostNotFoundException extends IllegalArgumentException {

  public PostNotFoundException(String msg) {
    super(msg);
  }
}
