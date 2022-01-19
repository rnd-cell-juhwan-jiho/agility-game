package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntrance {
    private String type;
    private String username;

    //`true` only when RUNNING game is canceling to VOTING
    private Boolean cancel = false;
}
