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

import java.util.Iterator;

public class Main1 extends Application {

    private GameManager gameManager;


    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager();

        setupGameObjects();

        Pane root = new Pane();
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D(); // Lấy "bút vẽ"
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

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


        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (gameManager.gameState.equals("Đang chơi")) {

                    updateGame();


                    renderGame(gc);
                } else if (gameManager.gameState.equals("Thua")) {

                    gc.setFill(Color.RED);
                    gc.fillText("GAME OVER! Final Score: " + gameManager.score, SCREEN_WIDTH / 2.0 - 50, SCREEN_HEIGHT / 2.0);
                    this.stop();
                }
            }
        };
        gameLoop.start();

        primaryStage.setTitle("OOP Game - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
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


        int brickWidth = 70;
        int brickHeight = 20;
        int rows = 5;
        int cols = 9;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * (brickWidth + 10) + 35;
                double y = i * (brickHeight + 10) + 50;
                gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, 1, "normal"));
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