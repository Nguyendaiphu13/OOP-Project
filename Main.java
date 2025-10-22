

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

import java.util.Iterator;

public class Main extends Application {

    private GameManager gameManager;


    private static final int SCREEN_WIDTH = 840;
    private static final int SCREEN_HEIGHT = 600;
    private StackPane rootPane;     // Pane gốc chứa mọi thứ
    private Canvas gameCanvas;      // Canvas để vẽ game
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager();


        rootPane = new StackPane();
        gameCanvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        Pane menuPane = createMenuPane();

        // 3. Thêm menuPane vào rootPane ban đầu
        rootPane.getChildren().add(menuPane);

        // 4. Khởi tạo vòng lặp game (nhưng chưa chạy)
        setupGameLoop();

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
        startButton.setStyle("-fx-font-size: 24px;");

        // Thiết lập hành động khi nhấn nút
        startButton.setOnAction(e -> {
            // 1. Chuẩn bị các đối tượng game
            setupGameObjects();

            // 2. Báo cho GameManager biết game đã bắt đầu
            gameManager.startGame();

            // 3. Chuyển màn hình: Xóa Menu và thêm Canvas Game
            rootPane.getChildren().clear(); // Xóa menuPane
            rootPane.getChildren().add(gameCanvas); // Thêm gameCanvas

            // 4. Khởi động vòng lặp game
            gameLoop.start();
        });

        // VBox để chứa nút và căn giữa
        VBox menuLayout = new VBox(20, startButton);
        menuLayout.setAlignment(Pos.CENTER);

        // StackPane làm nền cho menu
        StackPane menuPane = new StackPane(menuLayout);
        menuPane.setStyle("-fx-background-color: black;"); // Nền đen

        return menuPane;
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
                    gc.setFill(Color.RED);
                    gc.setFont(new javafx.scene.text.Font("Arial", 30));
                    gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                    gc.fillText("GAME OVER! Final Score: " + gameManager.score, SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0);
                    gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
                    this.stop();
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