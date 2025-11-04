package com.mygame.mygamearkanoid.data;

import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.MoveBrick; // THÊM IMPORT

import java.io.Serializable;

// Lớp này dùng để lưu thông tin cốt lõi của một viên gạch
public class BrickSave implements Serializable {
    private static final long serialVersionUID = 1L;

    public double x, y;
    public int width, height;
    public String type;
    public int hitPoints;
    public double dx;
    public double dy;
    public boolean isMoveBrick;
    // ----------------------------

    // Cần một constructor rỗng
    public BrickSave() {}
    public BrickSave(Brick brick) {
        this.x = brick.getX();
        this.y = brick.getY();
        this.width = brick.getWidth();
        this.height = brick.getHeight();
        this.type = brick.getType();
        this.hitPoints = brick.getHitPoints();
        this.dx = brick.getDx();
        this.dy = brick.getDy();
        if (brick instanceof MoveBrick) {
            this.isMoveBrick = true;
        } else {
            this.isMoveBrick = false;
        }
    }
}
