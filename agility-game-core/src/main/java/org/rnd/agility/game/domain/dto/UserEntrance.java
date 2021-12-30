package org.rnd.agility.game.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserEntrance {
    String type;
    String username;
    List<String> userList;
}
