package org.example.demo;

import java.util.Random;

public class Level extends GameManager {

    private static Random rand = new Random();

    /**
     * Hàm này tạo Level 1 (mặc định)
     */
    public static void generateLevel1(GameManager gameManager, int rows, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX, double gapY) {
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

    /**
     * --- TÍNH NĂNG MỚI: Hàm tạo Level 2 ---
     * (Tạo một mẫu gạch khác)
     */
    public static void generateLevel2(GameManager gameManager, int rows, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX, double gapY) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Tạo mẫu bàn cờ
                if ((i + j) % 2 == 0) {
                    double x = j * (brickWidth + gapX) + startX;
                    double y = i * (brickHeight + gapY) + startY;

                    String brickType;
                    double r = rand.nextDouble();
                    if (r < 0.30) {
                        brickType = "2normal";
                    } else if (r < 0.50) {
                        brickType = "expand";
                    } else {
                        brickType = "normal";
                    }
                    gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, brickType));
                }
            }
        }
    }

    /**
     * --- TÍNH NĂNG MỚI: Hàm tạo 1 hàng gạch (cho chế độ Endless) ---
     */
    public static void generateBrickRow(GameManager gameManager, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX) {
        for (int j = 0; j < cols; j++) {
            double x = j * (brickWidth + gapX) + startX;
            double y = startY;

            // 50% cơ hội tạo gạch ở mỗi vị trí
            if (rand.nextDouble() < 0.5) {
                String brickType;
                double r = rand.nextDouble();
                if (r < 0.20) {
                    brickType = "2normal";
                } else if (r < 0.30) {
                    brickType = "expand";
                } else {
                    brickType = "normal";
                }
                gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, brickType));
            }
        }
    }
}
