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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.text.Font;

import javafx.scene.image.Image;
import java.net.URL;
import java.util.Iterator;

// --- CÁC IMPORT MỚI ĐỂ SET BACKGROUND BẰNG CODE ---
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
// --------------------------------------------------

public class Main1 extends Application {

    private GameManager gameManager;
    private SoundManager soundManager;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private Pane gameScreen;
    private VBox menuScreen;
    private VBox gameOverScreen;
    private Label gameOverLabel;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    // --- BIẾN CHO TẤT CẢ ẢNH NỀN ---
    private Image gameBackgroundImage;
    private Image menuBackgroundImage;  // <-- THÊM CÁI NÀY
    private Image gameOverBackgroundImage; // <-- THÊM CÁI NÀY

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager();
        soundManager = new SoundManager();

        StackPane rootPane = new StackPane();

        // 1. Tải tất cả ảnh nền
        loadAllBackgroundImages();

        // 2. Tạo màn hình game (Canvas)
        gameScreen = createGameScreen();

        // 3. Tạo các màn hình còn lại (VBox)
        menuScreen = createMenuScreen();
        gameOverScreen = createGameOverScreen();

        // 4. Thêm tất cả vào rootPane
        rootPane.getChildren().addAll(gameScreen, gameOverScreen, menuScreen);

