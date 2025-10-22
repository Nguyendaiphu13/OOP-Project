package org.example.demo;

import javafx.scene.canvas.GraphicsContext;

public class Paddle extends MovableObject {
    protected double speed;
    protected static String currentPowerUp;

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

    public String getCurrentPowerUp() {
        return currentPowerUp;
    }
    public void applyPowerUp(String powerUp) {
        this.currentPowerUp = powerUp;
        switch (powerUp) {
            case "Expand":
                width += 20;
                height += 20;
                break;
            case "Shrink":
                width -= 20;
                height -= 20;
                break;
            case "SpeedUp":
                speed += 2;
                break;
            case "SlowDown":
                speed -= 2;
                break;
            default:
                break;
        }
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