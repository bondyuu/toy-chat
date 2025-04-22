'use client';

import { useState, useRef, useEffect } from 'react';
import { Message, ChatUser, MessageTemp } from '../types/chat';
import { getSocket } from '../utils/socket';

interface ChatRoomProps {
  nickname: string;
}

export default function ChatRoom({ nickname }: ChatRoomProps) {
  const [messages, setMessages] = useState<MessageTemp[]>([]);
  const [users, setUsers] = useState<ChatUser[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const socket = getSocket();

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    socket.on('chat_message', (message: MessageTemp) => {
      setMessages(prev => [...prev, message]);
    });

    socket.on('chat_join', (user: ChatUser) => {
      setUsers(prev => [...prev, user]);
    });

    socket.on('chat_leave', (user: ChatUser) => {
      setUsers(prev => prev.filter(u => u.id !== user.id));
    });

    socket.on('chat_users', (users: ChatUser[]) => {
      setUsers(users);
    });

    socket.emit('chat_join', nickname);

    return () => {
      socket.emit('chat_leave');
      socket.off('chat_message');
      socket.off('chat_join');
      socket.off('chat_leave');
      socket.off('chat_users');
    };
  }, [nickname, socket]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (newMessage.trim()) {
      socket.emit('chat_message', {
        username: nickname,
        message: newMessage.trim(),
        room: "test",
      });
      setNewMessage('');
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md h-[600px] flex flex-col">
      <div className="p-4 border-b">
        <h2 className="text-xl font-semibold">채팅방</h2>
        <p className="text-sm text-gray-500">현재 닉네임: {nickname}</p>
        <p className="text-sm text-gray-500">접속자 수: {users.length}명</p>
      </div>
      
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((message) => (
          <div
            key={message.username}
            className={`flex flex-col ${
              message.username === nickname ? 'items-end' : 'items-start'
            }`}
          >
            <div
              className={`max-w-[70%] rounded-lg p-3 ${
                message.username === nickname
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-100 text-gray-800'
              }`}
            >
              <div className="text-sm font-medium mb-1">
                {message.username}
              </div>
              <div>{message.message}</div>
              <div className="text-xs mt-1 opacity-70">
                {message.room}
              </div>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <form onSubmit={handleSubmit} className="p-4 border-t">
        <div className="flex space-x-2">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="메시지를 입력하세요..."
            maxLength={200}
          />
          <button
            type="submit"
            className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            전송
          </button>
        </div>
      </form>
    </div>
  );
} 