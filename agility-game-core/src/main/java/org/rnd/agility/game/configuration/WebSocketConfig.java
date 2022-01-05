package org.rnd.agility.game.configuration;

import lombok.RequiredArgsConstructor;
import org.rnd.agility.game.domain.game.GameRoomManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;

import java.net.URI;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig extends ReactorNettyRequestUpgradeStrategy {

    private final GameRoomManager manager;

    @Override
    public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler handler,
                              @Nullable String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {

    //Reject upgrade with "Bad Request" if param key "id" does not exist

        var roomId = exchange.getRequest().getQueryParams().get("id").get(0);
        if(!roomId.isEmpty() &&!manager.gameExists(roomId))
                manager.createGame(roomId);

        ServerHttpResponse response = exchange.getResponse();
        HttpServerResponse reactorResponse = ServerHttpResponseDecorator.getNativeResponse(response);
        HandshakeInfo handshakeInfo = handshakeInfoFactory.get();
        NettyDataBufferFactory bufferFactory = (NettyDataBufferFactory) response.bufferFactory();
        URI uri = exchange.getRequest().getURI();

        // Trigger WebFlux preCommit actions and upgrade
//        return response.setComplete()
//                .then(Mono.defer(() ->
//                        reactorResponse.sendWebsocket((in, out) -> {
//                            var session = new ReactorNettyWebSocketSession(in, out, handshakeInfo, bufferFactory);
//                            return handler.handle(session).checkpoint(uri + " [ReactorNettyRequestUpgradeStrategy]");
//                        }
//                )));

        return Mono.just(roomId)
                .filter(String::isEmpty)
                .then(Mono.error(new RuntimeException("Parameter for key 'id' not found")).then())
                .switchIfEmpty(Mono.defer(()->
                        reactorResponse.sendWebsocket((in, out)-> {
                            var session = new ReactorNettyWebSocketSession(in, out, handshakeInfo, bufferFactory);
                            return handler.handle(session).checkpoint(uri + " [ReactorNettyRequestUpgradeStrategy]");
                        }
                ))).switchIfEmpty(Mono.error(new RuntimeException("Parameter not found")).then());

    }

}
