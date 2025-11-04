package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.entities.Ball;
import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.Paddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lớp test này kiểm tra logic cốt lõi của GameManager,
 * bao gồm va chạm và tính điểm.
 */
@ExtendWith(MockitoExtension.class)
class GameManagerTest {

    static {
        try {
            // Yêu cầu luồng Swing khởi tạo một JFXPanel
            // Hành động này sẽ khởi động JavaFX Toolkit
            SwingUtilities.invokeAndWait(JFXPanel::new);
        } catch (Exception e) {
            System.out.println("Không thể khởi động JavaFX Toolkit cho test");
            e.printStackTrace();
        }
    }

    private GameManager gameManager;

    // Giả lập SoundManager để nó không phát ra âm thanh khi test
    @Mock
    private SoundManager mockSoundManager;

    @BeforeEach
    void setUp() {
        // 1. Khởi tạo GameManager
        gameManager = new GameManager();
        gameManager.gameState = GameState.PLAYING; // Đặt trạng thái để updateGame() chạy

        // 2. Khởi tạo Paddle thủ công (dùng hằng số từ file StaticFinal)
        // Vị trí X = (900 - 100) / 2 = 400
        // Vị trí Y = 750 - 20 - 50 = 680
        double paddleX = (StaticFinal.SCREEN_WIDTH - StaticFinal.PADDLE_WIDTH_DEFAULT) / 2.0;
        double paddleY = StaticFinal.SCREEN_HEIGHT - StaticFinal.PADDLE_HEIGHT_DEFAULT - StaticFinal.PADDLE_Y_OFFSET;
        gameManager.paddle = new Paddle(
                paddleX,
                paddleY,
                StaticFinal.PADDLE_WIDTH_DEFAULT,
                StaticFinal.PADDLE_HEIGHT_DEFAULT,
                StaticFinal.PADDLE_SPEED
        );

        // 3. Khởi tạo Ball thủ công
        gameManager.ball = new Ball(
                400, 300, // x, y (sẽ được các test tự đặt lại)
                StaticFinal.BALL_RADIUS * 2, // width = 20
                StaticFinal.BALL_RADIUS * 2, // height = 20
                StaticFinal.BALL_SPEED, // speed
                0.5, 0.5, // dirX, dirY
                StaticFinal.BALL_RADIUS
        );
    }

    // ========== 1. KIỂM TRA VA CHẠM TƯỜNG ==========

    @Test
    void testWallCollision_HitTopWall() {
        // Sắp xếp (Arrange)
        gameManager.ball.setY(1.0); // Đặt bóng sát tường trên
        gameManager.ball.directionY = -1.0; // Hướng bóng đi lên
        gameManager.ball.updateVelocity(); // Cập nhật dx/dy

        // Hành động (Act)
        gameManager.updateGame(mockSoundManager); // Chạy 1 frame game

        // Khẳng định (Assert)
        assertTrue(gameManager.ball.directionY > 0, "Bóng nên đổi hướng Y (đi xuống)");
        assertEquals(0, gameManager.ball.getY(), "Bóng nên được đặt lại vị trí Y=0");
    }

    @Test
    void testWallCollision_HitLeftWall() {
        // Arrange
        gameManager.ball.setX(1.0);
        gameManager.ball.directionX = -1.0; // Hướng đi sang trái
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        assertTrue(gameManager.ball.directionX > 0, "Bóng nên đổi hướng X (đi sang phải)");
        assertEquals(0, gameManager.ball.getX(), "Bóng nên được đặt lại vị trí X=0");
    }

    @Test
    void testWallCollision_HitRightWall() {
        // Arrange
        double ballWidth = gameManager.ball.getWidth(); // 20
        // Vị trí sát tường phải (900 - 20 - 1) = 879
        gameManager.ball.setX(StaticFinal.SCREEN_WIDTH - ballWidth - 1.0);
        gameManager.ball.directionX = 1.0; // Hướng đi sang phải
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        assertTrue(gameManager.ball.directionX < 0, "Bóng nên đổi hướng X (đi sang trái)");
        assertEquals(StaticFinal.SCREEN_WIDTH - ballWidth, gameManager.ball.getX(), "Bóng nên được đặt lại vị trí X tối đa");
    }

    @Test
    void testWallCollision_HitBottomWall_LosesLife() {
        // Arrange
        gameManager.lives = 3;
        double ballHeight = gameManager.ball.getHeight(); // 20
        // Vị trí sát tường dưới (750 - 20 - 1) = 729
        gameManager.ball.setY(StaticFinal.SCREEN_HEIGHT - ballHeight - 1.0);
        gameManager.ball.directionY = 1.0; // Hướng đi xuống
        gameManager.ball.updateVelocity();

        // Act
        // (updateGame sẽ gọi ball.update() -> checkWallCollision() -> xử lý !ball.alive)
        gameManager.updateGame(mockSoundManager);

        // Assert
        assertEquals(2, gameManager.lives, "Người chơi nên mất 1 mạng");
        assertTrue(gameManager.ball.alive, "Bóng nên được hồi sinh");
        assertTrue(gameManager.ball.getY() < gameManager.paddle.getY(), "Bóng hồi sinh phải ở trên paddle");
        assertTrue(gameManager.ball.directionY < 0, "Bóng mới nên có hướng Y đi lên");
    }

    // ========== 2. KIỂM TRA VA CHẠM PADDLE ==========

