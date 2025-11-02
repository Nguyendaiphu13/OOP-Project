package org.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
//tạo menu
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.util.Iterator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;

public class Main extends Application {

    private GameManager gameManager;
    private SoundManager soundManager;
    private VBox gameOverScreen;
    private Label gameOverLabel;
    private Pane gameScreen;
    private Pane menuScreen;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private StackPane rootPane;     // Pane gốc chứa mọi thứ
    private Canvas gameCanvas;      // Canvas để vẽ game
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private Image gameBackground;

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager();

        soundManager = new SoundManager();

        URL bgUrl = getClass().getResource("Graphics/BackgroundGame.png");
        if (bgUrl != null) {
            gameBackground = new Image(bgUrl.toExternalForm());
            if (gameBackground.isError()) {
                System.err.println("Lỗi khi tải ảnh. Tệp có thể bị hỏng: " + bgUrl.toExternalForm());
                gameBackground = null;
            }
        } else {
            System.err.println("Không thể tìm thấy tệp 'Graphics/BackgroundGame.png'. Sẽ dùng nền đen.");
            gameBackground = null;
        }

        gameOverScreen = createGameOverScreen();
        rootPane = new StackPane();
        gameCanvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();

        gameScreen = new Pane(gameCanvas);
        menuScreen = createMenuPane();

        rootPane.getChildren().addAll(gameScreen, gameOverScreen, menuScreen);
        gameScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        menuScreen.toFront();
        setupGameLoop();
        primaryStage.show();


        Scene scene = new Scene(rootPane, SCREEN_WIDTH, SCREEN_HEIGHT);
        setupKeyHandlers(scene);

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



        gameLoop.start();

        primaryStage.setTitle("OOP Game - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Pane createMenuPane() {
        // Tạo nút "Bắt đầu"
        Button startButton = new Button("BẮT ĐẦU");
        startButton.setStyle("-fx-font-size: 24px; " +
                "-fx-background-color: black; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20;"
        );

        VBox menuLayout = new VBox(20, startButton);
        menuLayout.setAlignment(Pos.CENTER);

        Image bgImage = null;

        URL resourceUrl = getClass().getResource("Graphics/StartGame1.png");
        if (resourceUrl != null) {
            bgImage = new Image(resourceUrl.toExternalForm());
            if (bgImage.isError()) {
                System.err.println("Lỗi khi tải ảnh. Tệp có thể bị hỏng: " + resourceUrl.toExternalForm());
                bgImage = null; // Đặt lại là null để kích hoạt nền đen
            }
        }

        if (bgImage == null) {
            System.err.println("Không thể tải 'Background.jpg'. Đảm bảo tệp tin ở đúng vị trí. Sử dụng nền đen.");
            // Nếu có lỗi, dùng tạm nền đen
            menuLayout.setStyle("-fx-background-color: black;");

            startButton.setOnAction(e -> {
                showGameScreen();
            });

            return new StackPane(menuLayout);
        }

        ImageView backgroundView = new ImageView(bgImage);
        backgroundView.setFitWidth(SCREEN_WIDTH);
        backgroundView.setFitHeight(SCREEN_HEIGHT);

        StackPane menuPane = new StackPane();
        menuPane.getChildren().addAll(backgroundView, menuLayout);

        startButton.setOnAction(e -> {
            showGameScreen();
        });

        return menuPane;
    }

    private VBox createGameOverScreen() {
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setStyle("-fx-background-color: #222;");

        gameOverLabel = new Label();
        gameOverLabel.setFont(new Font("Calibri Light", 30));

        Button playAgainButton = new Button("Chơi lại");
        playAgainButton.setFont(new Font("Calibri Light", 20));
        playAgainButton.setOnAction(e -> showGameScreen());

        gameOverBox.getChildren().addAll(gameOverLabel, playAgainButton);
        return gameOverBox;
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

    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (gameManager.gameState.equals("Đang chơi")) {
                    updateGame();
                    renderGame(gc);
                }
                // Khi gameState là "Menu", vòng lặp này không chạy nên không làm gì cả
                else if (gameManager.gameState.equals("Thua")) {
                    // Dừng vòng lặp
                    this.stop();
                    // Gọi hàm hiển thị màn hình game over (thay vì vẽ text)
                    showGameOverScreen();
                }
            }
        };
    }

    private void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            // Chỉ di chuyển paddle khi game "Đang chơi"
            if (gameManager.gameState.equals("Đang chơi")) {
                if (event.getCode() == KeyCode.LEFT) {
                    gameManager.paddle.setDx(-5); // Di chuyển trái
                } else if (event.getCode() == KeyCode.RIGHT) {
                    gameManager.paddle.setDx(5); // Di chuyển phải
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            // Chỉ dừng paddle khi game "Đang chơi"
            if (gameManager.gameState.equals("Đang chơi")) {
                if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                    gameManager.paddle.setDx(0); // Dừng lại
                }
            }
        });
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
        gameManager.ball = new Ball(ballX, ballY, ballRadius * 2, ballRadius * 2, 5.0, vecDir, -vecDir, ballRadius); // Tốc độ 5.0


        int brickWidth = 80;
        int brickHeight = 20;
        int rows = 5;
        int cols = 10;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * (brickWidth + 5) + 0;
                double y = i * (brickHeight + 5) + 50;
                gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight,  "normal"));
            }
        }
    }


    private void updateGame() {
        gameManager.paddle.update();
        gameManager.ball.update();

        // Giữ paddle trong màn hình
        if (gameManager.paddle.getX() < 0) {
            gameManager.paddle.setX(0);
        }
        if (gameManager.paddle.getX() + gameManager.paddle.getWidth() > SCREEN_WIDTH) {
            gameManager.paddle.setX(SCREEN_WIDTH - gameManager.paddle.getWidth());
        }

        CheckCollision.checkWallCollision(gameManager.ball, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (CheckCollision.intersects(gameManager.ball, gameManager.paddle)) {
            CheckCollision.bounceOff(gameManager.ball, gameManager.paddle);
        }

        Iterator<Brick> brickIterator = gameManager.bricks.iterator();
        while (brickIterator.hasNext()) {
            Brick brick = brickIterator.next();
            if (CheckCollision.intersects(gameManager.ball, brick)) {
                CheckCollision.bounceOff(gameManager.ball, brick);

                // Xóa gạch và cộng điểm
                brickIterator.remove();
                gameManager.score += 10;

                // Thoát khỏi vòng lặp để chỉ phá 1 gạch mỗi khung hình
                break;
            }
        }

        // Xử lý khi bóng rơi ra ngoài
        if (!gameManager.ball.alive) {
            gameManager.lives--;
            if (gameManager.lives <= 0) {
                gameManager.gameOver();
            } else {
                // Reset vị trí bóng lên giữa paddle
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
        if (gameBackground != null) {
            gc.drawImage(gameBackground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }


        gc.setFill(Color.BLUE);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);


        gc.setFill(Color.BLUE);
        gc.fillRect(gameManager.paddle.getX(), gameManager.paddle.getY(), gameManager.paddle.getWidth(), gameManager.paddle.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillOval(gameManager.ball.getX(), gameManager.ball.getY(), gameManager.ball.getWidth(), gameManager.ball.getHeight());

        gc.setFill(Color.GREEN);
        for (Brick brick : gameManager.bricks) {
            gc.fillRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
        }

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + gameManager.score, 10, 20);
        gc.fillText("Lives: " + gameManager.lives, SCREEN_WIDTH - 60, 20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
