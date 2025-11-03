
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

import javafx.geometry.Insets;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.Iterator;

// --- CÁC IMPORT MỚI ĐỂ SET BACKGROUND BẰNG CODE ---
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
// --------------------------------------------------

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main extends Application {

    private GameManager gameManager;
    private SoundManager soundManager;
    private HighScoreManager highScoreManager;

    private static final String AUTO_SAVE_FILE = "autoSaveFile";

    private static final int SCREEN_WIDTH = 900;
    private static final int SCREEN_HEIGHT = 750;

    private Pane gameScreen;
    private VBox menuScreen;
    private VBox highScoreScreen;
    private VBox gameOverScreen;
    private Label gameOverLabel;
    private VBox highScoreDisplayList;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private Font pixelFont;

    // --- BIẾN CHO TẤT CẢ ẢNH NỀN ---
    private Image gameBackgroundImage;
    private Image menuBackgroundImage;  // <-- THÊM CÁI NÀY
    private Image gameOverBackgroundImage; // <-- THÊM CÁI NÀY
    private Image highScoreBackgroundImage;

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager();
        soundManager = new SoundManager();
        highScoreManager = new HighScoreManager();

        try {
            // Đường dẫn bắt đầu từ thư mục "resources"
            String fontPath = "Fonts/VT323-Regular.ttf";
            pixelFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 20); // 20 là kích cỡ mặc định

            if (pixelFont == null) {
                System.err.println("Không thể tải font! Dùng font mặc định.");
                pixelFont = Font.font("Consolas", 20); // Font dự phòng
            } else {
                System.out.println("Đã tải font thành công.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải font: " + e.getMessage());
            pixelFont = Font.font("Consolas", 20); // Font dự phòng
        }

        StackPane rootPane = new StackPane();

        // 1. Tải tất cả ảnh nền
        loadAllBackgroundImages();

        // 2. Tạo màn hình game (Canvas)
        gameScreen = createGameScreen();

        // 3. Tạo các màn hình còn lại (VBox)
        menuScreen = createMenuScreen();
        gameOverScreen = createGameOverScreen();
        highScoreScreen = createHighScoreScreen();

        // 4. Thêm tất cả vào rootPane
        rootPane.getChildren().addAll(gameScreen, gameOverScreen,highScoreScreen, menuScreen);

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

        // --- LOGIC AUTO-LOAD KHI KHỞI ĐỘNG ---
        File autoSaveFile = new File(AUTO_SAVE_FILE);
        if (autoSaveFile.exists()) {
            try {
                // Thử tải game từ file
                gameManager.loadGame(AUTO_SAVE_FILE);
                System.out.println("Tải game từ auto-save thành công!");
                // Vào thẳng màn hình game
                showGameScreen(true); // true = bỏ qua resetGame()
            } catch (Exception e) {
                // Nếu lỗi, cứ vào menu
                System.err.println("Lỗi tải auto-save, bắt đầu game mới.");
                e.printStackTrace();
                showMenuScreen(); // Vào menu như bình thường
            }
        } else {
            // Không có file save, vào menu
            showMenuScreen();
        }

        primaryStage.setTitle("OOP Game - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Tự động lưu game nếu đang chơi
        if (gameManager != null && gameManager.gameState.equals("Đang chơi")) {
            try {
                gameManager.saveGame(AUTO_SAVE_FILE);
                System.out.println("Đã tự động lưu game.");
            } catch (IOException e) {
                System.err.println("Lỗi khi tự động lưu game: " + e.getMessage());
            }
        }
    }

    /**
     * Hàm mới: Tải tất cả ảnh nền ở một nơi
     */
    private void loadAllBackgroundImages() {
        String imagePathGame = "Graphics/BackgroundGame10.png";
        String imagePathMenu = "Graphics/StartGame10.png";
        String imagePathGameOver = "Graphics/GameOver10.png";
        String imagePathHighScore = "Graphics/HighScores10.png";

        try {
            gameBackgroundImage = loadImage(imagePathGame);
            menuBackgroundImage = loadImage(imagePathMenu);
            highScoreBackgroundImage = loadImage(imagePathHighScore);
            gameOverBackgroundImage = loadImage(imagePathGameOver);

        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi tải ảnh: " + e.getMessage());
        }

        if (menuBackgroundImage == null) {
            System.err.println("Không thể tải ảnh nền Menu: " + imagePathMenu);
        }
        if (highScoreBackgroundImage == null) {
            System.err.println("Không thể tải ảnh nền High Score: " + imagePathHighScore);
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



        Image imgStart = loadImage("Graphics/StartButton.png");
        ImageView viewStart = new ImageView(imgStart);
        Button startButton = new Button(); // Tạo nút không có chữ
        startButton.setGraphic(viewStart); // Đặt ảnh làm nội dung
        startButton.setOnAction(_ -> showGameScreen(false));
        startButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        startButton.setOnMouseEntered(e -> {
            viewStart.setOpacity(0.9);

        });
        startButton.setOnMouseExited(e -> {
            viewStart.setOpacity(1.0);
        });
        startButton.setOnMousePressed(e -> {
            startButton.setScaleX(0.95);
            startButton.setScaleY(0.95);
        });
        startButton.setOnMouseReleased(e -> {
            startButton.setScaleX(1.0);
            startButton.setScaleY(1.0);
        });

        Image imgHighScore = loadImage("Graphics/Highscore.png");
        ImageView viewHighScore = new ImageView(imgHighScore);
        Button highScoreButton = new Button();
        highScoreButton.setGraphic(viewHighScore);
        highScoreButton.setOnAction(_ -> showHighScoreScreen());
        highScoreButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        highScoreButton.setOnMouseEntered(e -> {
            viewHighScore.setOpacity(0.9);

        });
        highScoreButton.setOnMouseExited(e -> {
            viewHighScore.setOpacity(1.0);
        });
        highScoreButton.setOnMousePressed(e -> {
            viewHighScore.setScaleX(0.95);
            viewHighScore.setScaleY(0.95);
        });
        highScoreButton.setOnMouseReleased(e -> {
            viewHighScore.setScaleX(1.0);
            viewHighScore.setScaleY(1.0);
        });

        menuBox.getChildren().addAll( startButton, highScoreButton);
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

        Image imgRestart = loadImage("Graphics/RestartButton.png");
        ImageView viewRestart = new ImageView(imgRestart);
        Button playAgainButton = new Button();
        playAgainButton.setGraphic(viewRestart);
        playAgainButton.setOnAction(_ -> showGameScreen(true));
        playAgainButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        playAgainButton.setOnMouseEntered(e -> viewRestart.setOpacity(0.9));
        playAgainButton.setOnMouseExited(e -> viewRestart.setOpacity(1.0));
        playAgainButton.setOnMousePressed(e -> playAgainButton.setScaleX(0.95));
        playAgainButton.setOnMouseReleased(e -> playAgainButton.setScaleX(1.0));
        playAgainButton.setOnMousePressed(e -> playAgainButton.setScaleY(0.95));
        playAgainButton.setOnMouseReleased(e -> playAgainButton.setScaleY(1.0));

        gameOverBox.getChildren().addAll(gameOverLabel, playAgainButton);
        return gameOverBox;
    }

    // (Hàm showMenuScreen không đổi)
    private void showMenuScreen() {
        gameManager.gameState = "Menu";
        menuScreen.setVisible(true);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(false);
        highScoreScreen.setVisible(false);
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

        highScoreManager.addScore(gameManager.score);

        File autoSaveFile = new File(AUTO_SAVE_FILE);
        if (autoSaveFile.exists()) {
            if (autoSaveFile.delete()) {
                System.out.println("Đã xoá file auto-save (vì game kết thúc).");
            }
        }

        menuScreen.setVisible(false);
        gameOverScreen.setVisible(true);
        gameScreen.setVisible(false);
        gameOverScreen.toFront();
    }

    // --- ĐÃ SỬA LẠI HÀM NÀY ---
    private void showGameScreen(boolean skipReset) {
        if (!skipReset) {
            resetGame(); // Chỉ reset nếu là game mới (bắt đầu từ menu)
        }

        // Luôn đặt trạng thái là "Đang chơi" khi vào màn hình này
        gameManager.gameState = "Đang chơi";

        menuScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(true);
        gameScreen.toFront();

        gameOverScreen.setBackground(null);

        gameLoop.start();

        // Chỉ chơi nhạc opening nếu là game mới
        if (!skipReset) {
            soundManager.playOpening();
        }
    }

    /**
     * HÀM MỚI: Tạo màn hình Bảng Xếp Hạng
     */
    private VBox createHighScoreScreen() {
        VBox highScoreBox = new VBox(30);
        highScoreBox.setAlignment(Pos.CENTER);

        // Đặt ảnh nền khủng long
        if (highScoreBackgroundImage != null) {
            highScoreBox.setBackground(createBackgroundImage(highScoreBackgroundImage));
        } else {
            // Nền dự phòng nếu ảnh lỗi
            highScoreBox.setStyle("-fx-background-color: black;");
        }

        // Tiêu đề (chúng ta sẽ ẩn nó đi vì ảnh nền đã có chữ)
        Label title = new Label("Bảng Xếp Hạng");
        title.setFont(new Font("Calibri Light", 40));
        title.setTextFill(Color.WHITE);
        title.setVisible(false); // <-- ẨN TIÊU ĐỀ

        // VBox này sẽ chứa danh sách các điểm số
        highScoreDisplayList = new VBox(10);
        highScoreDisplayList.setAlignment(Pos.CENTER);

        // Nút để quay lại menu
        Image imgMenu = loadImage("Graphics/ReturnMenu.png");
        ImageView viewMenu = new ImageView(imgMenu);
        Button menuButton = new Button();
        menuButton.setGraphic(viewMenu);
        menuButton.setOnAction(_ -> showMenuScreen());
        menuButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        menuButton.setOnMouseEntered(e -> viewMenu.setOpacity(0.9));
        menuButton.setOnMouseExited(e -> viewMenu.setOpacity(1.0));
        menuButton.setOnMousePressed(e -> menuButton.setScaleX(0.95));
        menuButton.setOnMouseReleased(e -> menuButton.setScaleX(1.0));
        menuButton.setOnMousePressed(e -> menuButton.setScaleY(0.95));
        menuButton.setOnMouseReleased(e -> menuButton.setScaleY(1.0));

        // Thêm các thành phần vào màn hình
        highScoreBox.getChildren().addAll(title, highScoreDisplayList, menuButton);
        highScoreBox.setVisible(false); // Ẩn nó đi lúc ban đầu
        return highScoreBox;
    }

    /**
     * HÀM MỚI: Hiển thị màn hình Bảng Xếp Hạng
     */
    private void showHighScoreScreen() {
        // Cập nhật danh sách điểm MỖI KHI mở
        updateHighScoreDisplay();

        gameManager.gameState = "Menu"; // Chuyển trạng thái game về Menu
        menuScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(false);
        highScoreScreen.setVisible(true); // <-- HIỆN màn hình Bảng Xếp Hạng
        highScoreScreen.toFront();
    }

    /**
     * HÀM MỚI: Cập nhật và căn chỉnh danh sách điểm
     */
    private void updateHighScoreDisplay() {
        highScoreDisplayList.getChildren().clear(); // Xóa điểm cũ

        // Lấy danh sách điểm (List<Integer>)
        List<Integer> scores = highScoreManager.getHighScores();

        // --- CĂN CHỈNH CHO KHỚP VỚI ẢNH NỀN ---

        // 1. Đẩy danh sách điểm xuống (140 pixels) để vào giữa bảng đá
        highScoreDisplayList.setPadding(new Insets(0, 0, 108, 55));

        // 2. Giảm khoảng cách giữa các dòng
        highScoreDisplayList.setSpacing(5);

        // 3. Đặt chiều rộng cố định để căn giữa
        highScoreDisplayList.setPrefWidth(300);
        highScoreDisplayList.setMaxWidth(300);
        // ----------------------------------------

        if (scores.isEmpty()) {
            Label emptyLabel = new Label("--- Chưa có điểm ---");
            emptyLabel.setFont(Font.font(pixelFont.getFamily(), 18));
            emptyLabel.setTextFill(Color.rgb(200, 200, 200));
            highScoreDisplayList.getChildren().add(emptyLabel);
        } else {
            int rank = 1;
            for (Integer score : scores) {
                // Định dạng hiển thị: " 1. 12500"
                Label scoreLabel = new Label(String.format("%2d. %d", rank, score));

                // Dùng font và màu cho khớp với ảnh
                scoreLabel.setFont(Font.font(pixelFont.getFamily(), 25));
                scoreLabel.setTextFill(Color.rgb(255, 255, 150)); // Màu vàng nhạt

                highScoreDisplayList.getChildren().add(scoreLabel);
                rank++;
            }
        }
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
        int paddleWidth = GameManager.PADDLE_WIDTH_DEFAULT;
        int paddleHeight = GameManager.PADDLE_HEIGHT_DEFAULT;
        double paddleX = (SCREEN_WIDTH - paddleWidth) / 2.0;
        double paddleY = SCREEN_HEIGHT - paddleHeight - 50;
        gameManager.paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight,100);

        int ballRadius = 10;
        double ballX = SCREEN_WIDTH / 2.0 - ballRadius;
        double ballY = SCREEN_HEIGHT / 2.0 - ballRadius;
        double vecDir = 1 / Math.sqrt(2);
        gameManager.ball = new Ball(ballX, ballY, ballRadius * 2, ballRadius * 2, 5.0, vecDir, -vecDir, ballRadius);

        int rows = 6;
        int cols = 11;
        int brickWidth = 70;
        int brickHeight = 20;
        double startX = 50;
        double startY = 90;
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