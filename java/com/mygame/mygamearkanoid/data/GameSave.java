package com.mygame.mygamearkanoid.data;

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

    public int currentLevel;

    // Dữ liệu Paddle
    public double paddleX;
    public int paddleWidth; // Lưu cả chiều rộng vì nó thay đổi

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