package org.example.demo;

import javafx.scene.canvas.GraphicsContext;

public class Paddle extends MovableObject {
    protected double speed;

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public double getSpeed() {
        return speed;
    }
    public Paddle(double x, double y, int width, int height,double speed) {
        super(x, y, width, height);
        this.speed = speed;
    }

    public void update() {
        move();
        if (x < 0) {
            x = 0;
        }
    }
    public void moveLeft() {
        dx = -speed;
    }
    public void moveRight() {
        dx = speed;
    }

}