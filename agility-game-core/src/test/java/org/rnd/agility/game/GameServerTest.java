package org.rnd.agility.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rnd.agility.game.domain.game.dto.Init;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

class GameServerTest {

    ObjectMapper mapper = new ObjectMapper();
    AtomicInteger n = new AtomicInteger(0);
    Flux<Integer> countdownFlux = Flux.range(0, 11).map(n -> 10-n)
            .delayElements(Duration.ofMillis(1000));

    @Test
    public void play() throws InterruptedException{
        var fluxRange = Flux.range(1, 5).delayElements(Duration.ofMillis(500)).share();

        fluxRange.subscribe(System.out::println);
        Thread.sleep(3000);
        fluxRange.subscribe(System.out::println);
        Thread.sleep(10000);
    }

    @Test
    public void play2() throws InterruptedException{

        int rep = 100000;
        Thread t1 = new Thread(()->{
            for(int i=0; i<rep; i++)
                n.incrementAndGet();
        });
        Thread t2 = new Thread(()->{
            for(int i=0; i<rep; i++)
                n.incrementAndGet();
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        Assertions.assertThat(n.get()).isEqualTo(2*rep);
        System.out.println("n: "+n);
    }

    @Test
    public void play3() throws InterruptedException {
        Disposable disposable = countdownFlux.doOnCancel(()-> System.out.println("cancelled..")).subscribe(
                n -> System.out.println(n+" "+Thread.currentThread()),
                e -> e.printStackTrace(),
                () -> {
                    System.out.println("onComplete()..");
                }
        );
        Thread.sleep(2000);
        disposable.dispose();
        countdownFlux.subscribe(n -> System.out.println(n+" "+Thread.currentThread()));
        Thread.sleep(12000);
    }

    @Test
    public void play4() throws InterruptedException {
        Flux<Integer> timeline = Flux.range(0, 11)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(n -> System.out.println("--"+n+"--"));
        timeline.subscribe();

        CompletableFuture<String> future = new CompletableFuture<>();

        Mono<String> mono1 = Mono.fromFuture(future).doOnNext(System.out::println);
        Mono<String> mono2 = Mono.just("b").delaySubscription(mono1).doOnNext(c -> System.out.println("[mono2] "+c));

        mono2.subscribe();

        Thread.sleep(2000);
        future.complete("a");

        Thread.sleep(10_000);
    }
}