package org.rnd.agility.game;

import org.junit.jupiter.api.Test;
import org.rnd.agility.game.domain.dto.GameBid;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class GameServerTest {

    @Test
    public void play() throws InterruptedException{
        var fluxRange = Flux.range(1, 5).delayElements(Duration.ofMillis(500)).share();

        fluxRange.subscribe(System.out::println);
        Thread.sleep(3000);
        fluxRange.subscribe(System.out::println);
        Thread.sleep(10000);
    }

    @Test
    public void play2(){
    }
}