package org.rnd.agility.game.domain.join.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Games {
    private List<GameThumbnail> list;
}