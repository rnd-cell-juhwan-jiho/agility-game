package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private String username;
    private Boolean ready;
}
