package org.rnd.agility.game.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class GameCountdown {
    private String type = DtoType.COUNTDOWN;
    private Integer count;
}
