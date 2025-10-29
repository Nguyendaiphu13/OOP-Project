package org.example.demo; 

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
        paddleHitSound = loadClip("/resources_Ball_Paddle.mp3");
        brickHitSound = loadClip("/Ball_Block.mp3");
        hardBrickHitSound = loadClip("/Ball_Hard_Block.mp3");
        gameOverSound = loadClip("/GameOver.mp3");
        openingSound = loadClip("/Opening.mp3");
    }

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
