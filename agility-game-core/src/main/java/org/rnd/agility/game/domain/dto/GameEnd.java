package org.rnd.agility.game.domain.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GameEnd {
    String type;
    List<String> loserList;
}
