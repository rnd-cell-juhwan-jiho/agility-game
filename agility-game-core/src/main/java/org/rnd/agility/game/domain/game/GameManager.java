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

    private final ConcurrentMap<String, Game> games = new ConcurrentHashMap<>();    //gameId:game
    private final ObjectMapper mapper;

    public Game getGame(String gameId){
        return games.get(gameId);
    }

    public void createGame(String gameId){
        var game = new Game();
        game.setMapper(this.mapper);
        game.setGameId(gameId);
        games.put(gameId, game);
    }

    public boolean gameExists(String gameId){
        return games.containsKey(gameId);
    }

    public void removeGame(String gameId){
        games.remove(gameId);
    }
}
