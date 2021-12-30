package org.rnd.agility.game.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerMessage {
    String type;
    String message;
}
