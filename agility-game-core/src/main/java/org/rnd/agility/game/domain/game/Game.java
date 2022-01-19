package org.rnd.agility.game.domain.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.rnd.agility.game.domain.game.dto.*;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Setter
@Getter
public class Game {

    private String host;
    private String gameId;  //actual request parameter from client
    private ObjectMapper mapper;
    private final ConcurrentMap<String, Boolean> users = new ConcurrentHashMap<>();
    private final AtomicInteger readyCnt = new AtomicInteger(0);
    private final AtomicReference<GameBid> lastBid = new AtomicReference<>(new GameBid());
    private final AtomicLong lastBidTime = new AtomicLong(0);
    private final Set<String> losers = ConcurrentHashMap.newKeySet();   //concurrent set

    private final AtomicReference<GameStatus> status = new AtomicReference<>(GameStatus.VOTING);

    private final Sinks.Many<String> channel = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<Integer> countdownFlux = Flux.just(10).concatWith(
            Flux.range(1, 10).map(n -> 10 - n)
                    .delayElements(Duration.ofSeconds(1))
    );
    private final AtomicReference<Disposable> countdownSubs = new AtomicReference<>(null);

    public void processMessage(String type, String msg) throws JsonProcessingException {
        switch (type) {
            case DtoType.USER_IN:
                handleUserIn(msg);
                break;
            case DtoType.USER_OUT:
                handleUserOut(msg);
                break;
            case DtoType.BID:
                handleBid(msg);
                break;
            case DtoType.READY:
                handleReady(msg);
                break;
        }
    }

    private void handleUserIn(String inbound) throws JsonProcessingException {
        UserEntrance userIn = mapperRead(inbound, UserEntrance.class);
        if (this.isVoting()) {
            this.users.put(userIn.getUsername(), false);

            this.channel.tryEmitNext(inbound);
        } else
            this.channel.tryEmitNext(this.mapperWriteAsString(new Reject(DtoType.REJECT)));
    }

    private void handleUserOut(String inbound) throws JsonProcessingException {
        UserEntrance userOut = mapperRead(inbound, UserEntrance.class);
        var wasReady = this.users.get(userOut.getUsername());
        this.users.remove(userOut.getUsername());

        if (wasReady) {
            var newReadyCnt = this.readyCnt.decrementAndGet();
            if (this.isCountingDown() && newReadyCnt < users.size() / 2)
                this.cancelCountdown();
        } else if (this.isRunning() && this.users.size() < 3) {
            this.cancelRunning(userOut.getUsername());
        }

        this.channel.tryEmitNext(inbound);
    }

    private void handleBid(String inbound) throws JsonProcessingException {
        GameBid newBid = this.mapper.readValue(inbound, GameBid.class);
        if (this.isRunning()) {
            //if this is the first loser
            if (newBid.getBid() == this.lastBid.get().getBid()) {
                this.status.compareAndSet(GameStatus.RUNNING, GameStatus.ENDING);
                this.losers.add(this.lastBid.get().getUsername());
                this.losers.add(newBid.getUsername());

                Mono.just(0).delayElement(Duration.ofSeconds(1))
                        .subscribe(
                                __ -> {
                                },
                                Throwable::printStackTrace,
                                this::terminate
                        );

                String outbound = mapperWriteAsString(new GameEnding(DtoType.END, false, new ArrayList<>(this.losers)));
                this.channel.tryEmitNext(outbound);
            } else {
                this.lastBid.set(newBid);

                this.channel.tryEmitNext(inbound);
            }
        }
        //if 2 seconds countdown is ongoing,
        else if (this.isEnding()) {
            //add to `losers` and notify others
            if (newBid.getBid() == this.lastBid.get().getBid())
                this.losers.add(newBid.getUsername());

            String outbound = mapperWriteAsString(new GameEnding(DtoType.END, false, new ArrayList<>(this.losers)));
            this.channel.tryEmitNext(outbound);
        } else
            this.channel.tryEmitNext(this.mapperWriteAsString(new Reject(DtoType.REJECT)));
    }

