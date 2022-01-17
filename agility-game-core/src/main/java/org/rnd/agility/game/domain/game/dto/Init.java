package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class Init {
    private String init = DtoType.INIT;
    private Map<String, Boolean> users;
}
