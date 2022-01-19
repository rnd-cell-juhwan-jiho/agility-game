package org.rnd.agility.game.domain.game.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GameBid {
    private String type = DtoType.BID;
    private String username;
    private Date time = Date.from(Instant.now());
    private Integer bid = 0;
}
