package org.rnd.agility.game.domain.game;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Getter
public class GameRoomManager {

    private final ConcurrentMap<String, GameRoom> games = new ConcurrentHashMap<>();

    public GameRoom getGame(String roomId){
        return games.get(roomId);
    }

    public void createGame(String roomId){
        var game = new GameRoom();
        games.put(roomId, game);
    }

    public void removeGame(String host){
        games.remove(host);
    }
}
