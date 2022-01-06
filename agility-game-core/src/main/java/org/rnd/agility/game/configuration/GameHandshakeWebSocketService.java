package org.rnd.agility.game.configuration;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.game.GameRoomManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class GameHandshakeWebSocketService extends HandshakeWebSocketService {

    private final GameRoomManager gameManager;

    public GameHandshakeWebSocketService(RequestUpgradeStrategy upgradeStrategy, GameRoomManager grm){
        super(upgradeStrategy);
        this.gameManager = grm;
    }

    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {

        ServerHttpRequest request = exchange.getRequest();

        if(!request.getQueryParams().containsKey("id"))
            return Mono.error(new IllegalArgumentException("Key 'id' not found in query parameters"));

        var roomId = request.getQueryParams().get("id").get(0);
        if(!gameManager.gameExists(roomId))
            gameManager.createGame(roomId);

        return super.handleRequest(exchange, handler);
    }
}
