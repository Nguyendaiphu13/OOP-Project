package org.example.demo;

public abstract class PowerUp extends GameObject {
    protected String type;
    protected int duration;
    public boolean active = true;

    public PowerUp(double x, double y, int width, int height, String type, int duration) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }


    public abstract void applyEffect(Paddle paddle, Ball ball);


    public abstract void removeEffect(Paddle paddle, Ball ball);


    @Override
    public void update() {
        this.y += 2;
    }

    public String getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}