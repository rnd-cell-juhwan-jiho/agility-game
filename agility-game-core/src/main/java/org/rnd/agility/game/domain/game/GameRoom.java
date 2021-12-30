package org.rnd.agility.game.domain.game;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
public class GameRoom {

    private String host;
    private String roomId;  //actual request parameter from client
    private final ConcurrentMap<String, String> players = new ConcurrentHashMap<>();
    private final AtomicInteger readyCounter = new AtomicInteger(0);
    private final AtomicInteger gameCounter = new AtomicInteger(1);
    private final AtomicLong lastBidTime = new AtomicLong(0);
    private final AtomicBoolean isEnding = new AtomicBoolean(false);
    private final List<String> losers = new ArrayList<>();

    private final Sinks.Many<String> channel = Sinks.many().multicast().onBackpressureBuffer();

    public int userIn(String username){
        players.put(username, "");
        return players.size();
    }

    public boolean readyAndDecideStart(){
         int readyCnt = readyCounter.incrementAndGet();
         int p = players.size();
         if(readyCnt > p/2 && p > 2)
             return true;
         else
             return false;
    }

    public int bid(int n){
        if(n == gameCounter.get())
            return 0;
        else
            return gameCounter.incrementAndGet();
    }
}
