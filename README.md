# Multiplayer Blackjack

A real-time multiplayer Blackjack application built with Java Spring Boot and React, featuring WebSocket communication for synchronized gameplay.

## Overview

This project implements a complete multiplayer Blackjack game consisting of:

- **Server**: Java Spring Boot backend with WebSocket support
- **Client**: React-based frontend application
- **Game Logic**: Modular classes for game mechanics and state management
- **Communication**: WebSocket integration for real-time client-server interaction

## Features

- Multiplayer gameplay with real-time synchronization
- WebSocket-based communication
- Modular game logic architecture
- Data transfer objects for efficient messaging
- React frontend with responsive design

## Installation

### Prerequisites

- Java 17 or higher
- Node.js 18 or higher with npm

### Server Setup

Navigate to the server directory:
```bash
cd blackJackServer
```

Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

### Client Setup

Navigate to the client directory:
```bash
cd blackjackclient
```

Install dependencies:
```bash
npm install
```

Start the development server:
```bash
npm start
```

Access the application at `http://localhost:3000`

## Usage

Start both the server and client applications to begin playing multiplayer Blackjack. The WebSocket connection handles real-time game state updates between all connected players.