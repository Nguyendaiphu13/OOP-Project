package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.data.BrickSave;
import com.mygame.mygamearkanoid.data.GameSave;
import com.mygame.mygamearkanoid.data.PowerUpSave;
import com.mygame.mygamearkanoid.entities.*;

import java.util.List;
import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class GameManager {
    public Paddle paddle;
    public Ball ball;
    public List<Brick> bricks;
    public int score;
    public int lives;
    public GameState gameState; // Sửa từ String sang GameState

    public List<PowerUp> fallingPowerUps = new ArrayList<>();
    public List<PowerUp> activeEffects = new ArrayList<>();
    public long effectStartTime = 0;

    public GameManager() {
        this.bricks = new ArrayList<>();
        this.score = 0;
        this.lives = 3;
        this.gameState = GameState.MENU;
    }

    // ========== HÀM MỚI (Chuyển từ Main1) ==========
    public void resetGame() {
        this.score = 0;
        this.lives = 3;
        this.gameState = GameState.PLAYING;
        this.bricks.clear();
        this.fallingPowerUps.clear();
        this.activeEffects.clear();
        setupGameObjects();
    }

    // ========== HÀM MỚI (Chuyển từ Main1) ==========
    public void setupGameObjects() {
        // Sử dụng hằng số từ StaticFinal
        double paddleX = (StaticFinal.SCREEN_WIDTH - StaticFinal.PADDLE_WIDTH_DEFAULT) / 2.0;
        double paddleY = StaticFinal.SCREEN_HEIGHT - StaticFinal.PADDLE_HEIGHT_DEFAULT - StaticFinal.PADDLE_Y_OFFSET;
        this.paddle = new Paddle(paddleX, paddleY, StaticFinal.PADDLE_WIDTH_DEFAULT, StaticFinal.PADDLE_HEIGHT_DEFAULT, StaticFinal.PADDLE_SPEED);

        int ballRadius = StaticFinal.BALL_RADIUS;
        double ballX = StaticFinal.SCREEN_WIDTH / 2.0 - ballRadius;
        double ballY = StaticFinal.SCREEN_HEIGHT / 2.0 - ballRadius;
        double vecDir = 1 / Math.sqrt(2);
        this.ball = new Ball(ballX, ballY, ballRadius * 2, ballRadius * 2, StaticFinal.BALL_SPEED, vecDir, -vecDir, ballRadius);

        // Dùng hằng số StaticFinal để gọi Level
        Level.generateLevel1(this,
                StaticFinal.BRICK_ROWS, StaticFinal.BRICK_COLS,
                StaticFinal.BRICK_WIDTH, StaticFinal.BRICK_HEIGHT,
                StaticFinal.BRICK_START_X, StaticFinal.BRICK_START_Y,
                StaticFinal.BRICK_GAP_X, StaticFinal.BRICK_GAP_Y
        );
    }

    // ========== HÀM MỚI (Chuyển từ Main1) ==========
    public void updateGame(SoundManager soundManager) {
        if (this.paddle == null || this.ball == null) return;

        paddle.update();
        ball.update();

        // Logic giới hạn paddle (dùng StaticFinal)
        if (paddle.getX() < 0) {
            paddle.setX(0);
        }
        if (paddle.getX() + paddle.getWidth() > StaticFinal.SCREEN_WIDTH) {
            paddle.setX(StaticFinal.SCREEN_WIDTH - paddle.getWidth());
        }

        // Logic va chạm tường (dùng StaticFinal)
        CheckCollision.checkWallCollision(ball, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);

        // Va chạm bóng và paddle
        if (CheckCollision.intersects(ball, paddle)) {
            CheckCollision.bounceOff(ball, paddle);
            soundManager.playPaddleHit();
        }

        // Va chạm bóng và gạch
        Iterator<Brick> brickIterator = bricks.iterator();
        while (brickIterator.hasNext()) {
            Brick brick = brickIterator.next();
            if (CheckCollision.intersects(ball, brick)) {
                CheckCollision.bounceOff(ball, brick);

                if (brick.getType().equals("2normal")) {
                    brick.takeHit();
                    if (!brick.isDestroyed()){
                        soundManager.playHardBrickHit();
                    } else {
                        soundManager.playBrickHit();
                    }
                } else {
                    brick.takeHit();
                    soundManager.playBrickHit();
                }

                if (brick.isDestroyed()) {
                    if (brick.getType().equals("expand")) {
                        double pX = brick.getX() + (brick.getWidth() / 2.0) - 10;
                        double pY = brick.getY();
                        fallingPowerUps.add(new ExpandPaddlePowerUp(pX, pY));
                    }
                    brickIterator.remove();
                    score += 10;
                    break;
                }
            }
        }

        // Kiểm tra thắng
        if (bricks.isEmpty()) {
            gameState = GameState.VICTORY;
        }

        // Cập nhật power-up đang rơi (dùng StaticFinal)
        Iterator<PowerUp> powerUpIterator = fallingPowerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            powerUp.update();

            if (powerUp.getY() > StaticFinal.SCREEN_HEIGHT) { // Dùng StaticFinal
                powerUpIterator.remove();
                continue;
            }

            if (CheckCollision.intersects(powerUp, paddle)) {
                for (PowerUp oldEffect : activeEffects) {
                    oldEffect.removeEffect(paddle, ball);
                }
                activeEffects.clear();

                powerUp.applyEffect(paddle, ball);
                activeEffects.add(powerUp);
                effectStartTime = System.currentTimeMillis();

                powerUpIterator.remove();
            }
        }

        // Cập nhật hiệu ứng đang kích hoạt
        if (!activeEffects.isEmpty()) {
            PowerUp currentEffect = activeEffects.getFirst();
            long elapsedTime = System.currentTimeMillis() - effectStartTime;

            if (elapsedTime > currentEffect.getDuration()) {
                currentEffect.removeEffect(paddle, ball);
                activeEffects.remove(currentEffect);
            }
        }

        // Xử lý mất bóng
        if (!ball.alive) {
            lives--;
            if (lives <= 0) {
                gameOver();
            } else {
                double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
                double newBallX = paddleCenterX - ball.getWidth() / 2.0;
                double newBallY = paddle.getY() - ball.getHeight() - 5;

                ball.setX(newBallX);
                ball.setY(newBallY);

                double initialDir = 1 / Math.sqrt(2);
                ball.directionX = initialDir;
                ball.directionY = -initialDir;
                ball.updateVelocity();
                ball.alive = true;
            }
        }
    }


    public void gameOver() {
        gameState = GameState.GAME_OVER;
        System.out.println("Final score: " + score);
    }

    // ========== HÀM SAVE GAME ==========
    public void saveGame(String filename) throws IOException {
        GameSave data = new GameSave();

        data.score = this.score;
        data.lives = this.lives;
        data.paddleX = this.paddle.getX();
        data.paddleWidth = this.paddle.getWidth();
        data.ballX = this.ball.getX();
        data.ballY = this.ball.getY();
        data.ballDirectionX = this.ball.directionX;
        data.ballDirectionY = this.ball.directionY;
        data.ballAlive = this.ball.alive;
        for (Brick b : this.bricks) {
            data.bricks.add(new BrickSave(b));
        }
        if (!this.activeEffects.isEmpty()) {
            PowerUp effect = this.activeEffects.getFirst();
            long elapsedTime = System.currentTimeMillis() - this.effectStartTime;
            long timeRemaining = effect.getDuration() - elapsedTime;
            if (timeRemaining > 0) {
                data.activeEffect = new PowerUpSave(effect.getType(), timeRemaining);
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Game đã lưu vào: " + filename);
        }
    }

    // ========== HÀM LOAD GAME (Sửa để dùng StaticFinal) ==========
    public void loadGame(String filename) throws IOException, ClassNotFoundException {
        GameSave save;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            save = (GameSave) ois.readObject();
        }

        this.score = save.score;
        this.lives = save.lives;
        this.gameState = GameState.PLAYING;

        // Khôi phục Paddle (dùng StaticFinal)
        if (this.paddle == null) {
            double paddleY = StaticFinal.SCREEN_HEIGHT - StaticFinal.PADDLE_HEIGHT_DEFAULT - StaticFinal.PADDLE_Y_OFFSET;
            this.paddle = new Paddle(save.paddleX, paddleY, save.paddleWidth, StaticFinal.PADDLE_HEIGHT_DEFAULT, StaticFinal.PADDLE_SPEED);
        } else {
            this.paddle.setX(save.paddleX);
            this.paddle.setWidth(save.paddleWidth);
        }

        // Khôi phục Ball (dùng StaticFinal)
        if (this.ball == null) {
            int ballRadius = StaticFinal.BALL_RADIUS;
            int ballSize = ballRadius * 2;
            this.ball = new Ball(save.ballX, save.ballY, ballSize, ballSize, StaticFinal.BALL_SPEED,
                    save.ballDirectionX, save.ballDirectionY, ballRadius);
        } else {
            this.ball.setX(save.ballX);
            this.ball.setY(save.ballY);
            this.ball.directionX = save.ballDirectionX;
            this.ball.directionY = save.ballDirectionY;
        }

        this.ball.alive = save.ballAlive;
        this.ball.updateVelocity();

        // Khôi phục Bricks
        this.bricks.clear();
        for (BrickSave bd : save.bricks) {
            Brick b = new Brick(bd.x, bd.y, bd.width, bd.height, bd.type);
            if (bd.type.equals("2normal") && bd.hitPoints == 1) {
                b.takeHit();
            }
            this.bricks.add(b);
        }

        // Khôi phục Power-up
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