        Scene scene = new Scene(rootPane, SCREEN_WIDTH, SCREEN_HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                gameManager.paddle.setDx(-5);
            } else if (event.getCode() == KeyCode.RIGHT) {
                gameManager.paddle.setDx(5);
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                gameManager.paddle.setDx(0);
            }
        });

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
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

    /**
     * Hàm mới: Tải tất cả ảnh nền ở một nơi
     */
    private void loadAllBackgroundImages() {
        String imagePathGame = "/com/mygame/mygamearkanoid/images/BackgroundGame.png";
        String imagePathMenu = "/com/mygame/mygamearkanoid/images/StartGame1.png";
        String imagePathGameOver = "/com/mygame/mygamearkanoid/images/GameOver.png";

        try {
            gameBackgroundImage = loadImage(imagePathGame);
            menuBackgroundImage = loadImage(imagePathMenu);

            gameOverBackgroundImage = loadImage(imagePathGameOver);

        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi tải ảnh: " + e.getMessage());
            e.printStackTrace();
        }

        if (menuBackgroundImage == null) {
            System.err.println("Không thể tải ảnh nền Menu: " + imagePathMenu);
        }
        if (gameOverBackgroundImage == null) {
            System.err.println("Không thể tải ảnh nền Game Over: " + imagePathGameOver);
        }
    }

    /**
     * Hàm trợ giúp tải ảnh (giống trong file Main.java của bạn)
     */
    private Image loadImage(String path) {
        URL url = getClass().getResource(path);
        if (url != null) {
            Image img = new Image(url.toExternalForm());
            if (img.isError()) {
                System.err.println("Lỗi khi tải ảnh. Tệp có thể bị hỏng: " + url.toExternalForm());
                return null;
            }
            return img;
        } else {
            System.err.println("KHÔNG THỂ TÌM THẤY TỆP: " + path);
            return null; // Không tìm thấy tệp
        }
    }

    /**
     * Hàm trợ giúp tạo Background (giống trong file Main.java của bạn)
     */
    private Background createBackgroundImage(Image image) {
        BackgroundImage bgImg = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                // Đặt kích thước ảnh nền bằng kích thước màn hình
                new BackgroundSize(SCREEN_WIDTH, SCREEN_HEIGHT, false, false, false, false)
        );
        return new Background(bgImg);
    }


    private Pane createGameScreen() {
        Pane gamePane = new Pane();
        Canvas gameCanvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        gamePane.getChildren().add(gameCanvas);
        return gamePane;
    }

    // --- ĐÃ SỬA LẠI HÀM NÀY ---
    private VBox createMenuScreen() {
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);

        // --- XÓA DÒNG setStyle(...) ĐI ---
        // menuBox.setStyle( ... );

        // --- THAY BẰNG CODE JAVA NÀY ---
        if (menuBackgroundImage != null) {
            menuBox.setBackground(createBackgroundImage(menuBackgroundImage));
        } else {
            menuBox.setStyle("-fx-background-color: black;"); // Nền đen dự phòng
        }
        // --------------------------------

        // Thêm lại các nút bấm (như tôi đã nói ở câu trả lời trước)
        Label title = new Label("Arkanoid Game");
        title.setFont(new Font("Calibri Light", 40));
        title.setTextFill(Color.WHITE);

        Button startButton = new Button("Bắt đầu");
        startButton.setFont(new Font("Calibri Light", 20));
        startButton.setOnAction(_ -> showGameScreen());

        menuBox.getChildren().addAll(title, startButton);
        return menuBox;
    }

    // --- ĐÃ SỬA LẠI HÀM NÀY ---
    private VBox createGameOverScreen() {
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);

        // --- XÓA DÒNG setStyle(...) ĐI ---
        // gameOverBox.setStyle( ... );

        // --- THAY BẰNG CODE JAVA NÀY ---
        // Chúng ta sẽ set nền trong hàm showGameOverScreen()
        // để nó luôn cập nhật đúng
        gameOverBox.setStyle("-fx-background-color: transparent;");
        // --------------------------------

        gameOverLabel = new Label();
        gameOverLabel.setFont(new Font("Calibri Light", 30));

        Button playAgainButton = new Button("Chơi lại");
        playAgainButton.setFont(new Font("Calibri Light", 20));
        playAgainButton.setOnAction(_ -> showGameScreen());

        gameOverBox.getChildren().addAll(gameOverLabel, playAgainButton);
        return gameOverBox;
    }

    // (Hàm showMenuScreen không đổi)
    private void showMenuScreen() {
        gameManager.gameState = "Menu";
        menuScreen.setVisible(true);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(false);
        menuScreen.toFront();
    }

    // --- ĐÃ SỬA LẠI HÀM NÀY ---
    private void showGameOverScreen() {
        // Đặt ảnh nền MỖI KHI HIỂN THỊ
        if (gameOverBackgroundImage != null) {
            gameOverScreen.setBackground(createBackgroundImage(gameOverBackgroundImage));
        } else {
            gameOverScreen.setStyle("-fx-background-color: #222;"); // Nền dự phòng
        }

        // (Phần còn lại giữ nguyên)
        if (gameManager.gameState.equals("Thua")) {
            gameOverLabel.setText(" GAME OVER!\nFinal Score: " + gameManager.score);
            gameOverLabel.setTextFill(Color.WHITE);
            soundManager.playGameOver();
        } else { // Thắng
            gameOverLabel.setText(" YOU WIN!\nFinal Score: " + gameManager.score);
            gameOverLabel.setTextFill(Color.CYAN);
        }

        menuScreen.setVisible(false);
        gameOverScreen.setVisible(true);
        gameScreen.setVisible(false);
        gameOverScreen.toFront();
    }

    // --- ĐÃ SỬA LẠI HÀM NÀY ---
    private void showGameScreen() {
        resetGame();
        menuScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(true);
        gameScreen.toFront();

        // --- THÊM DÒNG NÀY ---
        // Xóa nền của màn hình game over đi
        // để nó không bị "rò rỉ" qua màn hình game
        gameOverScreen.setBackground(null);
        // ----------------------

        gameLoop.start();
        soundManager.playOpening();
    }

    // (Hàm resetGame không đổi)
    private void resetGame() {
        gameManager.score = 0;
        gameManager.lives = 3;
        gameManager.gameState = "Đang chơi";
        gameManager.bricks.clear();
        gameManager.fallingPowerUps.clear();
        gameManager.activeEffects.clear();
        setupGameObjects();
    }

    // (Hàm setupGameObjects không đổi)
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

    // (Hàm updateGame không đổi)
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


    // (Hàm renderGame không đổi)
    private void renderGame(GraphicsContext gc) {
        // 1. Vẽ nền game
        if (gameBackgroundImage != null) {
            gc.drawImage(gameBackgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        } else {
            // Nền dự phòng
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        // 2. Vẽ Paddle (từ ảnh)
        gameManager.paddle.render(gc);

        // 3. Vẽ Ball (từ ảnh)
        gameManager.ball.render(gc);

        // 4. Vẽ Bricks (từ ảnh)
        for (Brick brick : gameManager.bricks) {
            brick.render(gc);
        }

        // 5. Vẽ Powerups (vẫn là hình tròn)
        gc.setFill(Color.WHITE);
        for (PowerUp powerUp : gameManager.fallingPowerUps) {
            gc.fillOval(powerUp.getX(), powerUp.getY(), 10, 10);
        }

        // 6. Vẽ UI
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + gameManager.score, 10, 20);
        gc.fillText("Lives: " + gameManager.lives, SCREEN_WIDTH - 60, 20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}