package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.entities.Brick;

import java.util.Random;

public class Level extends GameManager {
    public static void generateLevel1(GameManager gameManager, int rows, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX, double gapY) {
        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * (brickWidth + gapX) + startX;
                double y = i * (brickHeight + gapY) + startY;

                String brickType;
                double r = rand.nextDouble();
                if (r < 0.20) {
                    brickType = "2normal";
                } else if (r < 0.40) {
                    brickType = "expand";
                } else {
                    brickType = "normal";
                }

                gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, brickType));
            }
        }
    }
}
