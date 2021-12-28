package org.rnd.agility.game.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler handler){

        Map<String, WebSocketHandler> mapping = new HashMap<>();
        mapping.put("/game", handler);

        return new SimpleUrlHandlerMapping(mapping, -1);
    }
}
