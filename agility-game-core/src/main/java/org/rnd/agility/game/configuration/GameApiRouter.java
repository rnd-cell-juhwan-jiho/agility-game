package org.rnd.agility.game.configuration;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.handler.GameApiHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@RequiredArgsConstructor
public class GameApiRouter {

    @Bean
    RouterFunction<ServerResponse> route(GameApiHandler apiHandler){
        return RouterFunctions.route()
                .path("/agility-game-core", builder -> builder
                        .GET("/games", accept(MediaType.APPLICATION_JSON), apiHandler::getGames)
                        .GET("/game/status", accept(MediaType.APPLICATION_JSON), apiHandler::getGameStatus)
                ).build();
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler handler){

        Map<String, WebSocketHandler> mapping = new HashMap<>();
        mapping.put("/agility-game-core/game", handler);

        return new SimpleUrlHandlerMapping(mapping, -1);
    }
}
