package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCountdown {
    private String type = DtoType.COUNTDOWN;
    private Integer count;
}
