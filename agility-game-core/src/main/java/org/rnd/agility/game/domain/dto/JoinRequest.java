package org.rnd.agility.game.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinRequest {
    String roomId;
    String type;
    String username;
}
