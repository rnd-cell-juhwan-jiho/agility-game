package org.rnd.agility.game.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rnd.agility.game.domain.game.GameManager;
import org.rnd.agility.game.domain.join.dto.GameStatus;
import org.rnd.agility.game.domain.join.dto.Games;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameApiHandler {

    private final GameManager gameManager;

    public Mono<ServerResponse> getGames(ServerRequest request) {

        Games games = new Games(
                gameManager.getGames().values().stream()
                        .map(GameStatus::new)
                        .collect(Collectors.toList())
        );
        return ServerResponse.ok().bodyValue(games);
    }

    public Mono<ServerResponse> getGameStatus(ServerRequest request){

        var gameIdOptional = request.queryParam("game-id");
        if(gameIdOptional.isEmpty())
            return ServerResponse.badRequest().build();

        if(!gameManager.gameExists(gameIdOptional.get()))
            return ServerResponse.notFound().build();

        var gameStatus = new GameStatus(gameManager.getGame(gameIdOptional.get()));
        return ServerResponse.ok().bodyValue(gameStatus);
    }
}