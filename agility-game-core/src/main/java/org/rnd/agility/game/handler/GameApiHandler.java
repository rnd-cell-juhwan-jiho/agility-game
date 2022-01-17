package org.rnd.agility.game.handler;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.game.GameManager;
import org.rnd.agility.game.domain.join.dto.GameThumbnail;
import org.rnd.agility.game.domain.join.dto.Games;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameApiHandler {

    private final GameManager gameManager;

    public Mono<ServerResponse> getGames(ServerRequest request) {

        Games games = new Games(
                gameManager.getGames().values().stream()
                        .map(GameThumbnail::new)
                        .collect(Collectors.toList())
        );
        return ServerResponse.ok().body(games, Games.class);
    }
}