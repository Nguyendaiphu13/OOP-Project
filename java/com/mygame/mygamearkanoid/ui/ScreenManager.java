package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.List;

public class ScreenManager {

    // --- Fields ---
    private Main1 mainApp;
    private GameManager gameManager;
    private HighScoreManager highScoreManager;
    private Font pixelFont;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private VBox highScoreDisplayList;
    private Label gameOverLabel;
    private Image gameBackgroundImage;
    private Image menuBackgroundImage;
    private Image gameOverBackgroundImage;
    private Image highScoreBackgroundImage;
    private Image modeSelectBackgroundImage;
    private Button saveButton;
    private Label saveStatusLabel;
    private Button continueClassicButton;
    private Button continueAdventureButton;

    public ScreenManager(Main1 mainApp, GameManager gameManager, HighScoreManager highScoreManager, Font pixelFont) {
        this.mainApp = mainApp;
        this.gameManager = gameManager;
        this.highScoreManager = highScoreManager;
        this.pixelFont = pixelFont;
        this.gameCanvas = new Canvas(StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);
        this.gc = gameCanvas.getGraphicsContext2D();
    }

    // --- Getters ---
    public GraphicsContext getGc() { return gc; }
    public Canvas getGameCanvas() { return gameCanvas; }
    public Image getGameBackgroundImage() { return gameBackgroundImage; }
    public Label getGameOverLabel() { return gameOverLabel; }
    public Button getSaveButton() { return saveButton; }
    public Label getSaveStatusLabel() { return saveStatusLabel; }
    public Button getContinueClassicButton() { return continueClassicButton; }
    public Button getContinueAdventureButton() { return continueAdventureButton; }

    // --- Loader ---
    public void loadAllBackgroundImages() {
        String basePath = "/com/mygame/mygamearkanoid/images/";
        // SỬA: Gọi UIManager
        gameBackgroundImage = UIManager.loadImage(basePath + "BackgroundGame10.png");
        menuBackgroundImage = UIManager.loadImage(basePath + "StartGame10.png");
        gameOverBackgroundImage = UIManager.loadImage(basePath + "GameOver.jpg");
        highScoreBackgroundImage = UIManager.loadImage(basePath + "HighScores10.png");
        modeSelectBackgroundImage = UIManager.loadImage(basePath + "StartGame10.png");
    }

