package org.rnd.agility.game.domain.game;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AgilityGameManager {

    private ConcurrentMap<String, AgilityGame> games = new ConcurrentHashMap<>();

    public void addGame(){
        var game = new AgilityGame();
    }
}
