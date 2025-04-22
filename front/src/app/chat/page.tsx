'use client';

import { useState, useEffect } from 'react';
import ChatRoom from '../../components/ChatRoom';
import NicknameForm from '../../components/NicknameForm';
import { initializeSocket, disconnectSocket } from '../../utils/socket';

export default function ChatPage() {
  const [nickname, setNickname] = useState<string>('');
  const [isJoined, setIsJoined] = useState<boolean>(false);

  useEffect(() => {
    const socket = initializeSocket();
    socket.connect();

    return () => {
      disconnectSocket();
    };
  }, []);

  const handleJoin = (name: string) => {
    setNickname(name);
    setIsJoined(true);
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-center mb-8">익명 채팅</h1>
        {!isJoined ? (
          <NicknameForm onJoin={handleJoin} />
        ) : (
          <ChatRoom nickname={nickname} />
        )}
      </div>
    </div>
  );
} 