package org.rnd.agility.game;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

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