package com.wefly.wefly.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Definimos el endpoint dinámico. Flutter se conectará a: ws://tu-ip:8080/chat/{idAnuncio}
        registry.addHandler(chatWebSocketHandler, "/chat/{announcementId}")
                .setAllowedOrigins("*"); // Permite conexiones desde el emulador o móviles físicos
    }
}
