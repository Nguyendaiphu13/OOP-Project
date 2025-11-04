package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.GameManager;
import com.mygame.mygamearkanoid.core.StaticFinal;
import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.PowerUp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameRenderer {

    public GraphicsContext gc;
    public Image gameBackgroundImage;
    private Font gameFont;

    public GameRenderer(GraphicsContext gc, Image gameBackgroundImage) {
        this.gc = gc;
        this.gameBackgroundImage = gameBackgroundImage;
        try {
            // Đường dẫn đến font trong thư mục resources
            String fontPath = "/com/mygame/mygamearkanoid/fonts/VT323-Regular.ttf";
            this.gameFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 24);

            if (this.gameFont == null) {
                // Nếu load lỗi, dùng font mặc định
                System.err.println("Không load được font! Dùng font mặc định.");
                this.gameFont = Font.getDefault();
            }
        } catch (Exception e) {
            System.err.println("Lỗi load font: " + e.getMessage());
            this.gameFont = Font.getDefault();
        }
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
        gc.setFont(this.gameFont);
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + gameManager.score, StaticFinal.SCREEN_WIDTH - 250, 20);
        gc.fillText("Lives: " + gameManager.lives, StaticFinal.SCREEN_WIDTH - 100, 20);
    }
}