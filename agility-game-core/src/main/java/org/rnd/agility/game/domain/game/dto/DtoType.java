package org.rnd.agility.game.domain.game.dto;

public abstract class DtoType {
    public static final String INIT = "INIT";
    public static final String BID = "BID";
    public static final String END = "END";
    public static final String READY = "READY";
    public static final String USER_IN = "USER_IN";
    public static final String USER_OUT = "USER_OUT";

    //s->c only
    public static final String COUNTDOWN = "COUNTDOWN";
    public static final String REJECT = "REJECT";

    //to-be added
    public static final String CHAT = "CHAT";
}
