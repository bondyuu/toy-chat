package com.bondyu.toy_chat.chat.handler;

import com.bondyu.toy_chat.chat.dto.ChatDto.ChatMessage;
import com.bondyu.toy_chat.chat.service.ChatService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSocketHandler {
    
    private final SocketIOServer socketIOServer;
    private final ChatService chatService;

    @PostConstruct
    private void init() {
        try {
            socketIOServer.start();
            log.info("Socket.IO server started on port {}", socketIOServer.getConfiguration().getPort());
        } catch (Exception e) {
            log.error("Failed to start Socket.IO server", e);
        }
    }

    @PreDestroy
    private void destroy() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            log.info("Socket.IO server stopped");
        }
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
        client.joinRoom("general");
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("Client disconnected: {}", client.getSessionId());
    }

    @OnEvent("chat_message")
    public void onChatMessage(SocketIOClient client, String message) {
        log.info("Received message from client {}: {}", client.getSessionId(), message);
        try {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUsername("user");
            chatMessage.setMessage(message);
            chatMessage.setRoom("general");
            
            socketIOServer.getRoomOperations(chatMessage.getRoom())
                    .sendEvent("chat_message", chatMessage);
            
            chatService.sendMessage(chatMessage);
            
            log.debug("Message broadcast complete");
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    @OnEvent("chat_join")
    public void onJoinRoom(SocketIOClient client, String room) {
        client.joinRoom(room);
        log.info("Client {} joined room {}", client.getSessionId(), room);
        
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setUsername("system");
        joinMessage.setMessage("New user joined the room");
        joinMessage.setRoom(room);
        
        socketIOServer.getRoomOperations(room)
                .sendEvent("chat_message", joinMessage);
    }

    @OnEvent("chat_leave")
    public void onLeaveRoom(SocketIOClient client, String room) {
        client.leaveRoom(room);
        log.info("Client {} left room {}", client.getSessionId(), room);
        
        ChatMessage leaveMessage = new ChatMessage();
        leaveMessage.setUsername("system");
        leaveMessage.setMessage("User left the room");
        leaveMessage.setRoom(room);
        
        socketIOServer.getRoomOperations(room)
                .sendEvent("chat_message", leaveMessage);
    }
}