    // --- 1. Create Game Screen ---
    public Node createGameScreen() {
        // SỬA: Gọi UIManager
        Image imgPause = UIManager.loadImage("/com/mygame/mygamearkanoid/images/PauseButton.png");
        Button pauseButton = UIManager.createIconButton(new ImageView(imgPause));
        HBox topBar = new HBox(pauseButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setPickOnBounds(false);
        pauseButton.setOnAction(e -> mainApp.pauseGame());
        StackPane gamePane = new StackPane();
        gamePane.getChildren().addAll(this.gameCanvas, topBar);
        gamePane.setFocusTraversable(true);
        return gamePane;
    }

    // --- 2. Create Menu Screen ---
    public VBox createMenuScreen() {
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        if (menuBackgroundImage != null) {
            // SỬA: Gọi UIManager
            menuBox.setBackground(UIManager.createBackgroundImage(menuBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        // SỬA: Gọi UIManager
        Image imgStart = UIManager.loadImage(imgPath + "StartButton.png");
        Button startButton = UIManager.createIconButton(new ImageView(imgStart));
        startButton.setOnAction(e -> mainApp.showStartModeSelectScreen());

        Image imgContinue = UIManager.loadImage(imgPath + "ContinueButton.png");
        Button continueButton = UIManager.createIconButton(new ImageView(imgContinue));
        continueButton.setOnAction(e -> mainApp.showContinueModeSelectScreen());

        Image imgHighScore = UIManager.loadImage(imgPath + "Highscore.png");
        Button highScoreButton = UIManager.createIconButton(new ImageView(imgHighScore));
        highScoreButton.setOnAction(e -> mainApp.showHighScoreModeSelectScreen());

        menuBox.getChildren().addAll(startButton, continueButton, highScoreButton);
        return menuBox;
    }

    // --- 3. MÀN HÌNH CHỌN CHẾ ĐỘ (START) ---
    public VBox createStartModeSelectScreen() {
        VBox modeBox = new VBox(20);
        modeBox.setAlignment(Pos.CENTER);
        if (modeSelectBackgroundImage != null) {
            // SỬA: Gọi UIManager
            modeBox.setBackground(UIManager.createBackgroundImage(modeSelectBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        // SỬA: Gọi UIManager
        Image imgClassic = UIManager.loadImage(imgPath + "ClassicButton.png");
        Button classicButton = UIManager.createIconButton(new ImageView(imgClassic));
        classicButton.setOnAction(e -> {
            mainApp.deleteSaveFile(GameMode.ADVENTURE);
            mainApp.showGameScreen(false, GameMode.CLASSIC);
        });

        Image imgAdventure = UIManager.loadImage(imgPath + "AdventureButton.png");
        Button adventureButton = UIManager.createIconButton(new ImageView(imgAdventure));
        adventureButton.setOnAction(e -> {
            mainApp.deleteSaveFile(GameMode.CLASSIC);
            mainApp.showGameScreen(false, GameMode.ADVENTURE);
        });

        Image imgBack = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgBack));
        backButton.setOnAction(e -> mainApp.showMenuScreen());
        VBox.setMargin(backButton, new Insets(40, 0, 0, 0));

        modeBox.getChildren().addAll(classicButton, adventureButton, backButton);
        return modeBox;
    }

    // --- 4. MÀN HÌNH CHỌN CHẾ ĐỘ (CONTINUE) ---
    public VBox createContinueModeSelectScreen() {
        VBox modeBox = new VBox(20);
        modeBox.setAlignment(Pos.CENTER);
        if (modeSelectBackgroundImage != null) {
            // SỬA: Gọi UIManager
            modeBox.setBackground(UIManager.createBackgroundImage(modeSelectBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        // SỬA: Gọi UIManager
        Image imgClassic = UIManager.loadImage(imgPath + "ClassicButton.png");
        continueClassicButton = UIManager.createIconButton(new ImageView(imgClassic));
        continueClassicButton.setOnAction(e -> {
            try {
                gameManager.loadGame(GameMode.CLASSIC);
                mainApp.showGameScreen(true, GameMode.CLASSIC);
            } catch (Exception ex) {
                mainApp.showMenuScreen();
            }
        });

        Image imgAdventure = UIManager.loadImage(imgPath + "AdventureButton.png");
        continueAdventureButton = UIManager.createIconButton(new ImageView(imgAdventure));
        continueAdventureButton.setOnAction(e -> {
            try {
                gameManager.loadGame(GameMode.ADVENTURE);
                mainApp.showGameScreen(true, GameMode.ADVENTURE);
            } catch (Exception ex) {
                mainApp.showMenuScreen();
            }
        });

        Image imgBack = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgBack));
        backButton.setOnAction(e -> mainApp.showMenuScreen());
        VBox.setMargin(backButton, new Insets(40, 0, 0, 0));

        modeBox.getChildren().addAll(continueClassicButton, continueAdventureButton, backButton);
        return modeBox;
    }

    // --- 5. MÀN HÌNH CHỌN CHẾ ĐỘ (HIGH SCORE) ---
    public VBox createHighScoreModeSelectScreen() {
        VBox modeBox = new VBox(20);
        modeBox.setAlignment(Pos.CENTER);
        if (modeSelectBackgroundImage != null) {
            // SỬA: Gọi UIManager
            modeBox.setBackground(UIManager.createBackgroundImage(modeSelectBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        // SỬA: Gọi UIManager
        Image imgClassic = UIManager.loadImage(imgPath + "ClassicButton.png");
        Button classicButton = UIManager.createIconButton(new ImageView(imgClassic));
        classicButton.setOnAction(e -> mainApp.showHighScoreScreen(GameMode.CLASSIC));

        Image imgAdventure = UIManager.loadImage(imgPath + "AdventureButton.png");
        Button adventureButton = UIManager.createIconButton(new ImageView(imgAdventure));
        adventureButton.setOnAction(e -> mainApp.showHighScoreScreen(GameMode.ADVENTURE));

        Image imgBack = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgBack));
        backButton.setOnAction(e -> mainApp.showMenuScreen());
        VBox.setMargin(backButton, new Insets(40, 0, 0, 0));

        modeBox.getChildren().addAll(classicButton, adventureButton, backButton);
        return modeBox;
    }

    // --- 6. Create Pause Menu ---
    public VBox createPauseMenu() {
        saveStatusLabel = new Label();
        saveStatusLabel.setFont(pixelFont);
        saveStatusLabel.setTextFill(Color.GREEN);
        String imgPath = "/com/mygame/mygamearkanoid/images/";

        // SỬA: Gọi UIManager (cho tất cả)
        Image imgResume = UIManager.loadImage(imgPath + "ContinueButton.png");
        Button resumeButton = UIManager.createIconButton(new ImageView(imgResume));
        resumeButton.setOnAction(e -> mainApp.resumeGame());

        Image imgSave = UIManager.loadImage(imgPath + "SaveButton.png");
        saveButton = UIManager.createIconButton(new ImageView(imgSave));
        saveButton.setOnAction(e -> {
            try {
                gameManager.saveGame();
                saveStatusLabel.setText("Đã lưu game!");
                if (gameManager.gameMode == GameMode.CLASSIC) {
                    mainApp.deleteSaveFile(GameMode.ADVENTURE);
                } else {
                    mainApp.deleteSaveFile(GameMode.CLASSIC);
                }
            } catch (Exception ex) {
                saveStatusLabel.setText("Lỗi khi lưu!");
            }
        });

        Image imgQuit = UIManager.loadImage(imgPath + "QuitButton.png");
        Button quitButton = UIManager.createIconButton(new ImageView(imgQuit));
        quitButton.setOnAction(e -> mainApp.showMenuScreen());

        VBox pauseBox = new VBox(20, resumeButton, saveButton, quitButton, saveStatusLabel);
        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        pauseBox.setMaxSize(300, 350);

        return pauseBox;
    }

    // --- 7. Create Game Over Screen ---
    public StackPane createGameOverScreen() {
        StackPane gameOverBox = new StackPane();
        if (gameOverBackgroundImage != null) {
            gameOverBox.setBackground(UIManager.createBackgroundImage(gameOverBackgroundImage));
        } else {
            // Phòng trường hợp ảnh bị lỗi
            gameOverBox.setStyle("-fx-background-color: black;");
        }
        int finalScore = gameManager.score;
        Font scoreFont = Font.getDefault();
        Label scoreLabel = new Label(String.valueOf(finalScore));
        try {
            // ĐẢM BẢO ĐÚNG ĐƯỜNG DẪN NÀY:
            String fontPath = "/com/mygame/mygamearkanoid/fonts/VT323-Regular.ttf";

            // Bro chỉnh CỠ CHỮ (số 24) cho vừa ô
            scoreFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 24);

            if (scoreFont == null) {
                System.err.println("Không load được font! Dùng font mặc định.");
                scoreFont = Font.getDefault();
            }
        } catch (Exception e) {
            System.err.println("Lỗi load font: " + e.getMessage());
            scoreFont = Font.getDefault();
        }
        scoreLabel.setTranslateY(85);

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        // SỬA: Gọi UIManager (cho tất cả)
        Image imgRestart = UIManager.loadImage(imgPath + "RestartButton.png");
        Button playAgainButton = UIManager.createIconButton(new ImageView(imgRestart));
        playAgainButton.setOnAction(e -> {
            GameMode lastMode = gameManager.gameMode;
            mainApp.showGameScreen(false, lastMode);
        });
        playAgainButton.setTranslateY(150);

        Image imgMenu = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button menuButton = UIManager.createIconButton(new ImageView(imgMenu));
        menuButton.setOnAction(e -> mainApp.showMenuScreen());
        menuButton.setTranslateY(210);

        gameOverBox.getChildren().addAll(scoreLabel, playAgainButton, menuButton);
        return gameOverBox;
    }

    // --- 8. Create High Score Screen ---
    public VBox createHighScoreScreen() {
        VBox highScoreBox = new VBox(20);
        highScoreBox.setAlignment(Pos.CENTER);
        if (highScoreBackgroundImage != null) {
            // SỬA: Gọi UIManager
            highScoreBox.setBackground(UIManager.createBackgroundImage(highScoreBackgroundImage));
        }
        Label title = new Label("Bảng Xếp Hạng");
        title.setVisible(false);
        highScoreDisplayList = new VBox(10);
        highScoreDisplayList.setAlignment(Pos.CENTER);
        // SỬA: Gọi UIManager
        Image imgMenu = UIManager.loadImage("/com/mygame/mygamearkanoid/images/ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgMenu));
        backButton.setOnAction(e -> mainApp.showMenuScreen());
        highScoreBox.getChildren().addAll(title, highScoreDisplayList, backButton);
        highScoreBox.setVisible(false);
        return highScoreBox;
    }

    // --- 9. Update High Score ---
    public void updateHighScoreDisplay(GameMode mode) {
        if (highScoreDisplayList == null) return;
        highScoreDisplayList.getChildren().clear();

        List<Integer> scores = highScoreManager.getHighScores(mode);

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
                scoreLabel.setFont(Font.font(pixelFont.getFamily(), 25));
                scoreLabel.setTextFill(Color.rgb(255, 255, 150));
                highScoreDisplayList.getChildren().add(scoreLabel);
                rank++;
            }
        }
    }
}