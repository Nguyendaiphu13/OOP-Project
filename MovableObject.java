package org.example.demo;
abstract class MovableObject extends GameObject {
    protected double dx,dy;

    // constructor này là để tạo di chuyển cho ball
    public MovableObject(double x, double y, int width, int height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    // cái này cho paddle nên không cần dx,dy
    public MovableObject(double x, double y, int width, int height) {
        super(x, y, width, height);
    }
    public double getDx() {
        return dx;
    }
    public double getDy() {
        return dy;
    }
    public void setDx(double dx) {
        this.dx = dx;
    }
    public void setDy(double dy) {
        this.dy = dy;
    }

    public void move() {
        x = x + dx;
        y = y + dy;
    }
    public void update() {
        move();

    }
}