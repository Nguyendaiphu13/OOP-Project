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
                if (r < 0.15) { // 15%
                    brickType = "2normal";
                } else if (r < 0.30) { // 15%
                    brickType = "expand";
                } else if (r < 0.45) { // 15% (THÊM MỚI)
                    brickType = "fast_ball";
                } else { // 55%
                    brickType = "normal";
                }

                gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, brickType));
            }
        }
    }

    // SỬA HÀM NÀY
    public static void generateLevel2(GameManager gameManager, int rows, int cols, int brickWidth, int brickHeight, double startX, double startY, double gapX, double gapY) {
        // Sửa lỗi (1): 'random' -> 'Random'
        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * (brickWidth + gapX) + startX;
                double y = i * (brickHeight + gapY) + startY;

                // SỬA LOGIC (2): Thêm MoveBrick
                // Hàng 2 và 4 (chỉ số 1 và 3) sẽ là gạch di chuyển
                if (i == 1 || i == 3) {
                    // Lấy tốc độ bóng làm tốc độ gạch
                    double speed = StaticFinal.BALL_SPEED;
                    // Cho gạch di chuyển ngược chiều nhau
                    double direction = (i == 1) ? 1.0 : -1.0;

                    gameManager.bricks.add(new MoveBrick(x, y, brickWidth, brickHeight, "normal", speed * direction, 0));

                } else {
                    // Các hàng khác tạo gạch bình thường
                    String brickType;
                    double r = rand.nextDouble();
                    if (r < 0.20) {
                        brickType = "2normal";
                    } else if (r < 0.40) {
                        brickType = "expand";
                    } else if (r < 0.60) {
                        brickType = "fast_ball";
                    } else {
                        brickType = "normal";
                    }
                    gameManager.bricks.add(new Brick(x, y, brickWidth, brickHeight, brickType));
                }
            }
        }
    }
}
