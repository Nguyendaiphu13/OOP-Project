package com.mygame.mygamearkanoid;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
// *** CÁC IMPORT MỚI ĐỂ TẠO MENU ***
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.text.Font;

import java.util.Iterator;

public class Main1 extends Application {

    private GameManager gameManager;
    private SoundManager soundManager;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    // (1) Khai báo các "màn hình" (Screens)
    private Pane gameScreen;
    private VBox menuScreen;
    private VBox gameOverScreen;
    private Label gameOverLabel;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager();
        soundManager = new SoundManager();

        StackPane rootPane = new StackPane();

        gameScreen = createGameScreen();
        menuScreen = createMenuScreen();
        gameOverScreen = createGameOverScreen();

        rootPane.getChildren().addAll(gameScreen, gameOverScreen, menuScreen);

        Scene scene = new Scene(rootPane, SCREEN_WIDTH, SCREEN_HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                gameManager.paddle.setDx(-5); // Di chuyển trái
            } else if (event.getCode() == KeyCode.RIGHT) {
                gameManager.paddle.setDx(5); // Di chuyển phải
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                gameManager.paddle.setDx(0); // Dừng lại
            }
        });

        // --- Cài đặt Game Loop ---
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Logic game loop
                if (gameManager.gameState.equals("Đang chơi")) {
                    updateGame();
                    renderGame(gc);
                } else if (gameManager.gameState.equals("Thua") || gameManager.gameState.equals("Thắng")) {
                    gameLoop.stop();
                    showGameOverScreen();
                }
            }
        };

        showMenuScreen();

        primaryStage.setTitle("OOP Game - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Pane createGameScreen() {
        Pane gamePane = new Pane();
        Canvas gameCanvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D(); // gc là biến global
        gamePane.getChildren().add(gameCanvas);
        return gamePane;
    }

    private VBox createMenuScreen() {
        VBox menuBox = new VBox(20); // Spacing 20
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: #222;");

        Label title = new Label("ArkanoidGame");
        title.setFont(new Font("Calibri Light", 40));
        title.setTextFill(Color.WHITE);

        Button startButton = new Button("Bắt đầu");
        startButton.setFont(new Font("Calibri Light", 20));
        startButton.setOnAction(_ -> showGameScreen());

        menuBox.getChildren().addAll(title, startButton);
        return menuBox;
    }


    private VBox createGameOverScreen() {
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setStyle("-fx-background-color: #222;");

        gameOverLabel = new Label();
        gameOverLabel.setFont(new Font("Calibri Light", 30));

        Button playAgainButton = new Button("Chơi lại");
        playAgainButton.setFont(new Font("Calibri Light", 20));
        playAgainButton.setOnAction(_ -> showGameScreen());

        gameOverBox.getChildren().addAll(gameOverLabel, playAgainButton);
        return gameOverBox;
    }

    private void showMenuScreen() {
        gameManager.gameState = "Menu";
        menuScreen.setVisible(true);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(false);
        menuScreen.toFront();
    }


    private void showGameOverScreen() {
        if (gameManager.gameState.equals("Thua")) {
            gameOverLabel.setText(" GAME OVER!\nFinal Score: " + gameManager.score);
            gameOverLabel.setTextFill(Color.WHITE);
            soundManager.playGameOver();
        } else { // Thắng
            gameOverLabel.setText(" YOU WIN!\nFinal Score: " + gameManager.score);
            gameOverLabel.setTextFill(Color.WHITE);
        }

        menuScreen.setVisible(false);
        gameOverScreen.setVisible(true);
        gameScreen.setVisible(false);
        gameOverScreen.toFront();
    }


    private void showGameScreen() {
        resetGame();
        menuScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(true);
        gameScreen.toFront();

        gameLoop.start();
        soundManager.playOpening();
    }

    private void resetGame() {
        // Reset trạng thái game
        gameManager.score = 0;
        gameManager.lives = 3;
        gameManager.gameState = "Đang chơi";

        //xóa các obj
        gameManager.bricks.clear();
        gameManager.fallingPowerUps.clear();
        gameManager.activeEffects.clear();

        // tạo new obj
        setupGameObjects();
    }

    private void setupGameObjects() {
        int paddleWidth = 100;
        int paddleHeight = 20;
        double paddleX = (SCREEN_WIDTH - paddleWidth) / 2.0;
        double paddleY = SCREEN_HEIGHT - paddleHeight - 30;
        gameManager.paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight,100);

        int ballRadius = 10;
        double ballX = SCREEN_WIDTH / 2.0 - ballRadius;
        double ballY = SCREEN_HEIGHT / 2.0 - ballRadius;
        double vecDir = 1 / Math.sqrt(2);
        gameManager.ball = new Ball(ballX, ballY, ballRadius * 2, ballRadius * 2, 5.0, vecDir, -vecDir, ballRadius);

        int rows = 5;
        int cols = 10;
        int brickWidth = 70;
        int brickHeight = 20;
        double startX = 35;
        double startY = 50;
        double gapX = 3;
        double gapY = 3;

        Level.generateLevel1(gameManager, rows, cols, brickWidth, brickHeight,startX, startY, gapX, gapY);

    }

    private void updateGame() {
        gameManager.paddle.update();
        gameManager.ball.update();

        if (gameManager.paddle.getX() < 0) {
            gameManager.paddle.setX(0);
        }
        if (gameManager.paddle.getX() + gameManager.paddle.getWidth() > SCREEN_WIDTH) {
            gameManager.paddle.setX(SCREEN_WIDTH - gameManager.paddle.getWidth());
        }

        CheckCollision.checkWallCollision(gameManager.ball, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (CheckCollision.intersects(gameManager.ball, gameManager.paddle)) {
            CheckCollision.bounceOff(gameManager.ball, gameManager.paddle);
            soundManager.playPaddleHit();
        }

        Iterator<Brick> brickIterator = gameManager.bricks.iterator();
        while (brickIterator.hasNext()) {
            Brick brick = brickIterator.next();
            if (CheckCollision.intersects(gameManager.ball, brick)) {
                CheckCollision.bounceOff(gameManager.ball, brick);

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
                        gameManager.fallingPowerUps.add(new ExpandPaddlePowerUp(pX, pY));
                    }
                    brickIterator.remove();
                    gameManager.score += 10;
                    break;
                }
            }
        }

        if (gameManager.bricks.isEmpty()) {
            gameManager.gameState = "Thắng";
        }

        Iterator<PowerUp> powerUpIterator = gameManager.fallingPowerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            powerUp.update();

            if (powerUp.getY() > SCREEN_HEIGHT) {
                powerUpIterator.remove();
                continue;
            }

            if (CheckCollision.intersects(powerUp, gameManager.paddle)) {
                for (PowerUp oldEffect : gameManager.activeEffects) {
                    oldEffect.removeEffect(gameManager.paddle, gameManager.ball);
                }
                gameManager.activeEffects.clear();

                powerUp.applyEffect(gameManager.paddle, gameManager.ball);
                gameManager.activeEffects.add(powerUp);
                gameManager.effectStartTime = System.currentTimeMillis();

                powerUpIterator.remove();
            }
        }

        if (!gameManager.activeEffects.isEmpty()) {
            PowerUp currentEffect = gameManager.activeEffects.getFirst();
            long elapsedTime = System.currentTimeMillis() - gameManager.effectStartTime;

            if (elapsedTime > currentEffect.getDuration()) {
                currentEffect.removeEffect(gameManager.paddle, gameManager.ball);
                gameManager.activeEffects.remove(currentEffect);
            }
        }

        if (!gameManager.ball.alive) {
            gameManager.lives--;
            if (gameManager.lives <= 0) {
                gameManager.gameOver();
            } else {
                double paddleCenterX = gameManager.paddle.getX() + gameManager.paddle.getWidth() / 2.0;
                double newBallX = paddleCenterX - gameManager.ball.getWidth() / 2.0;
                double newBallY = gameManager.paddle.getY() - gameManager.ball.getHeight() - 5;

                gameManager.ball.setX(newBallX);
                gameManager.ball.setY(newBallY);

                double initialDir = 1 / Math.sqrt(2);
                gameManager.ball.directionX = initialDir;
                gameManager.ball.directionY = -initialDir;
                gameManager.ball.updateVelocity();
                gameManager.ball.alive = true;
            }
        }
    }

    private void renderGame(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        gc.setFill(Color.BLUE);
        gc.fillRect(gameManager.paddle.getX(), gameManager.paddle.getY(), gameManager.paddle.getWidth(), gameManager.paddle.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillOval(gameManager.ball.getX(), gameManager.ball.getY(), gameManager.ball.getWidth(), gameManager.ball.getHeight());

        for (Brick brick : gameManager.bricks) {
            brick.render(gc);
        }
        gc.setFill(Color.WHITE);
        for (PowerUp powerUp : gameManager.fallingPowerUps) {
            gc.fillOval(powerUp.getX(), powerUp.getY(), 10, 10);
        }

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + gameManager.score, 10, 20);
        gc.fillText("Lives: " + gameManager.lives, SCREEN_WIDTH - 60, 20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}