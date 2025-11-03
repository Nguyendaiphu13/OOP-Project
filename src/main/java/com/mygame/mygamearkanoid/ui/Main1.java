package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.*;
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
import java.net.URL;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main1 extends Application {

    private GameManager gameManager;
    private SoundManager soundManager;
    private HighScoreManager highScoreManager;
    private GameRenderer gameRenderer;

    private Pane gameScreen;
    private VBox menuScreen;
    private VBox highScoreScreen;
    private VBox gameOverScreen;
    private Label gameOverLabel;
    private VBox highScoreDisplayList;

    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private Font pixelFont;

    private Image gameBackgroundImage;
    private Image menuBackgroundImage;
    private Image gameOverBackgroundImage;
    private Image highScoreBackgroundImage;

    @Override
    public void start(Stage primaryStage) {
        gameManager = new GameManager();
        soundManager = new SoundManager();
        highScoreManager = new HighScoreManager();

        try {
            String fontPath = "/com/mygame/mygamearkanoid/fonts/VT323-Regular.ttf";
            pixelFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 20);
            if (pixelFont == null) {
                System.err.println("Không thể tải font! Dùng font mặc định.");
                pixelFont = Font.font("Consolas", 20);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải font: " + e.getMessage());
            pixelFont = Font.font("Consolas", 20);
        }

        StackPane rootPane = new StackPane();

        loadAllBackgroundImages();

        gameScreen = createGameScreen(); // Hàm này tạo ra gc

        gameRenderer = new GameRenderer(gc, gameBackgroundImage);

        menuScreen = createMenuScreen();
        gameOverScreen = createGameOverScreen();
        highScoreScreen = createHighScoreScreen();

        rootPane.getChildren().addAll(gameScreen, gameOverScreen,highScoreScreen, menuScreen);

        Scene scene = new Scene(rootPane, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (gameManager.paddle != null) {
                if (event.getCode() == KeyCode.LEFT) {
                    gameManager.paddle.setDx(-5);
                } else if (event.getCode() == KeyCode.RIGHT) {
                    gameManager.paddle.setDx(5);
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            if (gameManager.paddle != null) {
                if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                    gameManager.paddle.setDx(0);
                }
            }
        });

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameManager.gameState == GameState.PLAYING) {
                    gameManager.updateGame(soundManager);
                    gameRenderer.render(gameManager);
                } else if (gameManager.gameState == GameState.GAME_OVER ||
                        gameManager.gameState == GameState.VICTORY) {
                    gameLoop.stop();
                    showGameOverScreen();
                }
            }
        };

        // Bỏ logic auto-load phức tạp, chỉ cần vào menu
        // File autoSaveFile = new File(StaticFinal.AUTO_SAVE_FILE);
        // ... (phần auto-load đã bị xóa)
        showMenuScreen();


        primaryStage.setTitle(StaticFinal.GAME_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // SỬA Ở ĐÂY: Chỉ auto-save ở chế độ CLASSIC
        if (gameManager != null && gameManager.gameState == GameState.PLAYING &&
                gameManager.gameMode == GameMode.CLASSIC) { // Thêm điều kiện check
            try {
                gameManager.saveGame(StaticFinal.AUTO_SAVE_FILE);
                System.out.println("Đã tự động lưu game (Classic Mode).");
            } catch (IOException e) {
                System.err.println("Lỗi khi tự động lưu game: " + e.getMessage());
            }
        } else {
            // Nếu là Adventure mode, xóa file save cũ đi
            File autoSaveFile = new File(StaticFinal.AUTO_SAVE_FILE);
            if (autoSaveFile.exists()) {
                autoSaveFile.delete();
                System.out.println("Đã xoá auto-save (Adventure Mode/Game Over).");
            }
        }
    }

    private void loadAllBackgroundImages() {
        String imagePathGame = "/com/mygame/mygamearkanoid/images/BackgroundGame10.png";
        String imagePathMenu = "/com/mygame/mygamearkanoid/images/StartGame10.png";
        String imagePathGameOver = "/com/mygame/mygamearkanoid/images/GameOver10.png";
        String imagePathHighScore = "/com/mygame/mygamearkanoid/images/HighScores10.png";

        try {
            gameBackgroundImage = loadImage(imagePathGame);
            menuBackgroundImage = loadImage(imagePathMenu);
            highScoreBackgroundImage = loadImage(imagePathHighScore);
            gameOverBackgroundImage = loadImage(imagePathGameOver);
        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi tải ảnh: " + e.getMessage());
        }
    }

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
            return null;
        }
    }

    private Background createBackgroundImage(Image image) {
        BackgroundImage bgImg = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT, false, false, false, false)
        );
        return new Background(bgImg);
    }


    private Pane createGameScreen() {
        Pane gamePane = new Pane();
        Canvas gameCanvas = new Canvas(StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        gamePane.getChildren().add(gameCanvas);
        return gamePane;
    }

    private VBox createMenuScreen() {
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        if (menuBackgroundImage != null) {
            menuBox.setBackground(createBackgroundImage(menuBackgroundImage));
        } else {
            menuBox.setStyle("-fx-background-color: black;");
        }
        Label title = new Label("Arkanoid Game");
        title.setFont(new Font("Calibri Light", 40));
        title.setTextFill(Color.WHITE);
        Button startButton = new Button("Classic Mode");
        startButton.setFont(new Font("Calibri Light", 20));
        startButton.setOnAction(_ -> showGameScreen(false, GameMode.CLASSIC));
        Button adventureButton = new Button("Adventure Mode");
        adventureButton.setFont(new Font("Calibri Light", 20));
        adventureButton.setOnAction(_ -> showGameScreen(false, GameMode.ADVENTURE));
        Button loadButton = new Button("Load Game (Classic)");
        loadButton.setFont(new Font("Calibri Light", 20));
        loadButton.setOnAction(_ -> {
            File autoSaveFile = new File(StaticFinal.AUTO_SAVE_FILE);
            if (autoSaveFile.exists()) {
                try {
                    gameManager.loadGame(StaticFinal.AUTO_SAVE_FILE);
                    System.out.println("Tải game từ auto-save thành công!");
                    showGameScreen(true, GameMode.CLASSIC);
                } catch (Exception e) {
                    System.err.println("Lỗi tải auto-save, bắt đầu game mới.");
                    e.printStackTrace();
                    showGameScreen(false, GameMode.CLASSIC);
                }
            } else {
                showGameScreen(false, GameMode.CLASSIC);
            }
        });

        Button highScoreButton = new Button("Bảng Xếp Hạng");
        highScoreButton.setFont(new Font("Calibri Light", 20));
        highScoreButton.setOnAction(_ -> showHighScoreScreen());
        menuBox.getChildren().addAll(title, startButton, adventureButton, loadButton, highScoreButton);

        return menuBox;
    }

    private VBox createGameOverScreen() {
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setStyle("-fx-background-color: transparent;");
        gameOverLabel = new Label();
        gameOverLabel.setFont(new Font("Calibri Light", 30));

        Button playAgainButton = new Button("Chơi lại (Menu)");
        playAgainButton.setFont(new Font("Calibri Light", 20));
        // Sửa nút này: Luôn quay về Menu chính
        playAgainButton.setOnAction(_ -> showMenuScreen());

        gameOverBox.getChildren().addAll(gameOverLabel, playAgainButton);
        return gameOverBox;
    }

    private void showMenuScreen() {
        gameManager.gameState = GameState.MENU;
        menuScreen.setVisible(true);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(false);
        highScoreScreen.setVisible(false);
        menuScreen.toFront();
    }

    private void showGameOverScreen() {
        if (gameOverBackgroundImage != null) {
            gameOverScreen.setBackground(createBackgroundImage(gameOverBackgroundImage));
        } else {
            gameOverScreen.setStyle("-fx-background-color: #222;");
        }

        if (gameManager.gameState == GameState.GAME_OVER) {
            gameOverLabel.setText(" GAME OVER!\nFinal Score: " + gameManager.score);
            gameOverLabel.setTextFill(Color.WHITE);
            soundManager.playGameOver();
        } else { // Thắng
            gameOverLabel.setText(" YOU WIN!\nFinal Score: " + gameManager.score);
            gameOverLabel.setTextFill(Color.CYAN);
        }

        highScoreManager.addScore(gameManager.score);

        File autoSaveFile = new File(StaticFinal.AUTO_SAVE_FILE);
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


    private void showGameScreen(boolean skipReset, GameMode mode) {
        if (!skipReset) {
            gameManager.resetGame(mode);
        } else {
            gameManager.gameMode = GameMode.CLASSIC;
        }

        gameManager.gameState = GameState.PLAYING;

        menuScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(true);
        gameScreen.toFront();

        gameOverScreen.setBackground(null);

        gameLoop.start();

        if (!skipReset) {
            soundManager.playOpening();
        }
    }

    private VBox createHighScoreScreen() {
        VBox highScoreBox = new VBox(20);
        highScoreBox.setAlignment(Pos.CENTER);
        if (highScoreBackgroundImage != null) {
            highScoreBox.setBackground(createBackgroundImage(highScoreBackgroundImage));
        } else {
            highScoreBox.setStyle("-fx-background-color: black;");
        }
        Label title = new Label("Bảng Xếp Hạng");
        title.setFont(new Font("Calibri Light", 40));
        title.setTextFill(Color.WHITE);
        title.setVisible(false);
        highScoreDisplayList = new VBox(10);
        highScoreDisplayList.setAlignment(Pos.CENTER);
        Button backButton = new Button("Quay Lại Menu");
        backButton.setFont(new Font("Calibri Light", 20));
        backButton.setOnAction(_ -> showMenuScreen());
        highScoreBox.getChildren().addAll(title, highScoreDisplayList, backButton);
        highScoreBox.setVisible(false);
        return highScoreBox;
    }

    private void showHighScoreScreen() {
        updateHighScoreDisplay();

        gameManager.gameState = GameState.HIGH_SCORE;
        menuScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(false);
        highScoreScreen.setVisible(true);
        highScoreScreen.toFront();
    }

    private void updateHighScoreDisplay() {
        highScoreDisplayList.getChildren().clear();

        List<Integer> scores = highScoreManager.getHighScores();

        highScoreDisplayList.setPadding(new Insets(0, 0, 108, 55));
        highScoreDisplayList.setSpacing(5);
        highScoreDisplayList.setPrefWidth(300);
        highScoreDisplayList.setMaxWidth(300);

        if (scores.isEmpty()) {
            Label emptyLabel = new Label("--- Chưa có điểm ---");
            emptyLabel.setFont(Font.font(pixelFont.getFamily(), 18));
            emptyLabel.setTextFill(Color.rgb(200, 200, 200));
            highScoreDisplayList.getChildren().add(emptyLabel);
        } else {
            int rank = 1;
            for (Integer score : scores) {
                Label scoreLabel = new Label(String.format("%2d. %d", rank, score));
                scoreLabel.setFont(Font.font(pixelFont.getFamily(), 27));
                scoreLabel.setTextFill(Color.rgb(255, 255, 150));
                highScoreDisplayList.getChildren().add(scoreLabel);
                rank++;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
