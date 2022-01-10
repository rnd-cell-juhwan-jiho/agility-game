package org.rnd.agility.game.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameBid {
//    private String roomId;
    private String type;
    private String username;
    private Date time = Date.from(Instant.now());
    private Integer bid;
}
