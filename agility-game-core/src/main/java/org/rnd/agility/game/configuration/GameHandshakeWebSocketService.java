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
        if(!request.getQueryParams().containsKey("game-id"))
            return Mono.error(new IllegalArgumentException("Key 'id' not found in query parameters"));
        else if(!request.getQueryParams().containsKey("username"))
            return Mono.error(new IllegalArgumentException("key 'username' not found in query parameters"));

        var gameId = request.getQueryParams().get("game-id").get(0);
        var username = request.getQueryParams().get("username").get(0);

        //if game does not exist, create new game
        if(!gameManager.gameExists(gameId))
            gameManager.createGame(gameId);
        else{
            if(!gameManager.getGame(gameId).isVoting())
                return Mono.error(new IllegalAccessException("Unable to join this game right now."));
        }

        return super.handleRequest(exchange, handler);
    }
}
