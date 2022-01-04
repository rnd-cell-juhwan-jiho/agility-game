package org.rnd.agility.game.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameReady {
    String roomId;
    String type;
    String username;
    Boolean ready;
}