    @Test
    void testPaddleCollision_CenterHit() {
        // Arrange
        // Paddle Y = 680. Ball Height = 20.
        // Đặt bóng ngay trên GİỮA paddle (X=440)
        gameManager.ball.setX(gameManager.paddle.getX() + (gameManager.paddle.getWidth() / 2.0) - (gameManager.ball.getWidth() / 2.0));
        // Đặt bóng tại Y = 680 - 20 - 1 = 659
        gameManager.ball.setY(gameManager.paddle.getY() - gameManager.ball.getHeight() - 1);
        gameManager.ball.directionY = 1.0; // Hướng đi xuống
        gameManager.ball.directionX = 0.0; // Đi thẳng xuống
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        // Va chạm giữa: hitPos = 0, angle = 0, directionX = sin(0) = 0, directionY = -cos(0) = -1
        assertTrue(gameManager.ball.directionY < 0, "Bóng nên nảy lên (directionY < 0)");
        assertEquals(0.0, gameManager.ball.directionX, "Bóng va chạm giữa nên nảy thẳng đứng (directionX = 0)");

        // Khẳng định rằng hàm playPaddleHit() đã được gọi 1 lần
        Mockito.verify(mockSoundManager, Mockito.times(1)).playPaddleHit();
    }

    @Test
    void testPaddleCollision_RightSideHit() {
        // Arrange
        // Đặt bóng ngay trên MÉP PHẢI paddle
        gameManager.ball.setX(gameManager.paddle.getX() + gameManager.paddle.getWidth() - gameManager.ball.getWidth());
        gameManager.ball.setY(gameManager.paddle.getY() - gameManager.ball.getHeight() - 1); // Y = 659
        gameManager.ball.directionY = 1.0; // Hướng đi xuống
        gameManager.ball.directionX = 0.0;
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        // Va chạm mép phải: hitPos gần 1.0, angle > 0, directionX = sin(angle) > 0
        assertTrue(gameManager.ball.directionY < 0, "Bóng nên nảy lên (directionY < 0)");
        assertTrue(gameManager.ball.directionX > 0, "Bóng va chạm phải nên nảy sang phải (directionX > 0)");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playPaddleHit();
    }


    // ========== 3. KIỂM TRA VA CHẠM GẠCH & TÍNH ĐIỂM ==========

    @Test
    void testBrickCollision_NormalBrick_And_Scoring() {
        // Arrange
        gameManager.score = 0;
        // Thêm 1 gạch "normal"
        Brick brick = new Brick(400, 100, StaticFinal.BRICK_WIDTH, StaticFinal.BRICK_HEIGHT, "normal");
        gameManager.bricks.add(brick);

        // Đặt bóng ngay dưới gạch (đáy gạch Y = 100 + 20 = 120)
        gameManager.ball.setX(410); // Nằm trong phạm vi gạch
        gameManager.ball.setY(121); // Y = 120 + 1 (ngay dưới gạch)
        gameManager.ball.directionY = -1.0; // Hướng đi lên
        gameManager.ball.directionX = 0.0;
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        assertTrue(gameManager.ball.directionY > 0, "Bóng nên nảy xuống (directionY > 0)");
        assertEquals(10, gameManager.score, "Điểm phải tăng lên 10"); // Test tính điểm
        assertTrue(gameManager.bricks.isEmpty(), "Viên gạch 'normal' nên bị phá hủy và xóa khỏi danh sách");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playBrickHit();
    }

    @Test
    void testBrickCollision_HardBrick_TwoHits() {
        // Arrange
        gameManager.score = 0;
        Brick hardBrick = new Brick(400, 100, StaticFinal.BRICK_WIDTH, StaticFinal.BRICK_HEIGHT, "2normal");
        gameManager.bricks.add(hardBrick);

        // --- LƯỢT VA CHẠM 1 ---
        // Arrange 1
        gameManager.ball.setX(410);
        gameManager.ball.setY(121); // Ngay dưới gạch
        gameManager.ball.directionY = -1.0; // Hướng đi lên
        gameManager.ball.updateVelocity();

        // Act 1
        gameManager.updateGame(mockSoundManager);

        // Assert 1
        assertTrue(gameManager.ball.directionY > 0, "Bóng nên nảy xuống (hit 1)");
        assertEquals(0, gameManager.score, "Chưa được cộng điểm (hit 1)"); // Test tính điểm
        assertEquals(1, gameManager.bricks.size(), "Gạch '2normal' không nên bị xóa (hit 1)");
        assertEquals(1, hardBrick.getHitPoints(), "Gạch '2normal' phải còn 1 máu");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playHardBrickHit(); // Âm thanh gạch cứng

        // --- LƯỢT VA CHẠM 2 ---
        // Arrange 2 (Reset bóng, vẫn hướng đi lên)
        gameManager.ball.setY(121);
        gameManager.ball.directionY = -1.0;
        gameManager.ball.updateVelocity();

        // Act 2
        gameManager.updateGame(mockSoundManager);

        // Assert 2
        assertTrue(gameManager.ball.directionY > 0, "Bóng nên nảy xuống (hit 2)");
        assertEquals(10, gameManager.score, "Phải được cộng 10 điểm (hit 2)"); // Test tính điểm
        assertTrue(gameManager.bricks.isEmpty(), "Gạch '2normal' nên bị xóa (hit 2)");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playBrickHit(); // Âm thanh gạch vỡ
    }
}