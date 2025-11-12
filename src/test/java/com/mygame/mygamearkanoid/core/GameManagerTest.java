package com.mygame.mygamearkanoid.core;

import com.mygame.mygamearkanoid.entities.Ball;
import com.mygame.mygamearkanoid.entities.Brick;
import com.mygame.mygamearkanoid.entities.Paddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameManagerTest {

    // 2. DÙNG @SPY THAY VÌ "private GameManager gameManager;"
    // Spy cho phép chúng ta gọi hàm thật, NHƯNG "tắt" (mock) một số hàm cụ thể
    @Spy
    private GameManager gameManager = new GameManager();

    @Mock
    private SoundManager mockSoundManager;

    @BeforeEach
    void setUp() {
        gameManager.gameState = GameState.PLAYING;
        gameManager.gameMode = GameMode.CLASSIC;

        // Khởi tạo Paddle và Ball
        gameManager.paddle = new Paddle(
                (StaticFinal.SCREEN_WIDTH - StaticFinal.PADDLE_WIDTH_DEFAULT) / 2.0,
                StaticFinal.SCREEN_HEIGHT - StaticFinal.PADDLE_HEIGHT_DEFAULT - StaticFinal.PADDLE_Y_OFFSET,
                StaticFinal.PADDLE_WIDTH_DEFAULT,
                StaticFinal.PADDLE_HEIGHT_DEFAULT,
                StaticFinal.PADDLE_SPEED
        );
        gameManager.ball = new Ball(
                400, 300,
                StaticFinal.BALL_RADIUS * 2,
                StaticFinal.BALL_RADIUS * 2,
                StaticFinal.BALL_SPEED,
                0.5, 0.5,
                StaticFinal.BALL_RADIUS
        );


        Mockito.doNothing().when(gameManager).setupGameObjects();
    }

    // ========== 1. KIỂM TRA VA CHẠM TƯỜNG (Logic trong GameManager) ==========

    @Test
    void testWallCollision_HitBottomWall_LosesLife() {
        gameManager.lives = 3;
        double ballHeight = gameManager.ball.getHeight();

        gameManager.ball.setY(StaticFinal.SCREEN_HEIGHT - ballHeight + 1.0);
        gameManager.ball.directionY = 1.0; // Hướng đi xuống
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        assertEquals(2, gameManager.lives, "Người chơi nên mất 1 mạng");
        assertTrue(gameManager.ball.alive, "Bóng nên được hồi sinh");
    }

    // ========== 2. KIỂM TRA VA CHẠM PADDLE ==========

    @Test
    void testPaddleCollision_CenterHit() {
        // Arrange
        gameManager.ball.setX(gameManager.paddle.getX() + (gameManager.paddle.getWidth() / 2.0) - (gameManager.ball.getWidth() / 2.0));
        gameManager.ball.setY(gameManager.paddle.getY() - gameManager.ball.getHeight() - 1);
        gameManager.ball.directionY = 1.0;
        gameManager.ball.directionX = 0.0;
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        assertTrue(gameManager.ball.directionY < 0, "Bóng nên nảy lên (directionY < 0)");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playPaddleHit();
    }


    // ========== 3. KIỂM TRA VA CHẠM GẠCH & TÍNH ĐIỂM ==========

    @Test
    void testBrickCollision_NormalBrick_And_Scoring() {
        // Xóa gạch
        gameManager.bricks.clear();
        gameManager.score = 0;

        Brick brick = new Brick(400, 100, StaticFinal.BRICK_WIDTH, StaticFinal.BRICK_HEIGHT, "normal");
        gameManager.bricks.add(brick);

        // Đặt bóng ngay dưới gạch
        gameManager.ball.setX(410);
        gameManager.ball.setY(121);
        gameManager.ball.directionY = -1.0; // Hướng đi lên
        gameManager.ball.directionX = 0.0;
        gameManager.ball.updateVelocity();

        // Act
        gameManager.updateGame(mockSoundManager);

        // Assert
        assertTrue(gameManager.ball.directionY > 0, "Bóng nên nảy xuống (directionY > 0)");
        assertEquals(10, gameManager.score, "Điểm phải tăng lên 10");
        assertTrue(gameManager.bricks.isEmpty(), "Viên gạch 'normal' nên bị phá hủy và xóa khỏi danh sách");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playBrickHit();
    }

    @Test
    void testBrickCollision_HardBrick_TwoHits() {
        // Xóa gạch
        gameManager.bricks.clear();
        gameManager.score = 0;

        Brick hardBrick = new Brick(400, 100, StaticFinal.BRICK_WIDTH, StaticFinal.BRICK_HEIGHT, "2normal");
        gameManager.bricks.add(hardBrick);

        // --- LƯỢT VA CHẠM 1 ---
        gameManager.ball.setX(410);
        gameManager.ball.setY(121);
        gameManager.ball.directionY = -1.0;
        gameManager.ball.updateVelocity();
        gameManager.updateGame(mockSoundManager);

        assertEquals(0, gameManager.score, "Chưa được cộng điểm (hit 1)");
        assertEquals(1, gameManager.bricks.size(), "Gạch '2normal' không nên bị xóa (hit 1)");
        assertEquals(1, hardBrick.getHitPoints(), "Gạch '2normal' phải còn 1 máu");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playHardBrickHit();

        // --- LƯỢT VA CHẠM 2 ---
        gameManager.ball.setY(121);
        gameManager.ball.directionY = -1.0;
        gameManager.ball.updateVelocity();
        gameManager.updateGame(mockSoundManager);

        assertEquals(10, gameManager.score, "Phải được cộng 10 điểm (hit 2)");
        assertTrue(gameManager.bricks.isEmpty(), "Gạch '2normal' nên bị xóa (hit 2)");
        Mockito.verify(mockSoundManager, Mockito.times(1)).playBrickHit();
    }
}