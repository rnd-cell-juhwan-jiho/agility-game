package org.rnd.agility.game.domain.join.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class Games {
    private Set<String> games;
}