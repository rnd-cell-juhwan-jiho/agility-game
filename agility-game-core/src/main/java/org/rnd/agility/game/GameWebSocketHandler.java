package org.rnd.agility.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.dto.CountDown;
import org.rnd.agility.game.domain.dto.DtoType;
import org.rnd.agility.game.domain.game.GameRoomManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*

 */
@Component
@RequiredArgsConstructor
public class GameWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper mapper;
    private final GameRoomManager gameManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        String roomId = "";

        var game = gameManager.getGame(roomId);

        session.receive()
                .doOnNext(wsm -> {
                    var msg = wsm.getPayloadAsText();
                    String type = "";
                    try {
                        type = mapper.reader().readTree(msg).get("type").asText();
                        game.processMessage(type, msg);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }).subscribe();

        return session.send(
                game.getChannel().asFlux().map(session::textMessage)
        );
    }

    private String mapperWrite(Object message) {
        try{
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
}
