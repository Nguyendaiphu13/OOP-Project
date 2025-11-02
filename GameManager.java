package org.example.demo;
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

    // --- TÍNH NĂNG MỚI ---
    public int currentLevel;
    public String gameMode; // "Standard" hoặc "Endless"
    // ---------------------

    public List<PowerUp> fallingPowerUps = new ArrayList<>();
    public List<PowerUp> activeEffects = new ArrayList<>();
    public long effectStartTime = 0;

    public static final int PADDLE_WIDTH_DEFAULT = 100;
    public static final int PADDLE_HEIGHT_DEFAULT = 20;

    public GameManager() {
        this.bricks = new ArrayList<>();
        this.score = 0;
        this.lives = 3;
        this.gameState = "Đang chơi";
        // --- TÍNH NĂNG MỚI ---
        this.currentLevel = 1;
        this.gameMode = "Standard"; // Mặc định
        // ---------------------
    }

    public void startGame() {
        System.out.println("Chơi");
    }

    public void updateGame() {
        if (lives <= 0) {
            gameOver();
        }
    }


    public void gameOver() {
        gameState = "Thua";
        System.out.println("Final score: " + score);
    }

    public void saveGame(String filename) throws IOException {
        GameSave data = new GameSave();

        // 1. Lưu state chung
        data.score = this.score;
        data.lives = this.lives;
        // --- TÍNH NĂNG MỚI ---
        data.currentLevel = this.currentLevel;
        data.gameMode = this.gameMode;
        // ---------------------

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
            PowerUp effect = this.activeEffects.get(0); // Sửa lỗi getFirst()
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


    public void loadGame(String filename) throws IOException, ClassNotFoundException {
        GameSave save;

        // 1. Đọc tệp tin
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            save = (GameSave) ois.readObject();
        }

        // 2. Khôi phục state chung
        this.score = save.score;
        this.lives = save.lives;
        this.gameState = "Đang chơi";
        // --- TÍNH NĂNG MỚI ---
        this.currentLevel = save.currentLevel;
        this.gameMode = save.gameMode;
        // Nếu file save cũ không có gameMode, đặt mặc định
        if (this.gameMode == null) {
            this.gameMode = "Standard";
        }
        // ---------------------


        // 3. Khôi phục Paddle
        if (this.paddle == null) {
            double paddleY = 750 - 20 - 50;
            int paddleHeight = 20;
            double paddleSpeed = 100;
            this.paddle = new Paddle(save.paddleX, paddleY, save.paddleWidth, paddleHeight, paddleSpeed);
        } else {
            this.paddle.setX(save.paddleX);
            this.paddle.setWidth(save.paddleWidth);
        }

        // 4. Khôi phục Ball
        if (this.ball == null) {
            int ballRadius = 10;
            int ballSize = ballRadius * 2;
            double ballSpeed = 5.0;
            this.ball = new Ball(save.ballX, save.ballY, ballSize, ballSize, ballSpeed,
                    save.ballDirectionX, save.ballDirectionY, ballRadius);
        } else {
            this.ball.setX(save.ballX);
            this.ball.setY(save.ballY);
            this.ball.directionX = save.ballDirectionX;
            this.ball.directionY = save.ballDirectionY;
        }

        this.ball.alive = save.ballAlive;
        this.ball.updateVelocity();

        // 5. Khôi phục Bricks
        this.bricks.clear();
        for (BrickSave bd : save.bricks) {
            Brick b = new Brick(bd.x, bd.y, bd.width, bd.height, bd.type);
            if (bd.type.equals("2normal") && bd.hitPoints == 1) {
                b.takeHit();
            }
            this.bricks.add(b);
        }

        // 6. Khôi phục Power-up
        this.fallingPowerUps.clear();
        this.activeEffects.clear();
        if (save.activeEffect != null) {
            PowerUp newEffect = null;

            if (save.activeEffect.type.equals("expand")) {
                newEffect = new ExpandPaddlePowerUp(0, 0);
            }

            if (newEffect != null) {
                newEffect.applyEffect(this.paddle, this.ball);
                this.activeEffects.add(newEffect);
                long duration = newEffect.getDuration();
                this.effectStartTime = System.currentTimeMillis() - (duration - save.activeEffect.time);
            }
        }
    }
}
