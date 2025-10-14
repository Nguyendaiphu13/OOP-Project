public abstract class PowerUp extends GameObject {
    protected String type;
    protected int duration;

    public PowerUp(double x, double y, int width, int height, String type) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }


    public abstract void applyEffect(Paddle paddle);


    public abstract void removeEffect(Paddle paddle);


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
