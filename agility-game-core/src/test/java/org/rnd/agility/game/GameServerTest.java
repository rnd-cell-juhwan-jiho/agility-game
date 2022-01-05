package org.rnd.agility.game;

import org.junit.jupiter.api.Test;
import org.rnd.agility.game.domain.dto.GameBid;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GameServerTest {

    @Test
    public void play() throws InterruptedException{
        var mono = Mono.just("abc").doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNext("abc")
                .verifyComplete();

        Thread.sleep(1000);

        StepVerifier.create(mono)
                .expectNext("abc")
                .verifyComplete();
    }

    @Test
    public void play2(){
    }
}