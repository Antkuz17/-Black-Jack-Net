// src/App.js
import React, { useEffect, useRef, useState } from 'react';

function App() {
  const [messages, setMessages] = useState([]);
  const ws = useRef(null);

  useEffect(() => {
    const hostname = window.location.hostname;
    ws.current = new WebSocket(`ws://${hostname}:8080/game`);

    ws.current.onopen = () => {
      console.log('✅ WebSocket connected');
    };

    ws.current.onmessage = (event) => {
      setMessages((prev) => [...prev, event.data]);
    };

    ws.current.onclose = () => {
      console.log('❌ WebSocket closed');
    };

    return () => ws.current.close();
  }, []);

  const sendMessage = () => {
    if (ws.current && ws.current.readyState === WebSocket.OPEN) {
      ws.current.send('Player hit');
    }
  };

  return (
    <div>
      <h1>Multiplayer Test</h1>
      <button onClick={sendMessage}>Hit</button>
      <ul>
        {messages.map((msg, i) => <li key={i}>{msg}</li>)}
      </ul>
    </div>
  );
}

export default App;
