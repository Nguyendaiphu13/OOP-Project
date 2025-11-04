package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.MoveBrick;

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
                if (r < 0.3) {
                    brickType = "2normal";
                } else if (r < 0.45) {
                    brickType = "expand";
                } else if (r < 0.6) {
                    brickType = "fast_ball";
                } else {
                    brickType = "normal";
                }

                Brick newBrick = new Brick(x, y, brickWidth, brickHeight, brickType);
                newBrick.initializeGraphics();
                gameManager.bricks.add(newBrick);
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
                    MoveBrick newMoveBrick = new MoveBrick(x, y, brickWidth, brickHeight, "normal", speed * direction, 0);
                    newMoveBrick.initializeGraphics();
                    gameManager.bricks.add(newMoveBrick);

                } else {
                    String brickType;
                    double r = rand.nextDouble();
                    if (r < 0.3) {
                        brickType = "2normal";
                    } else if (r < 0.45) {
                        brickType = "expand";
                    } else if (r < 0.6) {
                        brickType = "fast_ball";
                    } else {
                        brickType = "normal";
                    }
                    Brick newBrick = new Brick(x, y, brickWidth, brickHeight, brickType);
                    newBrick.initializeGraphics();
                    gameManager.bricks.add(newBrick);
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

                MoveBrick newMoveBrick = new MoveBrick(x, y, brickWidth, brickHeight, "normal", speed * direction, 0);
                newMoveBrick.initializeGraphics();
                gameManager.bricks.add(newMoveBrick);

            }
        }
    }
}