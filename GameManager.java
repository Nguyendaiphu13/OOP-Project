package com.mygame.mygamearkanoid;
import java.util.List;
import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class GameManager {
    public Paddle paddle;
    public Ball ball;
    public List<Brick> bricks;
    public int score;
    public int lives;
    public String gameState; // trang thái game

    public List<PowerUp> fallingPowerUps = new ArrayList<>();
    // các power-up đang được áp dụng

    public List<PowerUp> activeEffects = new ArrayList<>();

    // đếm thời gian
    public long effectStartTime = 0;

    public static final int PADDLE_WIDTH_DEFAULT = 100;
    public static final int PADDLE_HEIGHT_DEFAULT = 20;

    public GameManager() {
        this.bricks = new ArrayList<>();
        this.score = 0;
        this.lives = 3;
        this.gameState = "Đang chơi"; // trạng thái đang  chs game
    }

    public void startGame() {
        System.out.println("Chơi");
    }

    public void updateGame() {
        // chưa cập nhật

        if (lives <= 0) {
            gameOver(); // thuaaaaa
        }
    }


    public void gameOver() {
        gameState = "Thua"; // sau khi thua đổi trạng thái
        System.out.println("Final score: " + score);
    }

    // ========== HÀM MỚI: SAVE GAME ==========
    public void saveGame(String filename) throws IOException {
        GameSave data = new GameSave();

        // 1. Lưu state chung
        data.score = this.score;
        data.lives = this.lives;

        // 2. Lưu Paddle
        data.paddleX = this.paddle.x;
        data.paddleWidth = this.paddle.width;

        // 3. Lưu Ball
        data.ballX = this.ball.x;
        data.ballY = this.ball.y;
        data.ballDirectionX = this.ball.directionX;
        data.ballDirectionY = this.ball.directionY;
        data.ballAlive = this.ball.alive;

        // 4. Lưu Bricks
        for (Brick b : this.bricks) {
            data.bricks.add(new BrickSave(b));
        }

        // 5. Lưu Active Power-up
        if (!this.activeEffects.isEmpty()) {
            PowerUp effect = this.activeEffects.getFirst();
            long elapsedTime = System.currentTimeMillis() - this.effectStartTime;
            long timeRemaining = effect.getDuration() - elapsedTime;

            if (timeRemaining > 0) {
                data.activeEffect = new PowerUpSave(effect.getType(), timeRemaining);
            }
        }

        // 6. Ghi ra tệp tin
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Game đã lưu vào: " + filename);
        }
    }

    // ========== HÀM MỚI: LOAD GAME ==========
    // ========== HÀM MỚI: LOAD GAME (ĐÃ SỬA LỖI) ==========
    public void loadGame(String filename) throws IOException, ClassNotFoundException {
        GameSave save;

        // 1. Đọc tệp tin
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            save = (GameSave) ois.readObject();
        }

        // 2. Khôi phục state chung
        this.score = save.score;
        this.lives = save.lives;
        this.gameState = "Đang chơi"; // Đảm bảo game ở trạng thái chơi

        // 3. Khôi phục Paddle (SỬA LỖI: Kiểm tra null và khởi tạo)
        if (this.paddle == null) {
            // Chúng ta phải tạo paddle mới với các giá trị từ file save
            // Các giá trị y, height, speed lấy từ hàm setupGameObjects
            double paddleY = 750 - 20 - 50; // (SCREEN_HEIGHT - paddleHeight - 50)
            int paddleHeight = 20;
            double paddleSpeed = 100;
            this.paddle = new Paddle(save.paddleX, paddleY, save.paddleWidth, paddleHeight, paddleSpeed);
        } else {
            // Nếu paddle đã tồn tại (trường hợp hiếm), chỉ cần set
            this.paddle.setX(save.paddleX);
            this.paddle.setWidth(save.paddleWidth);
        }

        // 4. Khôi phục Ball (SỬA LỖI: Kiểm tra null và khởi tạo)
        if (this.ball == null) {
            // Tạo bóng mới với các giá trị từ file save
            // Các giá trị width, height, speed, radius lấy từ hàm setupGameObjects
            int ballRadius = 10;
            int ballSize = ballRadius * 2;
            double ballSpeed = 5.0;
            this.ball = new Ball(save.ballX, save.ballY, ballSize, ballSize, ballSpeed,
                    save.ballDirectionX, save.ballDirectionY, ballRadius);
        } else {
            // Nếu bóng đã tồn tại, chỉ cần set
            this.ball.setX(save.ballX);
            this.ball.setY(save.ballY);
            this.ball.directionX = save.ballDirectionX;
            this.ball.directionY = save.ballDirectionY;
        }

        // Cập nhật các giá trị còn lại cho bóng
        this.ball.alive = save.ballAlive;
        this.ball.updateVelocity(); // Rất quan trọng: cập nhật dx, dy!

        // 5. Khôi phục Bricks
        this.bricks.clear();
        for (BrickSave bd : save.bricks) {
            // Tạo lại gạch từ dữ liệu đã lưu
            Brick b = new Brick(bd.x, bd.y, bd.width, bd.height, bd.type);

            // Điều chỉnh hitPoints (vì constructor mặc định là 1 hoặc 2)
            if (bd.type.equals("2normal") && bd.hitPoints == 1) {
                b.takeHit(); // Đánh 1 hit để nó trở về 1 máu
            }
            this.bricks.add(b);
        }

        // 6. Khôi phục Power-up
        this.fallingPowerUps.clear();
        this.activeEffects.clear();
        if (save.activeEffect != null) {
            PowerUp newEffect = null;

            // Tạo lại đối tượng PowerUp dựa trên type
            if (save.activeEffect.type.equals("expand")) {
                newEffect = new ExpandPaddlePowerUp(0, 0); // X, Y không quan trọng
            }
            // (Thêm các else if cho các loại power-up khác nếu có)

            if (newEffect != null) {
                // Áp dụng lại hiệu ứng
                newEffect.applyEffect(this.paddle, this.ball);
                this.activeEffects.add(newEffect);

                // Đặt thời gian bắt đầu "ảo" trong quá khứ
                // để nó hết hạn đúng lúc
                long duration = newEffect.getDuration();
                this.effectStartTime = System.currentTimeMillis() - (duration - save.activeEffect.time);
            }
        }
    }
}

