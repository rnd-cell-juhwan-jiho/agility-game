package org.rnd.agility.game.domain.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.rnd.agility.game.domain.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Setter
@Getter
public class Game {

    private String host;
    private String gameId;  //actual request parameter from client
    private ObjectMapper mapper;
    private final Set<String> users = ConcurrentHashMap.newKeySet();
    private final Set<String> usersReady = ConcurrentHashMap.newKeySet();
    private final AtomicReference<GameBid> lastBid = new AtomicReference<>(new GameBid());
    private final AtomicLong lastBidTime = new AtomicLong(0);
    private final Vector<String> losers = new Vector<>();

    private final AtomicReference<GameStatus> status = new AtomicReference<>(GameStatus.VOTING);

    private final Sinks.Many<String> channel = Sinks.many().multicast().onBackpressureBuffer();

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

        if (this.status.get() == GameStatus.VOTING) {
            this.users.add(userIn.getUsername());

            userIn.setUsers(this.users);
            userIn.setUsersReady(this.usersReady);

            String outbound = mapperWriteAsString(userIn);
            this.channel.tryEmitNext(outbound);
        }
    }

    private void handleUserOut(String inbound) throws JsonProcessingException {
        UserEntrance userOut = mapperRead(inbound, UserEntrance.class);

        this.users.remove(userOut.getUsername());
        this.usersReady.remove(userOut.getUsername());
        userOut.setUsers(this.users);
        userOut.setUsersReady(this.usersReady);

        String outbound = mapperWriteAsString(userOut);
        this.channel.tryEmitNext(outbound);
    }

    private void handleBid(String inbound) throws JsonProcessingException {
        GameBid newBid = this.mapper.readValue(inbound, GameBid.class);
        if (this.isRunning()) {
            //if this is the first loser
            if (newBid.getBid().equals(this.lastBid.get().getBid())) {
                this.status.set(GameStatus.ENDING);
                this.losers.add(this.lastBid.get().getUsername());
                this.losers.add(newBid.getUsername());

                Flux.just(0, 1, 2).zipWith(Flux.interval(Duration.ofSeconds(1)))
                        .subscribe(
                                (tuple2) -> { },
                                Throwable::printStackTrace,
                                () -> {
                                    this.status.set(GameStatus.TERMINATING);
                                    this.channel.tryEmitNext(mapperWriteAsString(new GameEnding(DtoType.END, true, new ArrayList<>(this.losers))));
                                }
                        );

                String outbound = mapperWriteAsString(new GameEnding(DtoType.END, false, new ArrayList<>(this.losers)));
                this.channel.tryEmitNext(outbound);
            } else {
                this.lastBid.set(newBid);
                this.channel.tryEmitNext(inbound);
            }
        }
        //if 2 seconds countdown is ongoing,
        //add to losers and notify others
        else if (this.isEnding()) {
            this.losers.add(newBid.getUsername());

            String outbound = mapperWriteAsString(new GameEnding(DtoType.END, false, new ArrayList<>(this.losers)));
            this.channel.tryEmitNext(outbound);
        }
    }

    private void handleReady(String inbound) throws JsonProcessingException {
        UserReady userReady = mapperRead(inbound, UserReady.class);
        if (this.isVoting() || this.isCountingDown()) {
            if (userReady.getReady() && isCountdownAfterReady(userReady.getUsername())) {
                this.status.set(GameStatus.COUNTDOWN);
                Flux.range(0, 10).map(n -> 10 - n)
                        .doOnNext(n -> channel.tryEmitNext(mapperWriteAsString(new GameCountdown(DtoType.COUNTDOWN, n))))
                        .doOnComplete(() -> this.status.compareAndSet(GameStatus.COUNTDOWN, GameStatus.RUNNING))
                        .subscribe();
            } else if (!userReady.getReady()) {
                this.usersReady.remove(userReady.getUsername());
            }

            channel.tryEmitNext(inbound);
        }
    }

    private String mapperWriteAsString(Object obj) {
        try {
            return this.mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    private <T> T mapperRead(String json, Class<T> cls) throws JsonProcessingException {
        return this.mapper.readValue(json, cls);
    }

    public void cancelGame() {
        this.status.compareAndSet(GameStatus.COUNTDOWN, GameStatus.VOTING);
    }

    public boolean isCountdownAfterReady(String username) {
        this.usersReady.add(username);
        int readyCnt = usersReady.size();
        int p = users.size();
        return readyCnt > p / 2 && p >= 3;
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
        VOTING, COUNTDOWN, RUNNING, ENDING, TERMINATING
    }
}
