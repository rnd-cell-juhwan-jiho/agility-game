package org.rnd.agility.game.domain.game.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserReady {
    private String type = DtoType.READY;
    private String username;
    private Boolean ready;
}
