package org.rnd.agility.game.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReady {
//    private String roomId;
    private String type = DtoType.READY;
    private String username;
    private Boolean ready;
}
