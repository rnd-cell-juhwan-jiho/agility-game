package org.rnd.agility.game.configuration;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.game.GameManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class GameWebConfig implements WebFluxConfigurer{

    private final GameManager gameManager;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET");
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("GET");
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter(WebSocketService webSocketService) {
        return new WebSocketHandlerAdapter(webSocketService);
    }

    @Bean
    public WebSocketService webSocketService(RequestUpgradeStrategy upgradeStrategy){
        return new GameHandshakeWebSocketService(upgradeStrategy, gameManager);
    }

    @Bean
    public RequestUpgradeStrategy requestUpgradeStrategy(){
        return new ReactorNettyRequestUpgradeStrategy();
    }
}
