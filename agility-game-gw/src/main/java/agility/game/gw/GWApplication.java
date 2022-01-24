package agility.game.gw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GWApplication {
    public static void main(String[] args) {
        SpringApplication.run(GWApplication.class);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(p -> p.path("/games").and().method(HttpMethod.GET)
                        .uri("http://localhost:8079/games")
                ).route(p -> p.path("/game/status").and().method(HttpMethod.GET)
                        .uri("http://localhost:8079/game/status")
                ).route(p -> p.path("/game")
                        .uri("ws://localhost:8079/game")
                ).build();
    }
}
