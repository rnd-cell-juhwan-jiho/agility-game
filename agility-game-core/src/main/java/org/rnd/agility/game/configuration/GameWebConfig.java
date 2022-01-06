package org.rnd.agility.game.configuration;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.game.GameRoomManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class GameWebConfig implements WebFluxConfigurer{

    private final GameRoomManager gameRoomManager;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("POST");
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler handler){

        Map<String, WebSocketHandler> mapping = new HashMap<>();
        mapping.put("/game", handler);

        return new SimpleUrlHandlerMapping(mapping, -1);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter(WebSocketService webSocketService) {
        return new WebSocketHandlerAdapter(webSocketService);
    }

    @Bean
    public WebSocketService webSocketService(RequestUpgradeStrategy upgradeStrategy){
        return new GameHandshakeWebSocketService(upgradeStrategy, gameRoomManager);
    }

    @Bean
    public RequestUpgradeStrategy requestUpgradeStrategy(){
        return new ReactorNettyRequestUpgradeStrategy();
    }
}
