package org.example.demo;

// 1. Xóa 'abstract'
public class ExpandPaddlePowerUp extends PowerUp {

    public int expandAmount = 50;
    private boolean effectApplied = false; // biến để theo dõi powerup

    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, 20, 20, "ExpandPaddle", 10000); // 5 giây
    }

    @Override
    public void applyEffect(Paddle paddle, Ball ball) {
        // chỉ áp dụng hiệu ứng nếu nó chưa được áp dụng
        if (!effectApplied) {
            paddle.setWidth(paddle.getWidth() + expandAmount);
            effectApplied = true;
            System.out.println("Tăng kích thước paddle");
        }
    }

    @Override
    public void removeEffect(Paddle paddle, Ball ball) {
        // Chỉ gỡ bỏ hiệu ứng nếu nó đã được áp dụng
        if (effectApplied) {
            paddle.setWidth(paddle.getWidth() - expandAmount);
            effectApplied = false;
            this.setActive(false); // Đánh dấu power-up này là "chết" hoàn toàn
            System.out.println("Hết hiệu lực");
        }
    }
}