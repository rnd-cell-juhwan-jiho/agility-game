package org.rnd.agility.game.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rnd.agility.game.domain.game.GameManager;
import org.rnd.agility.game.domain.game.dto.DtoType;
import org.rnd.agility.game.domain.game.dto.Init;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/*

 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper mapper;
    private final GameManager gameManager;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        var queryMap = getQueryMap(session.getHandshakeInfo().getUri().getQuery());
        var gameId = queryMap.get("id");
        var game = this.gameManager.getGame(gameId);

        HashMap<String, Boolean> userMap = new HashMap<>(game.getUsers());
        Mono<String> initialMessage = Mono.just(mapperWriteAsString(new Init(DtoType.INIT, userMap)));

        Consumer<WebSocketMessage> onNextConsumer = (wsm) -> {
            var msg = wsm.getPayloadAsText();
            log.info(msg);
            try {
                String type = this.mapper.reader().readTree(msg).get("type").asText();
                game.processMessage(type, msg);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };

        Runnable onCompleteConsumer = () -> {
            if(game.getUsers().isEmpty()) {
                //when last player exits
                log.info("=== Terminating game [ {} ] ===", gameId);
                this.gameManager.removeGame(gameId);
            }
            else if(game.isCountingDown() && game.getUsers().size() < 3){
                log.info("=== Cancelling game [ {} ] ===", gameId);
                game.cancelCountdown();
            }
        };

        session.receive()
                .subscribe(
                        onNextConsumer,
                        Throwable::printStackTrace,
                        onCompleteConsumer
                );

        return session.send(
                initialMessage
                        .concatWith(game.getChannel().asFlux())
                        .map(session::textMessage)
        );
    }

    private Map<String, String> getQueryMap(String rawQuery){
        var queryMap = new HashMap<String, String>();
        Arrays.stream(rawQuery.split("&")).forEach(str -> {
            int idx = str.indexOf("=");
            queryMap.put(str.substring(0, idx), str.substring(idx+1));
        });
        return queryMap;
    }

    private String mapperWriteAsString(Object obj) {
        try {
            assert this.mapper != null;
            return this.mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

}
