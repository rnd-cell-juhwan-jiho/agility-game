package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserEntrance {
    private String type;
    private String username;
    private Set<String> users;
    private Set<String> usersReady;
}
