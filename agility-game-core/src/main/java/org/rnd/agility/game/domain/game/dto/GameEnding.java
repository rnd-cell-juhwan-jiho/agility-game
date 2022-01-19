package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameEnding {
    private String type = DtoType.END;
    private Boolean terminating;
    private List<String> losers;
}
