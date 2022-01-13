package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reject {
    private String type = DtoType.REJECT;
}
