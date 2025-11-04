package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Background;
import javafx.scene.image.Image;

public class Main1 extends Application {
    // --- Managers ---
    public GameManager gameManager;
    public SoundManager soundManager;
    public HighScoreManager highScoreManager;
    public GameRenderer gameRenderer;

    // --- Screens ---
    public Node gameScreen;
    public VBox menuScreen;
    public VBox highScoreScreen;
    public StackPane gameOverScreen;
    public VBox pauseMenu;
    public VBox startModeSelectScreen;
    public VBox continueModeSelectScreen;
    public VBox highScoreModeSelectScreen;
    // ---------------------------------

    // --- Components ---
    private ScreenManager screenManager;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    public AnimationTimer gameLoop;
    private Button saveButton;
    private Label saveStatusLabel;
    private Image gameOverBackgroundImage;

    private Font pixelFont;


    @Override
    public void start(Stage primaryStage) {

        // 1. Khởi tạo Managers
        soundManager = new SoundManager();
        gameManager = new GameManager();
        highScoreManager = new HighScoreManager();

        // 2. Tải Font
        try {
            String fontPath = "/com/mygame/mygamearkanoid/fonts/VT323-Regular.ttf";
            InputStream fontStream = getClass().getResourceAsStream(fontPath);
            if (fontStream == null) throw new Exception("Không tìm thấy font");
            pixelFont = Font.loadFont(fontStream, 24);
            if (pixelFont == null) throw new Exception("Không thể tải font");
        } catch (Exception e) {
            System.err.println("Lỗi khi tải font: " + e.getMessage());
            pixelFont = Font.font("Consolas", 24);
        }

        // 3. Khởi tạo ScreenManager
        screenManager = new ScreenManager(this, gameManager, highScoreManager, pixelFont);

        // 4. Yêu cầu Factory tải ảnh nền
        screenManager.loadAllBackgroundImages();

        // 5. Yêu cầu Factory tạo các màn hình
        gameScreen = screenManager.createGameScreen();
        menuScreen = screenManager.createMenuScreen();
        gameOverScreen = screenManager.createGameOverScreen();
        highScoreScreen = screenManager.createHighScoreScreen();
        pauseMenu = screenManager.createPauseMenu();
        pauseMenu.setVisible(false);
        // --- Tạo 3 màn hình mới ---
        startModeSelectScreen = screenManager.createStartModeSelectScreen();
        startModeSelectScreen.setVisible(false);
        continueModeSelectScreen = screenManager.createContinueModeSelectScreen();
        continueModeSelectScreen.setVisible(false);
        highScoreModeSelectScreen = screenManager.createHighScoreModeSelectScreen();
        highScoreModeSelectScreen.setVisible(false);
        // --------------------------------

        // 6. Lấy Canvas và GC
        gameCanvas = screenManager.getGameCanvas();
        gc = screenManager.getGc();

        // 7. Lấy Label và Button
        saveButton = screenManager.getSaveButton();
        saveStatusLabel = screenManager.getSaveStatusLabel();

        // 8. Khởi tạo Renderer
        gameRenderer = new GameRenderer(gc, screenManager.getGameBackgroundImage());

        // 9. Set up Root Pane
        StackPane rootPane = new StackPane();
        // --- Thêm 3 màn hình mới ---
        rootPane.getChildren().addAll(
                gameScreen, gameOverScreen, highScoreScreen, menuScreen,
                pauseMenu, startModeSelectScreen, continueModeSelectScreen, highScoreModeSelectScreen
        );
        // --------------------------------

        // 10. Set up Scene và Key Handlers
        Scene scene = new Scene(rootPane, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);

        scene.setOnKeyPressed(event -> {
            if (gameManager.gameState == GameState.PLAYING && gameManager.paddle != null) {
                if (event.getCode() == KeyCode.LEFT) {
                    gameManager.paddle.setDx(-5);
                } else if (event.getCode() == KeyCode.RIGHT) {
                    gameManager.paddle.setDx(5);
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            if (gameManager.gameState == GameState.PLAYING && gameManager.paddle != null) {
                if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                    gameManager.paddle.setDx(0);
                }
            }
        });

        // 11. Set up Game Loop
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameManager.gameState == GameState.PLAYING) {
                    gameManager.updateGame(soundManager);
                    gameRenderer.render(gameManager);
                } else if (gameManager.gameState == GameState.GAME_OVER || gameManager.gameState == GameState.VICTORY) {
                    gameLoop.stop();
                    showGameOverScreen();
                }
            }
        };

        // 12. Bắt đầu
        showMenuScreen();
        primaryStage.setTitle(StaticFinal.GAME_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Logic save khi thoát (đã sửa)
        if (gameManager != null && gameManager.gameState == GameState.PLAYING) {
            try {
                gameManager.saveGame(); // Tự save (nó biết chế độ)
                System.out.println("Đã tự động lưu game.");
            } catch (IOException e) {
                System.err.println("Lỗi khi tự động lưu game: " + e.getMessage());
            }
        }
    }

    // --- CÁC HÀM QUẢN LÝ (GỌI TỪ FACTORY) ---

    public void pauseGame() {
        gameLoop.stop();
        gameManager.gameState = GameState.MENU;
        gameScreen.setEffect(new BoxBlur(10, 10, 3));

        if(saveStatusLabel != null) {
            saveStatusLabel.setText("");
        }

        // Luôn hiện nút Save
        if (saveButton != null) {
            saveButton.setVisible(true);
        }

        pauseMenu.setVisible(true);
        pauseMenu.toFront();
    }

    public void resumeGame() {
        pauseMenu.setVisible(false);
        gameScreen.setEffect(null);
        gameManager.gameState = GameState.PLAYING;
        gameLoop.start();
    }


    // Hàm xóa file theo mode
    public void deleteSaveFile(GameMode mode) {
        String filename = (mode == GameMode.CLASSIC)
                ? StaticFinal.CLASSIC_SAVE_FILE
                : StaticFinal.ADVENTURE_SAVE_FILE;

        File autoSaveFile = new File(filename);
        if (autoSaveFile.exists()) {
            if (autoSaveFile.delete()) {
                System.out.println("Đã xoá file save: " + filename);
            }
        }
    }

    // --- CÁC HÀM CHUYỂN MÀN HÌNH ---

    // Ẩn tất cả các màn hình
    private void hideAllScreens() {
        menuScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScreen.setVisible(false);
        highScoreScreen.setVisible(false);
        pauseMenu.setVisible(false);
        startModeSelectScreen.setVisible(false);
        continueModeSelectScreen.setVisible(false);
        highScoreModeSelectScreen.setVisible(false);
    }

    public void showMenuScreen() {
        gameLoop.stop();
        gameManager.gameState = GameState.MENU;
        hideAllScreens();
        menuScreen.setVisible(true);
        menuScreen.toFront();
    }

    public void showStartModeSelectScreen() {
        gameLoop.stop();
        gameManager.gameState = GameState.MENU;
        hideAllScreens();
        startModeSelectScreen.setVisible(true);
        startModeSelectScreen.toFront();
    }

    public void showContinueModeSelectScreen() {
        gameLoop.stop();
        gameManager.gameState = GameState.MENU;

        // LOGIC CHÍNH (VÔ HIỆU HÓA NÚT)
        File classicSave = new File(StaticFinal.CLASSIC_SAVE_FILE);
        File adventureSave = new File(StaticFinal.ADVENTURE_SAVE_FILE);

        screenManager.getContinueClassicButton().setDisable(!classicSave.exists());
        screenManager.getContinueAdventureButton().setDisable(!adventureSave.exists());

        hideAllScreens();
        continueModeSelectScreen.setVisible(true);
        continueModeSelectScreen.toFront();
    }

    public void showHighScoreModeSelectScreen() {
        gameLoop.stop();
        gameManager.gameState = GameState.MENU;
        hideAllScreens();
        highScoreModeSelectScreen.setVisible(true);
        highScoreModeSelectScreen.toFront();
    }

    public void showGameOverScreen() {
        if (gameManager.gameState != GameState.VICTORY) {
            soundManager.playGameOver();
        }

        // Thêm high score theo mode (ĐÚNG)
        highScoreManager.addScore(gameManager.score, gameManager.gameMode);
        // Xóa file save của mode vừa chơi (ĐÚNG)
        deleteSaveFile(gameManager.gameMode);

        hideAllScreens();
        gameOverScreen.setVisible(true);
        gameOverScreen.toFront();
    }

    public void showGameScreen(boolean skipReset, GameMode mode) {
        if (!skipReset) {
            gameManager.resetGame(mode);
        }

        gameManager.gameState = GameState.PLAYING;
        hideAllScreens();
        gameScreen.setVisible(true);
        gameScreen.toFront();
        gameScreen.setEffect(null);

        gameLoop.start();
        gameScreen.requestFocus();

        if (!skipReset) {
            soundManager.stopAllSounds();
            soundManager.playOpening();
        }
    }

    public void showHighScoreScreen(GameMode mode) {
        screenManager.updateHighScoreDisplay(mode); // Cập nhật theo mode
        gameLoop.stop();
        gameManager.gameState = GameState.HIGH_SCORE;
        hideAllScreens();
        highScoreScreen.setVisible(true);
        highScoreScreen.toFront();
    }

    public static void main(String[] args) {
        launch(args);
    }
}