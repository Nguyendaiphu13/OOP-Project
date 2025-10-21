public abstract class ExpandPaddlePowerUp extends PowerUp{
    // tăng kích thước paddle

    public int expandAmount = 50;

    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, 20, 20, "ExpandPaddle", 5000); // 5 giây hiệu lực
    }


    public void applyEffect(Paddle paddle) {
        if (Paddle.currentPowerUp == null) {
            paddle.setWidth(paddle.getWidth() + expandAmount);
            active = true;
            System.out.println("Tăng kích tươớc paddle");
        }
    }


    public void removeEffect(Paddle paddle) {
        if (active) {
            paddle.setWidth(paddle.getWidth() - expandAmount);
            active = false;
            System.out.println("Hết hiệu lực");
        }
    }
}


