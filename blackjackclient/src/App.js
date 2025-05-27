import React, {useEffect, useRef, useState} from 'react';

function App() {
  const [messages, setMessages] = useState([]);
  const ws = useRef(null);
  useEffect(() => {
    // Connect to Spring Boot WebSocket
    ws.current = new WebSocket('ws://10.0.0.214:8080/game');

    ws.current.onopen = () => {
      console.log('✅ WebSocket connected');
      ws.current.send('Hello from React!');
    };

    ws.current.onmessage = (event) => {
      console.log('📩 Message from server:', event.data);
      setMessages((prev) => [...prev, event.data]);
    };

    ws.current.onclose = () => {
      console.log('❌ WebSocket disconnected');
    };

    return () => {
      ws.current.close();
    };
  }, []);

  const sendMessage = () => {
    if (ws.current && ws.current.readyState === WebSocket.OPEN) {
      ws.current.send('Player move: Hit');
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>🃏 Blackjack Client</h1>
      <button onClick={sendMessage}>Send "Hit" to Server</button>

      <div style={{ marginTop: '20px' }}>
        <h3>Server Messages:</h3>
        <ul>
          {messages.map((msg, i) => (
            <li key={i}>{msg}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default App;
