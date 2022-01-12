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
public class GameManager {

    private final ConcurrentMap<String, Game> games = new ConcurrentHashMap<>();    //roomId:game
    private final ObjectMapper mapper;

    public Game getGame(String roomId){
        return games.get(roomId);
    }

    public void createGame(String roomId){
        var game = new Game();
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
