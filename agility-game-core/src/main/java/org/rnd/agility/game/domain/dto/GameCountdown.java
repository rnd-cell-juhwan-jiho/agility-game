package org.rnd.agility.game.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCountdown {
    private final String type;
    private final Integer count;
}
