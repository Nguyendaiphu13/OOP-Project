package org.example.demo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameSave implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Dữ liệu chung
    public int score;
    public int lives;
    // --- TÍNH NĂNG MỚI ---
    public int currentLevel;
    public String gameMode;
    // ---------------------

    // Dữ liệu Paddle
    public double paddleX;
    public int paddleWidth;

    // Dữ liệu Ball
    public double ballX, ballY;
    public double ballDirectionX, ballDirectionY;
    public boolean ballAlive;

    // Dữ liệu Bricks
    public List<BrickSave> bricks = new ArrayList<>();

    // Dữ liệu Power-up
    public PowerUpSave activeEffect = null;

    // Constructor rỗng
    public GameSave() {}
}
