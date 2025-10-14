public class Brick extends GameObject {
    private int hitPoints;
    private final String type;

    public Brick(double x, double y, int width, int height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
    }

    public void takeHit() {
        hitPoints--;
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    @Override
    public void update() {}
}
