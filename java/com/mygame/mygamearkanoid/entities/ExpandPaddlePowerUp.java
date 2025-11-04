package com.mygame.mygamearkanoid.entities;

import com.mygame.mygamearkanoid.core.StaticFinal;

public class ExpandPaddlePowerUp extends PowerUp {
    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, 20, 20, "expand", 8000);
    }

    @Override
    public void applyEffect(Paddle paddle, Ball ball) {
        // Sửa ở đây: Dùng StaticFinal.PADDLE_WIDTH_DEFAULT
        paddle.setWidth((int)(StaticFinal.PADDLE_WIDTH_DEFAULT * 1.5));
    }

    @Override
    public void removeEffect(Paddle paddle, Ball ball) {
        // Sửa ở đây: Dùng StaticFinal.PADDLE_WIDTH_DEFAULT
        paddle.setWidth(StaticFinal.PADDLE_WIDTH_DEFAULT);
    }
}