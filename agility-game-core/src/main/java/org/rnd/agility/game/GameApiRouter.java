package org.rnd.agility.game;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

public class GameApiRouter {
    @Bean
    RouterFunction<ServerResponse> route(GameApiHandler handler){
        return RouterFunctions.route()
                .POST("/join", accept(MediaType.APPLICATION_JSON),handler::join)
                .build();
    }
}
