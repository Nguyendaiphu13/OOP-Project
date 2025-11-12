package com.mygame.mygamearkanoid.core;

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
        String basePath = "/com/mygame/mygamearkanoid/sounds/";

        paddleHitSound = loadClip(basePath + "resources_Ball_Paddle.mp3");
        brickHitSound = loadClip(basePath + "Ball_Block.mp3");
        hardBrickHitSound = loadClip(basePath + "Ball_Hard_Block.mp3");
        gameOverSound = loadClip(basePath + "GameOver.mp3");
        openingSound = loadClip(basePath + "Opening.mp3");
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
    public void stopAllSounds() {
        if (paddleHitSound != null) {
            paddleHitSound.stop();
        }
        if (brickHitSound != null) {
            brickHitSound.stop();
        }
        if (hardBrickHitSound != null) {
            hardBrickHitSound.stop();
        }
        if (gameOverSound != null) {
            gameOverSound.stop();
        }
        if (openingSound != null) {
            openingSound.stop();
        }
    }
}