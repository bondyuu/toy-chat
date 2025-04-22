package com.bondyu.toy_chat.chat.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.bondyu.toy_chat.chat.dto.ChatDto.ChatMessage;
import com.corundumstudio.socketio.SocketIOServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SocketIOServer socketIOServer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void sendMessage(ChatMessage message) {
        try {
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
            log.info("Message sent to Redis: {}", message);
        } catch (Exception e) {
            log.error("Error sending message: ", e);
        }
    }
}
