public class Paddle extends MovableObject {
    protected double speed;
    protected String currentPowerUp;
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public double getSpeed() {
        return speed;
    }
    public Paddle(int x, int y, int width, int height, double speed) {
        super(x, y, width, height, dx, dy);
        this.speed = speed;
        this.currentPowerUp = null;
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
