package org.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {
    protected int radius;         // bán kính bóng
    protected double speed = 3.0; // tốc độ di chuyển
    protected double directionX = 1;  // hướng X (1 = phải, -1 = trái)
    protected double directionY = -1; // hướng Y (-1 = lên, 1 = xuống)
    public boolean alive = true;

    // --- Constructor đầy đủ ---
    public Ball(double x, double y, int width, int height, double speed, double directionX, double directionY, int radius) {
        super(x, y, width, height, directionX * speed, directionY * speed);
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;
        this.radius = radius;
    }

    // --- Constructor ngắn ---
    public Ball(double x, double y, int width, int height) {
        this(x, y, width, height, 3.0, 1.0, -1.0, Math.min(width, height) / 2);
    }

    // --- getter / setter bán kính ---
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    // --- getter / setter tốc độ ---
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if (speed <= 0) return;
        this.speed = speed;
        // Cập nhật dx/dy tương ứng với tốc độ mới
        this.dx = this.directionX * speed;
        this.dy = this.directionY * speed;
    }

    // --- Cập nhật hướng ---
    public void setDirection(double dirX, double dirY) {
        this.directionX = dirX;
        this.directionY = dirY;
        this.dx = dirX * speed;
        this.dy = dirY * speed;
    }

    // --- Kiểm tra va chạm với vật khác ---
    public boolean intersects(GameObject other) {
        return !(x + width < other.getX()
                || x > other.getX() + other.getWidth()
                || y + height < other.getY()
                || y > other.getY() + other.getHeight());
    }

    // --- Kiểm tra va chạm ---
    public boolean checkCollision(GameObject other) {
        return intersects(other);
    }

    // --- Va chạm tường ---
    public void checkWallCollision(int screenWidth, int screenHeight) {
        // Chạm tường trái hoặc phải
        if (x <= 0 || x + width >= screenWidth) {
            directionX *= -1;
            dx = directionX * speed;
        }

        // Chạm trần
        if (y <= 0) {
            directionY *= -1;
            dy = directionY * speed;
        }

        // Rơi khỏi màn hình (thua)
        if (y + height >= screenHeight) {
            alive = false;
        }
    }

    // --- Va chạm với Brick hoặc Paddle ---
    public void bounceOff(GameObject other) {
        if (other instanceof Brick) { 
            // Nảy ngược theo trục Y
            directionY *= -1;
            dy = directionY * speed;

        } else if (other instanceof Paddle paddle) {
            // Xác định vị trí va chạm trên paddle để đổi góc bật
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
            double ballCenter = x + width / 2.0;

            double hitPos = (ballCenter - paddleCenter) / (paddle.getWidth() / 2.0);
            hitPos = Math.max(-1.0, Math.min(1.0, hitPos));

            double maxAngle = Math.toRadians(60);
            double angle = hitPos * maxAngle;

            directionX = Math.sin(angle);
            directionY = -Math.cos(angle);

            dx = directionX * speed;
            dy = directionY * speed;
        } else {
            // mặc định đảo chiều Y
            directionY *= -1;
            dy = directionY * speed;
        }
    }

    // --- Cập nhật vị trí ---
    public void update() {
        x += dx;
        y += dy;
    }

    // --- Vẽ bóng ---
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x, y, width, height);
    }
}
