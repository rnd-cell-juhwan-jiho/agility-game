package org.rnd.agility.game.domain.join.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import org.rnd.agility.game.domain.game.Game;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameStatus {
    private String gameId;
    private String status;
    private Integer size;

    public GameStatus(Game game){
        this.gameId = game.getGameId();
        this.status = game.getStatus().get().getValue();
        this.size = game.getUsers().size();
    }
}
