package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.entities.Ball;
import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.GameObject;
import com.mygame.mygamearkanoid.entities.Paddle;

// check va chạm
public class CheckCollision {

    // kiểm tra va chạm
    public static boolean intersects(GameObject obj1, GameObject obj2) {
        return !(obj1.getX() + obj1.getWidth() < obj2.getX()
                || obj1.getX() > obj2.getX() + obj2.getWidth()
                || obj1.getY() + obj1.getHeight() < obj2.getY()
                || obj1.getY() > obj2.getY() + obj2.getHeight());
    }

    // va chạm với tường
    public static void checkWallCollision(Ball ball, int screenWidth, int screenHeight) {
        boolean directionChanged = false;
        // chạm tường trái hoặc phải thì đổi hướng x
        if (ball.getX() <= 0) {
            ball.directionX *= -1;
            ball.setX(0);
            directionChanged = true;
        } else if (ball.getX() + ball.getWidth() >= screenWidth) {
            ball.directionX *= -1;
            ball.setX(screenWidth - ball.getWidth());
            directionChanged = true;
        }

        // va chạm tường trên đổi hướng y
        if (ball.getY() <= 0) {
            ball.directionY *= -1;
            ball.setY(0);
            directionChanged = true;
        }

        // rơi ra màn hình thì - 1 mạng
        if (ball.getY() + ball.getHeight() >= screenHeight) {
            ball.alive = false;
        }

        // cập nhật lại vận tốc nếu hướng thay đổi
        if (directionChanged) {
            ball.updateVelocity();
        }
    }

    // check va chạm với obj khác như paddle
    public static void bounceOffPaddle(Ball ball, Paddle paddle) {
        double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
        double ballCenter = ball.getX() + ball.getWidth() / 2.0;

        double hitPos = (ballCenter - paddleCenter) / (paddle.getWidth() / 2.0);
        hitPos = Math.max(-1.0, Math.min(1.0, hitPos));

        double maxAngle = Math.toRadians(60);
        double angle = hitPos * maxAngle;

        ball.directionX = Math.sin(angle);
        ball.directionY = -Math.cos(angle); // Hướng lên trên

        //cập nhật vận tốc ngay
        ball.updateVelocity();
    }

    public static boolean[] bounceOffBrick(Ball ball, Brick brick, boolean hasBouncedX, boolean hasBouncedY) {
        // Lấy tọa độ tâm
        double ballCenterX = ball.getX() + ball.getWidth() / 2.0;
        double ballCenterY = ball.getY() + ball.getHeight() / 2.0;
        double brickCenterX = brick.getX() + brick.getWidth() / 2.0;
        double brickCenterY = brick.getY() + brick.getHeight() / 2.0;

        // Tính độ lún
        double overlapX = (ball.getWidth() / 2.0 + brick.getWidth() / 2.0) - Math.abs(ballCenterX - brickCenterX);
        double overlapY = (ball.getHeight() / 2.0 + brick.getHeight() / 2.0) - Math.abs(ballCenterY - brickCenterY);

        if (overlapX < overlapY) {
            // Va chạm cạnh bên
            if (!hasBouncedX) {
                ball.directionX *= -1;
                hasBouncedX = true; // Cập nhật cờ
            }

            // Luôn sửa vị trí
            if (ball.getDx() > 0) {
                ball.setX(ball.getX() - overlapX);
            } else {
                ball.setX(ball.getX() + overlapX);
            }
        } else {
            // Va chạm cạnh trên/dưới
            if (!hasBouncedY) {
                ball.directionY *= -1;
                hasBouncedY = true; // Cập nhật cờ
            }

            // Luôn sửa vị trí
            if (ball.getDy() > 0) {
                ball.setY(ball.getY() - overlapY);
            } else {
                ball.setY(ball.getY() + overlapY);
            }
        }
        
        return new boolean[]{hasBouncedX, hasBouncedY};
    }
}