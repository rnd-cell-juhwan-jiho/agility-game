package org.rnd.agility.game.configuration;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.handler.GameApiHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@RequiredArgsConstructor
public class GameApiRouter {

    private final

    @Bean
    RouterFunction<ServerResponse> route(GameApiHandler apiHandler){
        return RouterFunctions.route()
                .GET("/games", accept(MediaType.APPLICATION_JSON), apiHandler::games)
                .build();
    }
}
