package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.StackPane;

import java.util.List;

public class ScreenManager {

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

    // Getter
    public GraphicsContext getGc() { return gc; }
    public Canvas getGameCanvas() { return gameCanvas; }
    public Image getGameBackgroundImage() { return gameBackgroundImage; }
    public Label getGameOverLabel() { return gameOverLabel; }
    public Button getSaveButton() { return saveButton; }
    public Label getSaveStatusLabel() { return saveStatusLabel; }
    public Button getContinueClassicButton() { return continueClassicButton; }
    public Button getContinueAdventureButton() { return continueAdventureButton; }

    // Loader
    public void loadAllBackgroundImages() {
        String basePath = "/com/mygame/mygamearkanoid/images/";
        gameBackgroundImage = UIManager.loadImage(basePath + "BackgroundGame10.png");
        menuBackgroundImage = UIManager.loadImage(basePath + "StartGame10.png");
        gameOverBackgroundImage = UIManager.loadImage(basePath + "GameOver100.png");
        highScoreBackgroundImage = UIManager.loadImage(basePath + "HighScores10.png");
        modeSelectBackgroundImage = UIManager.loadImage(basePath + "StartGame10.png");
    }

    // tạo gamescreen
    public Node createGameScreen() {
        Image imgPause = UIManager.loadImage("/com/mygame/mygamearkanoid/images/PauseButton.png");
        Button pauseButton = UIManager.createIconButton(new ImageView(imgPause));
        HBox topBar = new HBox(pauseButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setPickOnBounds(false);
        pauseButton.setOnAction(_ -> mainApp.pauseGame());
        StackPane gamePane = new StackPane();
        gamePane.getChildren().addAll(this.gameCanvas, topBar);
        gamePane.setFocusTraversable(true);
        return gamePane;
    }

    //màn hình chính
    public VBox createMenuScreen() {
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(380, 0, 0, 50));
        if (menuBackgroundImage != null) {
            menuBox.setBackground(UIManager.createBackgroundImage(menuBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        Image imgStart = UIManager.loadImage(imgPath + "StartButton.png");
        Button startButton = UIManager.createIconButton(new ImageView(imgStart));
        startButton.setOnAction(_ -> mainApp.showStartModeSelectScreen());

        Image imgContinue = UIManager.loadImage(imgPath + "ContinueButton.png");
        Button continueButton = UIManager.createIconButton(new ImageView(imgContinue));
        continueButton.setOnAction(_ -> mainApp.showContinueModeSelectScreen());

        VBox.setMargin(continueButton, new Insets(10, 0, 0, 0));

        Image imgHighScore = UIManager.loadImage(imgPath + "Highscore.png");
        Button highScoreButton = UIManager.createIconButton(new ImageView(imgHighScore));
        highScoreButton.setOnAction(_ -> mainApp.showHighScoreModeSelectScreen());

        VBox.setMargin(highScoreButton, new Insets(10, 0, 0, 0));


        menuBox.getChildren().addAll(startButton, continueButton, highScoreButton);
        return menuBox;
    }

    // màn hình chọn chế độ khi ấn START
    public VBox createStartModeSelectScreen() {
        VBox modeBox = new VBox(20);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.setPadding(new Insets(380, 0, 0, 50));
        if (modeSelectBackgroundImage != null) {
            modeBox.setBackground(UIManager.createBackgroundImage(modeSelectBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        Image imgClassic = UIManager.loadImage(imgPath + "ClassicButton.png");
        Button classicButton = UIManager.createIconButton(new ImageView(imgClassic));
        classicButton.setOnAction(_ -> {
            mainApp.deleteSaveFile(GameMode.ADVENTURE);
            mainApp.showGameScreen(false, GameMode.CLASSIC);
        });

        Image imgAdventure = UIManager.loadImage(imgPath + "AdventureButton.png");
        Button adventureButton = UIManager.createIconButton(new ImageView(imgAdventure));
        adventureButton.setOnAction(_ -> {
            mainApp.deleteSaveFile(GameMode.CLASSIC);
            mainApp.showGameScreen(false, GameMode.ADVENTURE);
        });

        Image imgBack = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgBack));
        backButton.setOnAction(_ -> mainApp.showMenuScreen());
        VBox.setMargin(backButton, new Insets(20, 0, 0, 0)); // Giữ nguyên, đã ổn

        modeBox.getChildren().addAll(classicButton, adventureButton, backButton);
        return modeBox;
    }

    // màn hình chọn chế độ khi ấn CONTINUE
    public VBox createContinueModeSelectScreen() {
        VBox modeBox = new VBox(20);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.setPadding(new Insets(380, 0, 0, 50));
        if (modeSelectBackgroundImage != null) {
            modeBox.setBackground(UIManager.createBackgroundImage(modeSelectBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        Image imgClassic = UIManager.loadImage(imgPath + "ClassicButton.png");
        continueClassicButton = UIManager.createIconButton(new ImageView(imgClassic));
        continueClassicButton.setOnAction(_ -> {
            try {
                gameManager.loadGame(GameMode.CLASSIC);
                mainApp.showGameScreen(true, GameMode.CLASSIC);
            } catch (Exception ex) {
                mainApp.showMenuScreen();
            }
        });

        Image imgAdventure = UIManager.loadImage(imgPath + "AdventureButton.png");
        continueAdventureButton = UIManager.createIconButton(new ImageView(imgAdventure));
        continueAdventureButton.setOnAction(_ -> {
            try {
                gameManager.loadGame(GameMode.ADVENTURE);
                mainApp.showGameScreen(true, GameMode.ADVENTURE);
            } catch (Exception ex) {
                mainApp.showMenuScreen();
            }
        });

        Image imgBack = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgBack));
        backButton.setOnAction(_ -> mainApp.showMenuScreen());
        VBox.setMargin(backButton, new Insets(20, 0, 0, 0)); // Giữ nguyên

        modeBox.getChildren().addAll(continueClassicButton, continueAdventureButton, backButton);
        return modeBox;
    }

    // màn hình chọn chế độ khi ấn HIGH SCORES
    public VBox createHighScoreModeSelectScreen() {
        VBox modeBox = new VBox(20);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.setPadding(new Insets(380, 0, 0, 50));
        if (modeSelectBackgroundImage != null) {
            modeBox.setBackground(UIManager.createBackgroundImage(modeSelectBackgroundImage));
        }

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        Image imgClassic = UIManager.loadImage(imgPath + "ClassicButton.png");
        Button classicButton = UIManager.createIconButton(new ImageView(imgClassic));
        classicButton.setOnAction(_ -> mainApp.showHighScoreScreen(GameMode.CLASSIC));

        Image imgAdventure = UIManager.loadImage(imgPath + "AdventureButton.png");
        Button adventureButton = UIManager.createIconButton(new ImageView(imgAdventure));
        adventureButton.setOnAction(_ -> mainApp.showHighScoreScreen(GameMode.ADVENTURE));

        Image imgBack = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgBack));
        backButton.setOnAction(_ -> mainApp.showMenuScreen());
        VBox.setMargin(backButton, new Insets(20, 0, 0, 0)); // Giữ nguyên

        modeBox.getChildren().addAll(classicButton, adventureButton, backButton);
        return modeBox;
    }

    // menu pause khi đang chơi
    public VBox createPauseMenu() {
        saveStatusLabel = new Label();
        saveStatusLabel.setFont(pixelFont);
        saveStatusLabel.setTextFill(Color.GREEN);
        String imgPath = "/com/mygame/mygamearkanoid/images/";

        Image imgResume = UIManager.loadImage(imgPath + "ContinueButton.png");
        Button resumeButton = UIManager.createIconButton(new ImageView(imgResume));
        resumeButton.setOnAction(_ -> mainApp.resumeGame());

        Image imgSave = UIManager.loadImage(imgPath + "SaveButton.png");
        saveButton = UIManager.createIconButton(new ImageView(imgSave));
        saveButton.setOnAction(_ -> {
            try {
                gameManager.saveGame();
                saveStatusLabel.setText("SAVED !!!");
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
        quitButton.setOnAction(_ -> mainApp.showMenuScreen());

        VBox pauseBox = new VBox(20, resumeButton, saveButton, quitButton, saveStatusLabel);
        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        pauseBox.setMaxSize(300, 350);

        return pauseBox;
    }

    // menu gameover
    public StackPane createGameOverScreen() {
        StackPane gameOverBox = new StackPane();
        if (gameOverBackgroundImage != null) {
            gameOverBox.setBackground(UIManager.createBackgroundImage(gameOverBackgroundImage));
        } else {
            gameOverBox.setStyle("-fx-background-color: black;");
        }
        int finalScore = gameManager.score;
        Font scoreFont = Font.getDefault();

        gameOverLabel = new Label(String.format("%d", finalScore)); //

        try {
            String fontPath = "/com/mygame/mygamearkanoid/fonts/PixelOperatorHB.ttf";
            scoreFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 25);

            if (scoreFont == null) {
                System.err.println("Không load được font! Dùng font mặc định.");
                scoreFont = Font.getDefault();
            }
        } catch (Exception e) {
            System.err.println("Lỗi load font: " + e.getMessage());
            scoreFont = Font.getDefault();
        }
        gameOverLabel.setFont(scoreFont); //
        gameOverLabel.setTextFill(Color.WHITE); //
        gameOverLabel.setTranslateY(147); //
        gameOverLabel.setTranslateX(35); //

        String imgPath = "/com/mygame/mygamearkanoid/images/";

        Image imgRestart = UIManager.loadImage(imgPath + "RestartButton.png");
        Button playAgainButton = UIManager.createIconButton(new ImageView(imgRestart));
        playAgainButton.setOnAction(_ -> {
            GameMode lastMode = gameManager.gameMode;
            mainApp.showGameScreen(false, lastMode);
        });

        playAgainButton.setTranslateY(210);
        playAgainButton.setTranslateX(0);

        Image imgMenu = UIManager.loadImage(imgPath + "ReturnMenu.png");
        Button menuButton = UIManager.createIconButton(new ImageView(imgMenu));
        menuButton.setOnAction(_ -> mainApp.showMenuScreen());

        menuButton.setTranslateY(270);
        menuButton.setTranslateX(0);

        gameOverBox.getChildren().addAll(gameOverLabel, playAgainButton, menuButton);
        return gameOverBox;
    }

    // tạo màn hình high scores
    public VBox createHighScoreScreen() {
        VBox highScoreBox = new VBox(20);
        highScoreBox.setAlignment(Pos.CENTER);
        highScoreBox.setPadding(new Insets(100, 0, 0, 0));
        if (highScoreBackgroundImage != null) {
            highScoreBox.setBackground(UIManager.createBackgroundImage(highScoreBackgroundImage));
        }
        Label title = new Label("Bảng Xếp Hạng");
        title.setVisible(false);
        highScoreDisplayList = new VBox(10);
        highScoreDisplayList.setAlignment(Pos.CENTER);
        VBox.setMargin(highScoreDisplayList, new Insets(0, 0, 130, 0));

        Image imgMenu = UIManager.loadImage("/com/mygame/mygamearkanoid/images/ReturnMenu.png");
        Button backButton = UIManager.createIconButton(new ImageView(imgMenu));
        backButton.setOnAction(_ -> mainApp.showMenuScreen());

        VBox.setMargin(backButton, new Insets(0, 0, 50, 70)); // Giữ nguyên, đã được đẩy bằng margin của list

        highScoreBox.getChildren().addAll(title, highScoreDisplayList, backButton);
        return highScoreBox;
    }

    // cập nhật điểm bxh
    public void updateHighScoreDisplay(GameMode mode) {
        if (highScoreDisplayList == null) return;
        highScoreDisplayList.getChildren().clear();

        List<Integer> scores = highScoreManager.getHighScores(mode);

        highScoreDisplayList.setPadding(new Insets(0, 0, 0, 55));
        highScoreDisplayList.setSpacing(5);
        highScoreDisplayList.setPrefWidth(300);
        highScoreDisplayList.setMaxWidth(300);

        if (scores.isEmpty()) {
            Label emptyLabel = new Label("Chưa có điểm");
            emptyLabel.setFont(Font.font(pixelFont.getFamily(), 18));
            emptyLabel.setTextFill(Color.rgb(200, 200, 200));
            highScoreDisplayList.getChildren().add(emptyLabel);
        } else {
            int rank = 1;
            for (Integer score : scores) {
                Label scoreLabel = new Label(String.format("%2d. %d", rank, score));
                scoreLabel.setFont(Font.font(pixelFont.getFamily(), 23));
                scoreLabel.setTextFill(Color.rgb(255, 255, 150));
                highScoreDisplayList.getChildren().add(scoreLabel);
                rank++;
            }
        }
    }
}