package com.bondyu.toy_chat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChatDto {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ChatMessage {
    private String username;
    private String message;
    private String room;
  }

}
