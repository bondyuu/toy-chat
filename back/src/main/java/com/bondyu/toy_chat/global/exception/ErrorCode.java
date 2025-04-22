package com.bondyu.toy_chat.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

  INVALID_REQUEST(400, "Invalid request"),
  UNAUTHORIZED(401, "Unauthorized"),
  NOT_FOUND(404, "Not found");
  
  private final int code;
  private final String message;
}
