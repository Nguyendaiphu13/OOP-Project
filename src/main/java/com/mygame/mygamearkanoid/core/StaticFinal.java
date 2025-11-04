package com.mygame.mygamearkanoid.core;

// Lớp này chứa tất cả các hằng số của game
public final class StaticFinal {

    // Ngăn không cho ai đó tạo đối tượng
    private StaticFinal() {}
    public static final String CLASSIC_SAVE_FILE = "classicSave.dat";
    public static final String ADVENTURE_SAVE_FILE = "adventureSave.dat";
    public static final String CLASSIC_HIGHSCORE_FILE = "classic_highscore.txt";
    public static final String ADVENTURE_HIGHSCORE_FILE = "adventure_highscore.txt";

    // --- CẤU HÌNH ỨNG DỤNG ---
    public static final String GAME_TITLE = "OOP Game - JavaFX";
    public static final int SCREEN_WIDTH = 900;
    public static final int SCREEN_HEIGHT = 750;

    // --- CẤU HÌNH FILE ---
    public static final String AUTO_SAVE_FILE = "autoSaveFile";
    public static final String HIGH_SCORE_FILE = "ark_highscores.dat";
    public static final int MAX_SCORES_TO_KEEP = 4;

    // --- CẤU HÌNH PADDLE ---
    public static final int PADDLE_WIDTH_DEFAULT = 100;
    public static final int PADDLE_HEIGHT_DEFAULT = 20;
    public static final double PADDLE_SPEED = 100;
    public static final double PADDLE_Y_OFFSET = 50;

    // --- CẤU HHình BALL ---
    public static final int BALL_RADIUS = 10;
    public static final double BALL_SPEED = 5.0;

    // --- CẤU HÌNH LEVEL ---
    public static final int BRICK_ROWS = 6;
    public static final int BRICK_COLS = 11;
    public static final int BRICK_WIDTH = 70;
    public static final int BRICK_HEIGHT = 20;
    public static final double BRICK_START_X = 50;
    public static final double BRICK_START_Y = 90;
    public static final double BRICK_GAP_X = 3;
    public static final double BRICK_GAP_Y = 3;
}