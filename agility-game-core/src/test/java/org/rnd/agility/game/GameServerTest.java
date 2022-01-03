package org.rnd.agility.game;

import org.junit.jupiter.api.Test;
import org.rnd.agility.game.domain.dto.GameBid;

import static org.junit.jupiter.api.Assertions.*;

class GameServerTest {

    @Test
    public void play(){
        GameBid bid = new GameBid();
        assertNull(bid.getType());
    }
}