package com.antonkuzmichev.blackjack_server.config;

import com.antonkuzmichev.blackjack_server.websocket.GameWebSocketHandler;

import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameWebSocketHandler(), "/game").setAllowedOrigins("*"); // Allow all origins (for development)
    }
}