    private void handleReady(String inbound) throws JsonProcessingException {
        UserReady userReady = mapperRead(inbound, UserReady.class);
        var willCountdown = isCountdownAfterReady(userReady);
        if (this.isVoting()) {
            if (willCountdown)
                this.startCountdown();
            this.channel.tryEmitNext(inbound);
        } else if (this.isCountingDown()) {
            if (!willCountdown)
                this.cancelCountdown();
            this.channel.tryEmitNext(inbound);
        } else
            this.channel.tryEmitNext(this.mapperWriteAsString(new Reject(DtoType.REJECT)));
    }

    private String mapperWriteAsString(Object obj) {
        try {
            assert this.mapper != null;
            return this.mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    private <T> T mapperRead(String json, Class<T> cls) throws JsonProcessingException {
        return this.mapper.readValue(json, cls);
    }

    public void startCountdown() {
        this.getStatus().compareAndSet(GameStatus.VOTING, GameStatus.COUNTDOWN);
        var subscription = this.countdownFlux
                .doOnCancel(() -> {
                    this.status.compareAndSet(GameStatus.COUNTDOWN, GameStatus.VOTING);
                    String outbound = mapperWriteAsString(new GameCountdown(DtoType.COUNTDOWN, -1, true));
                    channel.tryEmitNext(outbound);
                }).subscribe(
                        n -> channel.tryEmitNext(mapperWriteAsString(new GameCountdown(DtoType.COUNTDOWN, n, false))),
                        Throwable::printStackTrace,
                        () -> this.status.compareAndSet(GameStatus.COUNTDOWN, GameStatus.RUNNING)
                );
        this.countdownSubs.set(subscription);
    }

    public void cancelCountdown() {
        this.countdownSubs.get().dispose();
    }

    //triggered ONLY by USER_OUT
    private void cancelRunning(String username) {
        this.status.compareAndSet(GameStatus.RUNNING, GameStatus.VOTING);
        this.readyCnt.set(0);
        for (var entry : this.users.entrySet())
            entry.setValue(false);
        this.lastBidTime.set(0);
        this.lastBid.set(new GameBid());
        String outbound = mapperWriteAsString(new UserEntrance(DtoType.USER_OUT, username, true));
        this.channel.tryEmitNext(outbound);
    }

    private void terminate() {
        this.status.compareAndSet(GameStatus.ENDING, GameStatus.TERMINATING);
        this.channel.tryEmitNext(mapperWriteAsString(new GameEnding(DtoType.END, true, new ArrayList<>(this.losers))));
    }

    public boolean isCountdownAfterReady(UserReady userReady) {
        var username = userReady.getUsername();
        var ready = userReady.getReady();
        //discard duplicate READY
        if (this.users.get(username) == ready)
            return false;
        this.users.put(username, ready);
        int usz = this.users.size(), readyCnt;
        if (ready)
            readyCnt = this.readyCnt.incrementAndGet();
        else
            readyCnt = this.readyCnt.decrementAndGet();
        return readyCnt > usz / 2 && usz >= 3;
    }

    public boolean isVoting() {
        return this.status.get() == GameStatus.VOTING;
    }

    public boolean isCountingDown() {
        return this.status.get() == GameStatus.COUNTDOWN;
    }

    public boolean isRunning() {
        return this.status.get() == GameStatus.RUNNING;
    }

    public boolean isEnding() {
        return this.status.get() == GameStatus.ENDING;
    }

    public boolean isTerminating() {
        return this.status.get() == GameStatus.TERMINATING;
    }

    public enum GameStatus {
        VOTING("VOTING"),
        COUNTDOWN("COUNTDOWN"),
        RUNNING("RUNNING"),
        ENDING("ENDING"),
        TERMINATING("TERMINATING");

        private final String value;

        GameStatus(String s) {
            this.value = s;
        }

        public String getValue() {
            return this.value;
        }
    }
}
