package com.bondyu.toy_chat.global.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bondyu.toy_chat.chat.dto.ChatDto.ChatMessage;
import com.corundumstudio.socketio.SocketIOServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final SocketIOServer socketIOServer;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessage chatMessage = (ChatMessage) redisTemplate.getValueSerializer()
                    .deserialize(message.getBody());
            
            socketIOServer.getRoomOperations("general")
                    .sendEvent("chat:message", chatMessage);
                    
            log.info("Message received: {}", chatMessage);
        } catch (Exception e) {
            log.error("Error processing message: ", e);
        }
    }
}
