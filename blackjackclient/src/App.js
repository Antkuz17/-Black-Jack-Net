import React, { useState, useEffect, useCallback } from 'react';
import { Users, DollarSign, Play, RotateCcw, Plus, Minus } from 'lucide-react';

const BlackjackGame = () => {
  const [ws, setWs] = useState(null);
  const [gameState, setGameState] = useState(null);
  const [playerName, setPlayerName] = useState('');
  const [roomId, setRoomId] = useState('');
  const [connectionStatus, setConnectionStatus] = useState('disconnected');
  const [messages, setMessages] = useState([]);
  const [betAmount, setBetAmount] = useState(50);
  const [currentPlayerId, setCurrentPlayerId] = useState(null);

  // WebSocket connection
  const connectWebSocket = useCallback(() => {
    try {
      const websocket = new WebSocket('ws://10.10.172.83:8080/game');
      
      websocket.onopen = () => {
        console.log('Connected to WebSocket');
        setConnectionStatus('connected');
        setWs(websocket);
      };

      websocket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        handleWebSocketMessage(message);
      };

      websocket.onclose = () => {
        console.log('WebSocket connection closed');
        setConnectionStatus('disconnected');
        setWs(null);
      };

      websocket.onerror = (error) => {
        console.error('WebSocket error:', error);
        setConnectionStatus('error');
      };

    } catch (error) {
      console.error('Failed to connect:', error);
      setConnectionStatus('error');
    }
  }, []);

  const handleWebSocketMessage = (message) => {
    console.log('Received message:', message);
    
    switch (message.type) {
      case 'gameState':
        setGameState(message.data);
        break;
      case 'roomCreated':
        setRoomId(message.data);
        addMessage(`Room created: ${message.data}`);
        break;
      case 'joinedRoom':
        setRoomId(message.data);
        addMessage(`Joined room: ${message.data}`);
        break;
      case 'info':
        addMessage(message.data);
        break;
      case 'error':
        addMessage(`Error: ${message.data}`, 'error');
        break;
      case 'connection':
        addMessage(message.data);
        break;
      default:
        console.log('Unknown message type:', message.type);
    }
  };

  const addMessage = (text, type = 'info') => {
    setMessages(prev => [...prev.slice(-9), { text, type, timestamp: Date.now() }]);
  };

  const sendMessage = (type, data = null) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      const message = { type, ...data };
      ws.send(JSON.stringify(message));
    }
  };

  // Game actions
  const createRoom = () => {
    if (!playerName.trim()) {
      addMessage('Please enter your name first', 'error');
      return;
    }
    sendMessage('createRoom', { playerName });
  };

  const joinRoom = () => {
    if (!playerName.trim() || !roomId.trim()) {
      addMessage('Please enter your name and room ID', 'error');
      return;
    }
    sendMessage('joinRoom', { playerName , roomId });
  };

  const startGame = () => {
    sendMessage('startGame');
  };

  const placeBet = () => {
    sendMessage('bet', { amount: betAmount });
  };

  const playerHit = () => {
    sendMessage('hit');
  };

  const playerStand = () => {
    sendMessage('stand');
  };

  const playerDoubleDown = () => {
    sendMessage('doubleDown');
  };

  const startNewRound = () => {
    sendMessage('newRound');
  };

  useEffect(() => {
    if (gameState) {
      // Find current player's session ID
      const currentPlayer = Object.values(gameState.players).find(p => p.name === playerName);
      setCurrentPlayerId(currentPlayer?.sessionId || null);
    }
  }, [gameState, playerName]);

  // Component renders
  const renderCard = (card, isHidden = false) => {
    if (isHidden || card.rank === 'Hidden') {
      return (
        <div className="w-16 h-24 bg-blue-600 rounded-lg border-2 border-blue-800 flex items-center justify-center shadow-lg">
          <div className="text-white text-xs">🂠</div>
        </div>
      );
    }

    const isRed = card.suit === 'Hearts' || card.suit === 'Diamonds';
    const suitSymbol = {
      'Hearts': '♥️',
      'Diamonds': '♦️',
      'Clubs': '♣️',
      'Spades': '♠️'
    }[card.suit] || '?';

    return (
      <div className="w-16 h-24 bg-white rounded-lg border-2 border-gray-300 flex flex-col items-center justify-center shadow-lg">
        <div className={`text-sm font-bold ${isRed ? 'text-red-600' : 'text-black'}`}>
          {card.rank}
        </div>
        <div className={`text-lg ${isRed ? 'text-red-600' : 'text-black'}`}>
          {suitSymbol}
        </div>
      </div>
    );
  };

  const renderPlayer = (playerId, player) => {
    const isCurrentTurn = gameState?.currentPlayerId === playerId && gameState?.currentPhase === 'PLAYER_TURNS';
    const isMe = playerId === currentPlayerId;
    
    return (
      <div key={playerId} className={`p-4 rounded-lg border-2 ${
        isCurrentTurn ? 'border-yellow-400 bg-yellow-50' : 
        isMe ? 'border-blue-400 bg-blue-50' : 'border-gray-300 bg-white'
      }`}>
        <div className="flex justify-between items-center mb-2">
          <h3 className={`font-bold ${isMe ? 'text-blue-600' : 'text-gray-800'}`}>
            {player.name} {isMe && '(You)'}
          </h3>
          <div className="text-sm text-gray-600">
            Balance: ${player.balance}
          </div>
        </div>
        
        {player.bet > 0 && (
          <div className="mb-2 text-sm">
            Bet: <span className="font-semibold text-green-600">${player.bet}</span>
          </div>
        )}
        
        <div className="flex gap-1 mb-2">
          {player.cards?.map((card, idx) => renderCard(card, false))}
        </div>
        
        <div className="text-sm">
          Hand Value: <span className={`font-bold ${
            player.isBusted ? 'text-red-600' : 
            player.hasNaturalBlackjack ? 'text-green-600' : 'text-gray-800'
          }`}>
            {player.handValue}
          </span>
          {player.isBusted && <span className="text-red-600 ml-2">BUST!</span>}
          {player.hasNaturalBlackjack && <span className="text-green-600 ml-2">BLACKJACK!</span>}
        </div>
      </div>
    );
  };

  // Connection screen
  if (connectionStatus !== 'connected') {
    return (
      <div
      className="min-h-screen bg-green-800 bg-opacity-80 bg-blend-overlay flex items-center justify-center"
      style={{
        backgroundImage: `url('/26701996-53f2-4859-9476-4cd2561529b2.jpg')`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        height: '500px',
      }}
      >
      <div className="bg-white rounded-lg p-8 shadow-2xl max-w-md w-full">
        <h1 className="text-3xl font-bold text-center mb-6">Blackjack Net</h1>
        <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-2">Username</label>
          <input
          type="text"
          value={playerName}
          onChange={(e) => setPlayerName(e.target.value)}
          className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter your name"
          />
        </div>

        <div className="text-center">
          <button
          onClick={connectWebSocket}
          disabled={connectionStatus === 'connecting'}
          className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
          >
          {connectionStatus === 'connecting' ? 'Connecting...' : 'Connect to Game'}
          </button>
        </div>

        <div className="text-center text-sm text-gray-600">
          Status: <span className={`font-semibold ${
          connectionStatus === 'error' ? 'text-red-600' : 'text-gray-800'
          }`}>
          {connectionStatus}
          </span>
        </div>
        </div>
      </div>
      </div>
    );
  }

  // Room selection screen
  if (!gameState) {
    return (
      <div
      className="min-h-screen bg-green-800 bg-opacity-80 bg-blend-overlay flex items-center justify-center"
      style={{
        backgroundImage: `url('/26701996-53f2-4859-9476-4cd2561529b2.jpg')`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        height: '500px',
      }}
      >
        <div className="bg-white rounded-lg p-8 shadow-2xl max-w-md w-full">
          <h1 className="text-2xl font-bold text-center mb-6">Join or Create Room</h1>
          
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-2">Player Name</label>
              <input
                type="text"
                value={playerName}
                onChange={(e) => setPlayerName(e.target.value)}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Your name"
              />
            </div>

            <button
              onClick={createRoom}
              className="w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center justify-center gap-2"
            >
              <Users className="w-4 h-4" />
              Create New Room
            </button>

            <div className="text-center text-gray-500">or</div>

            <div>
              <label className="block text-sm font-medium mb-2">Room ID</label>
              <input
                type="text"
                value={roomId}
                onChange={(e) => setRoomId(e.target.value)}
                className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter room ID"
              />
            </div>

            <button
              onClick={joinRoom}
              className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Join Room
            </button>
          </div>

          {messages.length > 0 && (
            <div className="mt-6 p-3 bg-gray-100 rounded-lg max-h-32 overflow-y-auto">
              {messages.map((msg, idx) => (
                <div key={idx} className={`text-sm ${
                  msg.type === 'error' ? 'text-red-600' : 'text-gray-700'
                }`}>
                  {msg.text}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  }

  // Main game screen
  const isMyTurn = gameState.currentPlayerId === currentPlayerId;
  const currentPlayer = gameState.players[currentPlayerId];
  const isHost = gameState.hostId === currentPlayerId;

  return (
    <div
      className="min-h-screen bg-green-800 bg-opacity-80 bg-blend-overlay flex items-center justify-center"
      style={{
        backgroundImage: `url('/9999520.jpg')`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        height: '500px',
      }}
      >
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="bg-white rounded-lg p-4 mb-4 shadow-lg">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold">Blackjack - Room: {gameState.roomId}</h1>
              <p className="text-gray-600">Phase: {gameState.currentPhase}</p>
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-600">Players: {Object.keys(gameState.players).length}/6</p>
              {isHost && (
                <span className="bg-yellow-100 text-yellow-800 px-2 py-1 rounded text-xs">Host</span>
              )}
            </div>
          </div>
        </div>

        {/* Dealer */}
        <div className="bg-white rounded-lg p-4 mb-4 shadow-lg">
          <h2 className="text-xl font-bold mb-3">Dealer</h2>
          <div className="flex gap-1 mb-2">
            {gameState.dealerCards?.map((card, idx) => renderCard(card, false))}
          </div>
          <div className="text-sm">
            Hand Value: <span className="font-bold">
              {gameState.dealerSecondCardHidden ? '?' : gameState.dealerValue}
            </span>
          </div>
        </div>

        {/* Players */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
          {Object.entries(gameState.players).map(([playerId, player]) => 
            renderPlayer(playerId, player)
          )}
        </div>

        {/* Game Controls */}
        <div className="bg-white rounded-lg p-4 shadow-lg">
          <div className="flex flex-wrap gap-3">
            
            {/* Waiting Phase */}
            {gameState.currentPhase === 'WAITING' && isHost && (
              <button
                onClick={startGame}
                className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center gap-2"
              >
                <Play className="w-4 h-4" />
                Start Game
              </button>
            )}

            {/* Betting Phase */}
            {gameState.currentPhase === 'BETTING' && currentPlayer && currentPlayer.bet === 0 && (
              <>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setBetAmount(Math.max(10, betAmount - 10))}
                    className="bg-red-500 text-white p-1 rounded hover:bg-red-600"
                  >
                    <Minus className="w-4 h-4" />
                  </button>
                  <span className="px-3 py-1 bg-gray-100 rounded">${betAmount}</span>
                  <button
                    onClick={() => setBetAmount(Math.min(currentPlayer.balance, betAmount + 10))}
                    className="bg-green-500 text-white p-1 rounded hover:bg-green-600"
                  >
                    <Plus className="w-4 h-4" />
                  </button>
                </div>
                <button
                  onClick={placeBet}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
                >
                  <DollarSign className="w-4 h-4" />
                  Place Bet
                </button>
              </>
            )}

            {/* Player Turn */}
            {gameState.currentPhase === 'PLAYER_TURNS' && isMyTurn && currentPlayer && !currentPlayer.hasActed && (
              <>
                <button
                  onClick={playerHit}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                >
                  Hit
                </button>
                <button
                  onClick={playerStand}
                  className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors"
                >
                  Stand
                </button>
                {currentPlayer.cardCount === 2 && (
                  <button
                    onClick={playerDoubleDown}
                    className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 transition-colors"
                  >
                    Double Down
                  </button>
                )}
              </>
            )}

            {/* Showdown Phase */}
            {gameState.currentPhase === 'SHOWDOWN' && isHost && (
              <button
                onClick={startNewRound}
                className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center gap-2"
              >
                <RotateCcw className="w-4 h-4" />
                New Round
              </button>
            )}
          </div>

          {/* Turn indicator */}
          {gameState.currentPhase === 'PLAYER_TURNS' && (
            <div className="mt-3 text-sm">
              {isMyTurn ? (
                <span className="text-green-600 font-bold">Your turn!</span>
              ) : (
                <span className="text-gray-600">
                  Waiting for {gameState.players[gameState.currentPlayerId]?.name || 'player'}...
                </span>
              )}
            </div>
          )}
        </div>

        {/* Messages */}
        {messages.length > 0 && (
          <div className="bg-white rounded-lg p-4 mt-4 shadow-lg">
            <h3 className="font-bold mb-2">Game Messages</h3>
            <div className="max-h-32 overflow-y-auto space-y-1">
              {messages.map((msg, idx) => (
                <div key={idx} className={`text-sm ${
                  msg.type === 'error' ? 'text-red-600' : 'text-gray-700'
                }`}>
                  {msg.text}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default BlackjackGame;