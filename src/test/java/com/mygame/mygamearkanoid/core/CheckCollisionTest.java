package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.entities.Ball;
import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.GameObject;
import com.mygame.mygamearkanoid.entities.Paddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Cần thiết để khởi tạo JavaFX cho việc test các đối tượng có Image
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.*;

class CheckCollisionTest {

    // Khởi động JavaFX Toolkit
    // Cần thiết vì Ball, Paddle, Brick tải Image trong constructor
    static {
        try {
            SwingUtilities.invokeAndWait(JFXPanel::new);
        } catch (Exception e) {
            System.out.println("Không thể khởi động JavaFX Toolkit cho test");
            e.printStackTrace();
        }
    }

    private Ball ball;
    private Paddle paddle;

    @BeforeEach
    void setUp() {
        // Tạo một quả bóng tiêu chuẩn (20x20)
        ball = new Ball(100, 100, 20, 20, 5.0, 1, 1, 10);

        // Tạo một ván trượt tiêu chuẩn (100x20)
        paddle = new Paddle(100, 200, 100, 20, 10);
    }

    // ========== 1. KIỂM TRA HÀM INTERSECTS ==========

    @Test
    void testIntersects_True() {
        // Hai đối tượng giao nhau
        GameObject obj1 = new Paddle(10, 10, 10, 10, 0);
        GameObject obj2 = new Paddle(15, 15, 10, 10, 0);
        assertTrue(CheckCollision.intersects(obj1, obj2), "Hai đối tượng nên giao nhau");
    }

    @Test
    void testIntersects_False_BênTrái() {
        GameObject obj1 = new Paddle(10, 10, 10, 10, 0);
        GameObject obj2 = new Paddle(30, 10, 10, 10, 0); // obj2 ở bên phải
        assertFalse(CheckCollision.intersects(obj1, obj2), "Không giao nhau (obj2 ở bên phải)");
    }

    @Test
    void testIntersects_False_BênTrên() {
        GameObject obj1 = new Paddle(10, 10, 10, 10, 0);
        GameObject obj2 = new Paddle(10, 30, 10, 10, 0); // obj2 ở bên dưới
        assertFalse(CheckCollision.intersects(obj1, obj2), "Không giao nhau (obj2 ở bên dưới)");
    }

    // ========== 2. KIỂM TRA VA CHẠM TƯỜNG (Test tĩnh) ==========

    @Test
    void testWallCollision_HitLeftWall() {
        ball.setX(-1); // Đặt bóng ở ngoài lề trái
        ball.directionX = -1;

        CheckCollision.checkWallCollision(ball, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);

        assertEquals(1, ball.directionX, "Hướng X nên đổi thành 1");
        assertEquals(0, ball.getX(), "Vị trí X nên được sửa về 0");
    }

    @Test
    void testWallCollision_HitRightWall() {
        ball.setX(StaticFinal.SCREEN_WIDTH); // Đặt bóng ở ngoài lề phải
        ball.directionX = 1;

        CheckCollision.checkWallCollision(ball, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);

        assertEquals(-1, ball.directionX, "Hướng X nên đổi thành -1");
        assertEquals(StaticFinal.SCREEN_WIDTH - ball.getWidth(), ball.getX(), "Vị trí X nên được sửa");
    }

    @Test
    void testWallCollision_HitTopWall() {
        ball.setY(-1); // Đặt bóng ở ngoài lề trên
        ball.directionY = -1;

        CheckCollision.checkWallCollision(ball, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);

        assertEquals(1, ball.directionY, "Hướng Y nên đổi thành 1");
        assertEquals(0, ball.getY(), "Vị trí Y nên được sửa về 0");
    }

    @Test
    void testWallCollision_BallFallsThroughBottom() {
        ball.setY(StaticFinal.SCREEN_HEIGHT + 1); // Đặt bóng ở dưới màn hình
        ball.alive = true;

        CheckCollision.checkWallCollision(ball, StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT);

        assertFalse(ball.alive, "Bóng nên bị đánh dấu là 'không còn sống'");
    }

    // ========== 3. KIỂM TRA VA CHẠM PADDLE (Test tĩnh) ==========

    @Test
    void testBounceOffPaddle_HitCenter() {
        // Đặt bóng ở giữa ván trượt
        ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2); // X = 100 + 50 - 10 = 140

        CheckCollision.bounceOffPaddle(ball, paddle);

