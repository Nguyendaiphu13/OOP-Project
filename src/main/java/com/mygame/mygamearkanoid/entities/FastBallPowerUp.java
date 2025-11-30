package com.mygame.mygamearkanoid.entities;

import com.mygame.mygamearkanoid.core.StaticFinal;

// Kế thừa từ PowerUp
public class FastBallPowerUp extends PowerUp {

    // Constructor, đặt tên type là "fast_ball" và thời hạn 8 giây
    public FastBallPowerUp(double x, double y) {
        super(x, y, 20, 20, "fast_ball", 8000);
    }

    @Override
    public void applyEffect(Paddle paddle, Ball ball) {
        // Tăng tốc độ bóng lên 1.5 lần
        ball.setSpeed(StaticFinal.BALL_SPEED * 1.5);
    }

    @Override
    public void removeEffect(Paddle paddle, Ball ball) {
        // Trả tốc độ bóng về mặc định
        ball.setSpeed(StaticFinal.BALL_SPEED);
    }
}
