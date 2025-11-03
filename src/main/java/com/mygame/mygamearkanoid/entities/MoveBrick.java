package com.mygame.mygamearkanoid.entities;

import com.mygame.mygamearkanoid.core.StaticFinal;
public class MoveBrick extends Brick {

    public MoveBrick(double x, double y, int width, int height, String type, double dx, double dy) {
        super(x, y, width, height, type);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        super.update();
        if (this.x <= 0) {
            this.x = 0;
            this.dx *= -1;
        }

        if (this.x + this.width >= StaticFinal.SCREEN_WIDTH) {
            this.x = StaticFinal.SCREEN_WIDTH - this.width;
            this.dx *= -1; //
        }

    }
}
