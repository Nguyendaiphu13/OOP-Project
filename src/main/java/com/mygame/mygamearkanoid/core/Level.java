package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.MoveBrick; // THÊM IMPORT

import java.util.Random;

public class Level {

    public static void generateLevel1(GameManager gameManager, int rows, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX, double gapY) {
        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * (brickWidth + gapX) + startX;
                double y = i * (brickHeight + gapY) + startY;

                String brickType;
                double r = rand.nextDouble();
                if (r < 0.4) { // 15%
                    brickType = "2normal";
                } else if (r < 0.2) { // 15%
                    brickType = "expand";
                } else if (r < 0.2) { // 15% (THÊM MỚI)
                    brickType = "fast_ball";
                } else { // 55%
                    brickType = "normal";
                }

                gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, brickType));
            }
        }
    }
    public static void generateLevel2(GameManager gameManager, int rows, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX, double gapY) {
        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * (brickWidth + gapX) + startX;
                double y = i * (brickHeight + gapY) + startY;
                if (i == 1 || i == 3) {
                    double speed = StaticFinal.BALL_SPEED;
                    double direction = (i == 1) ? 1.0 : -1.0;
                    gameManager.bricks.add(new MoveBrick(x, y, brickWidth, brickHeight, "normal", speed * direction, 0));

                } else {
                    String brickType;
                    double r = rand.nextDouble();
                    if (r < 0.4) {
                        brickType = "2normal";
                    } else if (r < 0.2) {
                        brickType = "expand";
                    } else if (r < 0.2) {
                        brickType = "fast_ball";
                    } else {
                        brickType = "normal";
                    }
                    gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, brickType));
                }
            }
        }
    }
    public static void generateLevel3(GameManager gameManager, int rows, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX, double gapY) {
        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * (brickWidth + gapX) + startX;
                double y = i * (brickHeight + gapY) + startY;
                double speed = StaticFinal.BALL_SPEED;
                double direction = (i == 1) ? 1.0 : -1.0;

                gameManager.bricks.add(new MoveBrick(x, y, brickWidth, brickHeight, "normal", speed * direction, 0));

            }
        }
    }
}
