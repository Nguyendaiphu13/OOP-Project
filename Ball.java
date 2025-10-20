package org.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {
    protected int radius; // kích cỡ quả bóng
    protected double speed = 1.0;
    // direction components (unit-ish) — lưu để dễ hiểu; dx/dy = directionX * speed ...
    protected double directionX = 1;
    protected double directionY = -1;
    public boolean alive = true;

    public Ball(double x, double y, int width, int height, double speed, double directionX, double directionY, int radius) {
        super(x, y, width, height, directionX * speed, directionY * speed);
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;
        this.radius = radius;
    }

    public Ball(double x, double y, int width, int height) {
        this(x, y, width, height, 3.0, 1.0, -1.0, Math.min(width, height) / 2);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    // --- getters / setters cho vx/vy (dx/dy) để dễ truy cập từ main/GameManager ---
    public double getVx() {
        return dx;
    }

    public double getVy() {
        return dy;
    }

    public void setVx(double vx) {
        this.dx = vx;
        // cập nhật directionX nếu muốn giữ đơn vị
        if (speed != 0) this.directionX = vx / speed;
    }

    public void setVy(double vy) {
        this.dy = vy;
        if (speed != 0) this.directionY = vy / speed;
    }

    public double getDirectionX() {
        return directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public void setDirection(double dirX, double dirY) {
        this.directionX = dirX;
        this.directionY = dirY;
        this.dx = dirX * speed;
        this.dy = dirY * speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        // điều chỉnh dx/dy theo speed mới nhưng giữ hướng
        if (speed <= 0) return;
        this.speed = speed;
        this.dx = this.directionX * speed;
        this.dy = this.directionY * speed;
    }

    // --- kiểm tra va chạm (AABB) nhưng KHÔNG tự xử lý va chạm ---
    public boolean intersects(GameObject other) {
        return !(x + width < other.getX()
                || x > other.getX() + other.getWidth()
                || y + height < other.getY()
                || y > other.getY() + other.getHeight());
    }

    // --- kiểm tra và xử lý va chạm (gọi bounceOff nếu va chạm) ---
    public boolean checkCollision(GameObject other) {
        if (intersects(other)) {
            return true;
        }
        return false;
    }

    // va chạm với tường
    public void checkWallCollision(int screenWidth, int screenHeight) {
        if (x <= 0 || x + width >= screenWidth) {
            directionX *= -1;
            dx = directionX * speed;
        }

        if (y <= 0) {
            directionY *= -1;
            dy = directionY * speed;
        }

        if (y + height >= screenHeight) {
            alive = false;
        }
    }

    // Va chạm với các Object khác: public để gọi từ ngoài nếu muốn
    public void bounceOff(GameObject other) {
        if (other instanceof Brick) { // Va chạm với gạch
            directionY *= -1;
            dy = directionY * speed;
        } else if (other instanceof Paddle) {
            Paddle paddle = (Paddle) other;

            double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
            double ballCenter = x + width / 2.0;

            // Hit position trong [-1, 1]
            double hitPos = (ballCenter - paddleCenter) / (paddle.getWidth() / 2.0);
            hitPos = Math.max(-1.0, Math.min(1.0, hitPos));

            double maxAngle = Math.toRadians(60); // tối đa ±60 độ
            double angle = hitPos * maxAngle;

            directionX = Math.sin(angle);
            directionY = -Math.cos(angle); // hướng lên trên

            dx = directionX * speed;
            dy = directionY * speed;
        } else {
            // default: đảo chiều Y
            directionY *= -1;
            dy = directionY * speed;
        }
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, width, height);
    }
}
