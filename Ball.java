package org.example.demo;




/**
 * Đại diện cho đối tượng quả bóng trong game.
 * Lớp này chỉ chịu trách nhiệm về dữ liệu, trạng thái,
 * và việc cập nhật vị trí của chính nó.
 * Logic va chạm được xử lý bởi lớp CheckCollision.
 */
public class Ball extends MovableObject {
    public int radius;
    public double speed ;
    public double directionX ;
    public double directionY ;
    public boolean alive = true;

    // constructor
    public Ball(double x, double y, int width, int height, double speed, double directionX, double directionY, int radius) {
        super(x, y, width, height, directionX * speed, directionY * speed);
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;
        this.radius = radius;
    }

    // getter setter
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if (speed <= 0) return;
        this.speed = speed;
        updateVelocity(); // cập nhật vận tốc khi tốc độ thay đổi
    }

    public void updateVelocity() {
        this.dx = this.directionX * this.speed;
        this.dy = this.directionY * this.speed;
    }

    @Override
    public void update() {
        this.x += this.dx;
        this.y += this.dy;
    }


}