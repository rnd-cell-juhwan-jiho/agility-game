package org.rnd.agility.game;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.dto.DtoType;
import org.rnd.agility.game.domain.dto.JoinRequest;
import org.rnd.agility.game.domain.dto.ServerMessage;
import org.rnd.agility.game.domain.game.GameRoomManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GameApiHandler {

    private final GameRoomManager gameManager;

    public Mono<ServerResponse> join(ServerRequest request){
        if(request.queryParam("id").isEmpty())
            throw new RuntimeException("unknown query parameter");

        var roomId = request.queryParam("id").get();
        if(gameManager.getGame(roomId) == null)
            gameManager.createGame(roomId);

        return request.bodyToMono(JoinRequest.class)
                .flatMap(jr ->
                        ServerResponse.ok().bodyValue(new ServerMessage(DtoType.IN_OK, "welcome"))
                );
    }
}
