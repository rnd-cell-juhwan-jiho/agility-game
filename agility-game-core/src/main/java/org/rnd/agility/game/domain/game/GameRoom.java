package org.rnd.agility.game.domain.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.rnd.agility.game.domain.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Setter
@Getter
public class GameRoom {

    private String host;
    private String roomId;  //actual request parameter from client
    private ObjectMapper mapper;
    private final ConcurrentMap<String, String> players = new ConcurrentHashMap<>();
    private final AtomicInteger readyCounter = new AtomicInteger(0);
    private final AtomicReference<GameBid> lastBid = new AtomicReference<>(new GameBid());
    private final AtomicLong lastBidTime = new AtomicLong(0);
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final AtomicBoolean isEnding = new AtomicBoolean(false);
    private final AtomicBoolean isEnded = new AtomicBoolean(false);
    private final Vector<String> losers = new Vector<>();

    private final Sinks.Many<String> channel = Sinks.many().multicast().onBackpressureBuffer();

    public int addUser(String username){
        players.put(username, "");
        return players.size();
    }

    public boolean readyAndCheckStart(){
         int readyCnt = readyCounter.incrementAndGet();
         int p = players.size();
         if(readyCnt > p/2 && p > 2)
             return true;
         else
             return false;
    }

    public void processMessage(String type, String msg) throws JsonProcessingException {
        switch (type){
            case DtoType.USER_IN:
                UserEntrance userIn = mapperRead(msg, UserEntrance.class);
                addUser(userIn.getUsername());
                userIn.setUserList(new ArrayList<>(this.players.keySet()));
                channel.tryEmitNext(mapperWriteAsString(userIn));
                break;
            case DtoType.USER_OUT:
                UserEntrance userOut = mapperRead(msg, UserEntrance.class);
                readyCounter.decrementAndGet();
                userOut.setUserList(new ArrayList<>(this.players.keySet()));
                channel.tryEmitNext(mapperWriteAsString(userOut));
                break;
            case DtoType.BID:
                //discard if isStarted.get() == false
                GameBid newBid = mapper.readValue(msg, GameBid.class);
                if(isStarted.get() && !isEnded.get()){
                    String outbound = "";
                    if(!isEnding.get()){
                    //if no losers were set,
                        if(newBid.getBid().equals(lastBid.get().getBid())){
                            //first losers set
                            isEnding.set(true);
                            losers.add(lastBid.get().getUsername());
                            losers.add(newBid.getUsername());

                            Flux.just(0, 1, 2).zipWith(Flux.interval(Duration.ofSeconds(1)))
                                    .doOnComplete(()->{
                                        isEnded.set(true);
                                        channel.tryEmitNext(mapperWriteAsString(new GameEnd(DtoType.END, true, new ArrayList<>(this.losers))));
                                    }).subscribe();

                            outbound = mapperWriteAsString(new GameEnd(DtoType.END, false, new ArrayList<>(this.losers)));
                        }
                        else{
                            lastBid.set(newBid);
                            channel.tryEmitNext(msg);
                        }
                    }else {
                    //if 2 seconds countdown is ongoing,
                        //add to losers and notify
                        losers.add(newBid.getUsername());
                        outbound = mapperWriteAsString(new GameEnd(DtoType.END, false, new ArrayList<>(this.losers)));
                    }
                    channel.tryEmitNext(outbound);
                }
                break;
            case DtoType.READY:
                if(readyAndCheckStart()){
                    Flux.range(1, 10).map(n -> 11 - n)
                            .doOnNext(n -> channel.tryEmitNext(mapperWriteAsString(new CountDown(DtoType.COUNTDOWN, n))))
                            .doOnComplete(() -> isStarted.compareAndSet(false, true))
                            .subscribe();
                }
        }
    }

    public String mapperWriteAsString(Object obj){
        try{
            return mapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return "ERROR";
    }

    public <T> T mapperRead(String json, Class<T> cls) throws JsonProcessingException {
        return mapper.readValue(json, cls);
    }
}
