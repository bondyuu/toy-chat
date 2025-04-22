export interface Message {
  id: string;
  nickname: string;
  content: string;
  timestamp: string;
}

export interface ChatUser {
  id: string;
  nickname: string;
}

export interface MessageTemp {
  username: string;
  message: string;
  room: string;
}

export interface ServerToClientEvents {
  'chat_message': (message: MessageTemp) => void;
  'chat_join': (user: ChatUser) => void;
  'chat_leave': (user: ChatUser) => void;
  'chat_users': (users: ChatUser[]) => void;
}

export interface ClientToServerEvents {
  'chat_join': (nickname: string) => void;
  'chat_message': (content: MessageTemp) => void;
  'chat_leave': () => void;
} 