package org.rnd.agility.game.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var req = exchange.getRequest();
        var method = req.getMethodValue();
        var reqUri = req.getURI().toASCIIString();
        var remote = req.getRemoteAddress() != null ? req.getRemoteAddress().toString() : null;
        var reqContentType = req.getHeaders().getContentType();
        var reqContentTypeMain = reqContentType != null ? reqContentType.getType() : null;
        var reqContentTypeSub = reqContentType != null ? reqContentType.getSubtype() : null;

        var resp = exchange.getResponse();

        log.info("[REQ] remote={} method={} uri={} content-type={}/{}",
                remote, method, reqUri, reqContentTypeMain, reqContentTypeSub);
        resp.beforeCommit(()->{
            log.info("[RES] remote={} status={} uri={}", remote, exchange.getResponse().getRawStatusCode(), reqUri);
            return Mono.empty();
        });

        return chain.filter(exchange);
    }

}
