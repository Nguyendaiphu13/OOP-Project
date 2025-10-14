public abstract class FastBallPowerUp extends PowerUp{
    // tăng tốc độ bóng

    public double addSpeed = 1.5;

    public FastBallPowerUp(double x, double y) {
        super(x, y, 20, 20, "FastBall", 5000); // 5 giây hiệu lực
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (!active && paddle.getCurrentPowerUp() != null) {
            String ball = paddle.getCurrentPowerUp();
            ball.isEmpty(Ball.speed * addSpeed);
            active = true;
            System.out.println("Tăng tốc độ bóng");
        }
    }

    @Override
    public void removeEffect(Paddle paddle) {
        if (active && paddle.getCurrentPowerUp() != null) {
            String ball = paddle.getCurrentPowerUp();
            ball.isEmpty(Ball.speed / addSpeed);
            active = false;
            System.out.println("Hết hiệu lực");
        }
    }
}
