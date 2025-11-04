package com.mygame.mygamearkanoid.data;

import java.io.Serializable;

public class PowerUpSave implements Serializable {
    private static final long serialVersionUID = 1L;

    public String type;
    public long time;

    public PowerUpSave() {}

    public PowerUpSave(String type, long time) {
        this.type = type;
        this.time = time;
    }
}