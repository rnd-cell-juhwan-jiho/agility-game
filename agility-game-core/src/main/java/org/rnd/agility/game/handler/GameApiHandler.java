package org.rnd.agility.game.handler;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.game.GameManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GameApiHandler {

    private final GameManager gameManager;

    public Mono<ServerResponse> game(ServerRequest request){
//        boolean isUrlValid = request.queryParam("id").isPresent();
//
//        var roomId = request.queryParam("id");
//        if(gameManager.getGame(roomId) == null)
//            gameManager.createGame(roomId);
//
//        return request.bodyToMono(JoinRequest.class)
//                .filter( _jr ->isUrlValid)
//                .flatMap(jr -> ServerResponse.ok().bodyValue("welcome"));
        return ServerResponse.notFound().build();
    }
}
