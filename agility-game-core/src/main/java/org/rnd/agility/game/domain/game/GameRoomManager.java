package org.rnd.agility.game.domain.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Getter
@RequiredArgsConstructor
public class GameRoomManager {

    private final ConcurrentMap<String, GameRoom> games = new ConcurrentHashMap<>();    //roomId:game
    private final ObjectMapper mapper;

    public GameRoom getGame(String roomId){
        return games.get(roomId);
    }

    public void createGame(String roomId){
        var game = new GameRoom();
        game.setMapper(this.mapper);
        games.put(roomId, game);
    }

    public boolean gameExists(String roomId){
        return games.containsKey(roomId);
    }

    public void removeGame(String roomId){
        games.remove(roomId);
    }
}
