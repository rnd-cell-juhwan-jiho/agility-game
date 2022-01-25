package org.rnd.agility.game.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rnd.agility.game.domain.game.GameManager;
import org.rnd.agility.game.domain.game.dto.DtoType;
import org.rnd.agility.game.domain.game.dto.Init;
import org.rnd.agility.game.domain.game.dto.User;
import org.rnd.agility.game.domain.game.dto.UserEntrance;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*

 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper mapper;
    private final GameManager gameManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        //upon connection,
        //send INIT message containing list of users

        var queryMap = getQueryMap(session.getHandshakeInfo().getUri().getQuery());
        var gameId = queryMap.get("game-id");
        var username = queryMap.get("username");
        var game = this.gameManager.getGame(gameId);

        List<User> userList = game.getUsers().entrySet().stream()
                .map(entry -> new User(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        String initialMessage = mapperWriteAsString(new Init(DtoType.INIT, null, userList));
        Mono<String> initialMessageMono = Mono.just(initialMessage);

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
            //multicast USER_OUT
            String userOut = mapperWriteAsString(new UserEntrance(DtoType.USER_OUT, username, false));
            try {
                game.processMessage(DtoType.USER_OUT, userOut);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            //if last player closes connection
            if(game.getUsers().isEmpty()) {
                log.info("=== Terminating game [ {} ] ===", gameId);
                this.gameManager.removeGame(gameId);
            }
            //if RUNNING game should be canceled back to VOTING
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
                initialMessageMono
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
