package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.GameManager;
import com.mygame.mygamearkanoid.core.StaticFinal;
import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.PowerUp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameRenderer {

    private GraphicsContext gc;
    private Image gameBackgroundImage;

    public GameRenderer(GraphicsContext gc, Image gameBackgroundImage) {
        this.gc = gc;
        this.gameBackgroundImage = gameBackgroundImage;
    }

    public void render(GameManager gameManager) {
        // 1. Vẽ nền game (dùng StaticFinal)
        if (gameBackgroundImage != null) {
            gc.drawImage(gameBackgroundImage, 0, 0, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);
        }

        // 2. Vẽ Paddle
        if (gameManager.paddle != null) {
            gameManager.paddle.render(gc);
        }

        // 3. Vẽ Ball
        if (gameManager.ball != null) {
            gameManager.ball.render(gc);
        }

        // 4. Vẽ Bricks
        for (Brick brick : gameManager.bricks) {
            brick.render(gc);
        }

        // 5. Vẽ Powerups
        gc.setFill(Color.WHITE);
        for (PowerUp powerUp : gameManager.fallingPowerUps) {
            gc.fillOval(powerUp.getX(), powerUp.getY(), 10, 10);
        }

        // 6. Vẽ UI (dùng StaticFinal)
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + gameManager.score, 10, 20);
        gc.fillText("Lives: " + gameManager.lives, StaticFinal.SCREEN_WIDTH - 60, 20);
    }
}