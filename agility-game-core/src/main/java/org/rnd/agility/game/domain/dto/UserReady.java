package org.rnd.agility.game.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReady {
//    private String roomId;
    private String type;
    private String username;
    private Boolean ready;
}
