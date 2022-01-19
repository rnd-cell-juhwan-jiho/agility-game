package org.rnd.agility.game.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Init {
    private String type = DtoType.INIT;

    //not null only when c->s
    private String username;

    //not null only when s->c
    private List<User> users;
}
