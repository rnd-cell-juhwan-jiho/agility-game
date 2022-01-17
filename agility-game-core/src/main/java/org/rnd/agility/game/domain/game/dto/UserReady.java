package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserReady {
    private String type = DtoType.READY;
    private String username;
    private Boolean ready;
}