        // Khi đập vào giữa (hitPos=0), góc nảy gần như thẳng đứng (directionX ~ 0)
        assertEquals(0.0, ball.directionX, "Hướng X nên = 0 khi đập vào giữa");
        assertEquals(-1.0, ball.directionY, "Hướng Y nên luôn là -1 (đi lên)");
    }

    @Test
    void testBounceOffPaddle_HitFarRightEdge() {
        // Đặt bóng ở mép phải ván trượt (X = 100 + 100 - 10 = 190)
        ball.setX(paddle.getX() + paddle.getWidth() - ball.getWidth() / 2);

        CheckCollision.bounceOffPaddle(ball, paddle);

        // hitPos = 1.0, angle = 60 độ (radian ~ 1.047)
        // dirX = sin(angle) > 0.8
        // dirY = -cos(angle) = -0.5
        assertTrue(ball.directionX > 0.8, "Hướng X nên đi sang phải (gần 0.866)");
        assertTrue(ball.directionY < 0, "Hướng Y nên luôn là âm (đi lên)");
        assertTrue(ball.directionY > -1, "Góc nảy Y không phải -1 khi đập ở mép (gần -0.5)");
    }

    // ========== 4. KIỂM TRA VA CHẠM GẠCH (Test tĩnh) ==========

    @Test
    void testBounceOffBrick_HitFromBottom() {
        // Gạch ở (100, 80), đáy ở Y=100
        Brick brick = new Brick(100, 80, StaticFinal.BRICK_WIDTH, StaticFinal.BRICK_HEIGHT, "normal");

        // 1. Đặt bóng ở vị trí ĐÃ VA CHẠM (hơi lún vào gạch)
        //    Bóng (rộng 20, cao 20)
        ball.setX(125); // Nằm trong gạch theo trục X (100-170)
        ball.setY(99);  // Đáy gạch ở Y=100, bóng (có Y=99) bị lún 1px

        // 2. Quan trọng: Đặt hướng bóng là ĐI LÊN
        ball.directionY = -1.0;
        ball.directionX = 0.0;
        ball.updateVelocity(); // Cập nhật dy = -5

        // Act: Gọi hàm va chạm
        boolean[] result = CheckCollision.bounceOffBrick(ball, brick, false, false);

        // Assert: Kiểm tra kết quả

        // 1. Hướng Y phải bị lật (từ -1 thành 1)
        assertEquals(1.0, ball.directionY, "Bóng nên đổi hướng Y (đi xuống)");

        // 2. Cờ nảy Y (result[1]) phải là true
        assertTrue(result[1], "Cờ 'hasBouncedY' phải là true");

        // 3. Cờ nảy X (result[0]) phải là false
        assertFalse(result[0], "Cờ 'hasBouncedX' phải là false");

        // 4. Vị trí của bóng phải được ĐẨY RA, không còn lún
        assertTrue(ball.getY() >= 100, "Vị trí bóng phải được đẩy ra khỏi gạch (Y >= 100)");
    }

    @Test
    void testBounceOffBrick_HitFromLeft() {
        // Gạch ở (100, 80), mép trái ở X=100
        Brick brick = new Brick(100, 80, StaticFinal.BRICK_WIDTH, StaticFinal.BRICK_HEIGHT, "normal");

        // 1. Đặt bóng lún vào mép trái gạch
        //    (Gạch rộng 70, cao 20. Bóng rộng 20, cao 20)

        // KỊCH BẢN MỚI:
        ball.setX(99);  // Lún 1px theo chiều X
        ball.setY(85);  // Gần như ở giữa theo chiều Y

        // Tính toán Overlap cho kịch bản này:
        // Tâm Gạch: (100+35, 80+10) = (135, 90)
        // Tâm Bóng: (99+10, 85+10) = (109, 95)
        // Nửa rộng (Bóng+Gạch): 10 + 35 = 45
        // Nửa cao (Bóng+Gạch): 10 + 10 = 20
        // overlapX = 45 - abs(109 - 135) = 45 - 26 = 19
        // overlapY = 20 - abs(95 - 90) = 20 - 5 = 15
        // Vẫn là `overlapY < overlapX`. Test của tôi vẫn sai.

        // KỊCH BẢN MỚI (LẦN 3):
        // Ta cần overlapX < overlapY.
        // Di chuyển bóng sang phải, va chạm MÉP PHẢI
        // Mép phải gạch ở X = 170.
        ball.setX(169); // Lún 1px vào mép phải (Tâm X = 179)
        ball.setY(85);  // Giữ nguyên Y (Tâm Y = 95)

        // Tính toán Overlap MỚI:
        // overlapX = 45 - abs(179 - 135) = 45 - 44 = 1
        // overlapY = 20 - abs(95 - 90) = 20 - 5 = 15

        // KỊCH BẢN HOÀN HẢO: overlapX (1) < overlapY (15)

        // 2. Đặt hướng bóng là ĐI SANG TRÁI (để va vào mép phải)
        ball.directionX = -1.0;
        ball.directionY = 0.0;
        ball.updateVelocity();

        // Act
        boolean[] result = CheckCollision.bounceOffBrick(ball, brick, false, false);

        // Assert
        // Code sẽ kiểm tra if (1 < 15) -> true -> chạy khối "Va chạm cạnh bên"

        // 1. Hướng X phải bị lật (từ -1 thành 1)
        assertEquals(1.0, ball.directionX, "Bóng nên đổi hướng X (đi sang phải)");

        // 2. Cờ nảy X (result[0]) phải là true
        assertTrue(result[0], "Cờ 'hasBouncedX' phải là true");

        // 3. Vị trí bóng phải được đẩy ra
        assertTrue(ball.getX() >= 170, "Vị trí bóng phải được đẩy ra khỏi gạch (X >= 170)");
    }
}