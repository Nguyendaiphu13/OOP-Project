package com.mygame.mygamearkanoid;

// File này bị thiếu trong danh sách của bạn nhưng được gọi trong Main1
public class ExpandPaddlePowerUp extends PowerUp {
    public ExpandPaddlePowerUp(double x, double y) {
        // Kích thước (width, height) ở đây không quá quan trọng
        super(x, y, 20, 20, "expand", 8000);
    }

    @Override
    public void applyEffect(Paddle paddle, Ball ball) {
        paddle.setWidth((int)(GameManager.PADDLE_WIDTH_DEFAULT * 1.5));
    }

    @Override
    public void removeEffect(Paddle paddle, Ball ball) {
        paddle.setWidth(GameManager.PADDLE_WIDTH_DEFAULT);
    }
}