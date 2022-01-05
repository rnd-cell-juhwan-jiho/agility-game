package org.rnd.agility.game.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HttpConfig implements WebFluxConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("POST");
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler handler){

        Map<String, WebSocketHandler> mapping = new HashMap<>();
        mapping.put("/game", handler);

        return new SimpleUrlHandlerMapping(mapping, -1);
    }

}
