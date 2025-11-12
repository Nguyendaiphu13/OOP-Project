package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.GameManager;
import com.mygame.mygamearkanoid.core.StaticFinal;
import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.PowerUp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

public class GameRenderer {

    public GraphicsContext gc;
    public Image gameBackgroundImage;
    private Font gameFont;

    private Image expandPowerUpImage;
    private Image fastBallPowerUpImage;

    public GameRenderer(GraphicsContext gc, Image gameBackgroundImage) {
        this.gc = gc;
        this.gameBackgroundImage = gameBackgroundImage;
        try {
            // Đường dẫn
            String fontPath = "/com/mygame/mygamearkanoid/fonts/PixelOperatorHB.ttf";
            this.gameFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 25);

            if (this.gameFont == null) {
                // Nếu load lỗi, dùng font mặc định
                System.err.println("Không load được font! Dùng font mặc định.");
                this.gameFont = Font.getDefault();
            }
        } catch (Exception e) {
            System.err.println("Lỗi load font: " + e.getMessage());
            this.gameFont = Font.getDefault();
        }
        try {
            String imgPath = "/com/mygame/mygamearkanoid/images/";

            expandPowerUpImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imgPath + "PowerUpFalling.png")));
            fastBallPowerUpImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imgPath + "PowerUpFalling.png")));
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh power-up: " + e.getMessage());
        }
    }

    public void render(GameManager gameManager) {
        // Vẽ nền game
        if (gameBackgroundImage != null) {
            gc.drawImage(gameBackgroundImage, 0, 0, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);
        }

        // Vẽ Paddle
        if (gameManager.paddle != null) {
            gameManager.paddle.render(gc);
        }

        // Vẽ Ball
        if (gameManager.ball != null) {
            gameManager.ball.render(gc);
        }

        // Vẽ Bricks
        for (Brick brick : gameManager.bricks) {
            brick.render(gc);
        }

        // Vẽ Powerups
        for (PowerUp powerUp : gameManager.fallingPowerUps) {
            drawPowerUp(gc, powerUp);
        }

        gc.setFont(this.gameFont);
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + gameManager.score, StaticFinal.SCREEN_WIDTH - 250, 20);
        gc.fillText("Lives: " + gameManager.lives, StaticFinal.SCREEN_WIDTH - 100, 20);
    }

    private void drawPowerUp(GraphicsContext gc, PowerUp powerUp) {
        Image imageToDraw = null;

        switch (powerUp.getType()) {
            case "expand":
                imageToDraw = expandPowerUpImage;
                break;
            case "fast_ball":
                imageToDraw = fastBallPowerUpImage;
                break;
            default:
                // Không nhận dạng được type
                break;
        }

        if (imageToDraw != null) {
            // Vẽ ảnh, dùng width/height từ đối tượng PowerUp
            gc.drawImage(imageToDraw, powerUp.getX(), powerUp.getY(), powerUp.getWidth(), powerUp.getHeight());
        } else {
            // Vẽ một hình vuông màu mè nếu không tìm thấy ảnh
            gc.setFill(Color.MAGENTA);
            gc.fillRect(powerUp.getX(), powerUp.getY(), powerUp.getWidth(), powerUp.getHeight());
        }
    }
}