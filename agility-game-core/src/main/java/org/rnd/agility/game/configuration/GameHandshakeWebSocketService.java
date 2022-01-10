package org.rnd.agility.game.configuration;

import org.rnd.agility.game.domain.game.GameManager;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class GameHandshakeWebSocketService extends HandshakeWebSocketService {

    private final GameManager gameManager;

    public GameHandshakeWebSocketService(RequestUpgradeStrategy upgradeStrategy, GameManager gm){
        super(upgradeStrategy);
        this.gameManager = gm;
    }

    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {

        ServerHttpRequest request = exchange.getRequest();

        //if opening handshake request is invalid
        if(!request.getQueryParams().containsKey("id"))
            return Mono.error(new IllegalArgumentException("Key 'id' not found in query parameters"));

        var gameId = request.getQueryParams().get("id").get(0);

        //if game does not exist, create new game
        if(!gameManager.gameExists(gameId))
            gameManager.createGame(gameId);

        return super.handleRequest(exchange, handler);
    }
}
