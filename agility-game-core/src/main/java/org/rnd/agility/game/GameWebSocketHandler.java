package org.rnd.agility.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentMap;

/*

 */
@Component
@RequiredArgsConstructor
public class GameWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper mapper;
    private final ConcurrentMap<String, Sinks.Many<String>> roomSinks;  //host:room

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        var roomSink = roomSinks.get("");

        session.receive()
                .doOnNext(wsm -> {
                    var msg = wsm.getPayloadAsText();
                    try {
                        String type = mapper.reader().readTree(msg).get("type").asText();

                        switch(type){
                            case "USER_IN":
                            case "USER_OUT":
                            case "GAME_BID":
                            case "GAME_READY":

                        }

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }).subscribe();

        return session.send(
                roomSink.asFlux().map(session::textMessage)
        );
    }
}
