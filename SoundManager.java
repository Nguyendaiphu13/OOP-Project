package org.example.demo; // Phải cùng package với Main1.java

import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundManager {

    private AudioClip paddleHitSound;
    private AudioClip brickHitSound;
    private AudioClip hardBrickHitSound;
    private AudioClip gameOverSound;
    private AudioClip openingSound;


    public SoundManager() {
        loadSounds();
    }

    private void loadSounds() {
        // Dấu "/" ở đầu nghĩa là tìm từ gốc của thư mục "resources"
        paddleHitSound = loadClip("/resources_Ball_Paddle.mp3");
        brickHitSound = loadClip("/Ball_Block.mp3");
        hardBrickHitSound = loadClip("/Ball_Hard_Block.mp3");
        gameOverSound = loadClip("/GameOver.mp3");
        openingSound = loadClip("/Opening.mp3");
    }

    // Hàm trợ giúp để tải file, tránh lặp code
    private AudioClip loadClip(String fileName) {
        try {
            URL resource = getClass().getResource(fileName);
            if (resource == null) {
                System.err.println("Không tìm thấy file âm thanh: " + fileName);
                return null;
            }
            return new AudioClip(resource.toExternalForm());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải file âm thanh: " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    // Các hàm public để Main1.java có thể gọi
    public void playPaddleHit() {
        if (paddleHitSound != null) {
            paddleHitSound.play();
        }
    }

    public void playBrickHit() {
        if (brickHitSound != null) {
            brickHitSound.play();
        }
    }

    public void playHardBrickHit() {
        if (hardBrickHitSound != null) {
            hardBrickHitSound.play();
        }
    }

    public void playGameOver() {
        if (gameOverSound != null) {
            gameOverSound.play();
        }
    }
  public void playOpening() {
        if (openingSound != null) {
            openingSound.play();
        }
    }
}
