package org.rnd.agility.game.handler;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.game.GameManager;
import org.rnd.agility.game.domain.join.dto.Games;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class GameApiHandler {

    private final GameManager gameManager;

    public Mono<ServerResponse> games(ServerRequest request){

        Set<String> games = gameManager.getGames().keySet();

        return ServerResponse.ok().body(new Games(games), Games.class);
    }
}
