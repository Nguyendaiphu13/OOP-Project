package com.mygame.mygamearkanoid.data;

import com.mygame.mygamearkanoid.entities.Brick;

import java.io.Serializable;


public class BrickSave implements Serializable {
    private static final long serialVersionUID = 1L;

    public double x, y;
    public int width, height;
    public String type;
    public int hitPoints;

    // constructor rỗng
    public BrickSave() {}

    // Constructor để gán dữ liệu từ một đối tượng Brick thật
    public BrickSave(Brick brick) {
        this.x = brick.getX();
        this.y = brick.getY();
        this.width = brick.getWidth();
        this.height = brick.getHeight();
        this.type = brick.getType();

        this.hitPoints = brick.getHitPoints();
    }
}