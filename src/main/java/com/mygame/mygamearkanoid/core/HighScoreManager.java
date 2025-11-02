package com.mygame.mygamearkanoid.core;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {

    private List<Integer> highScores;

    public HighScoreManager() {
        this.highScores = new ArrayList<>();
        loadHighScores();
    }

    @SuppressWarnings("unchecked")
    private void loadHighScores() {
        // Dùng hằng số từ StaticFinal
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(StaticFinal.HIGH_SCORE_FILE))) {
            this.highScores = (ArrayList<Integer>) ois.readObject();
            System.out.println("Đã tải " + highScores.size() + " điểm cao.");
        } catch (FileNotFoundException e) {
            System.out.println("Không tìm thấy file high score. Sẽ tạo file mới khi game over.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi khi tải high scores: " + e.getMessage());
            this.highScores = new ArrayList<>();
        }
    }

    private void saveHighScores() {
        // Dùng hằng số từ StaticFinal
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(StaticFinal.HIGH_SCORE_FILE))) {
            oos.writeObject(this.highScores);
            System.out.println("Đã lưu high scores vào file.");
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu high scores: " + e.getMessage());
        }
    }

    public void addScore(int score) {
        if (score <= 0) return;

        highScores.add(score);

        highScores.sort(Collections.reverseOrder());

        // Dùng hằng số từ StaticFinal
        if (highScores.size() > StaticFinal.MAX_SCORES_TO_KEEP) {
            this.highScores = new ArrayList<>(highScores.subList(0, StaticFinal.MAX_SCORES_TO_KEEP));
        }

        saveHighScores();
    }

    public List<Integer> getHighScores() {
        return this.highScores;
    }
}