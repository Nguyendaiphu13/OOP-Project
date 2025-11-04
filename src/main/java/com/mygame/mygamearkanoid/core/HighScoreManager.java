package com.mygame.mygamearkanoid.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class HighScoreManager {

    private String getFileForMode(GameMode mode) {
        if (mode == GameMode.ADVENTURE) {
            return StaticFinal.ADVENTURE_HIGHSCORE_FILE;
        } else {
            return StaticFinal.CLASSIC_HIGHSCORE_FILE;
        }
    }

    public void addScore(int score, GameMode mode) {
        if (score <= 0) return;

        String filename = getFileForMode(mode);
        List<Integer> scores = loadScores(filename); // <-- Gọi hàm bị thiếu
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());

        // Giới hạn 4
        while (scores.size() > 4) {
            scores.remove(scores.size() - 1);
        }

        saveScores(scores, filename);
    }

    public List<Integer> getHighScores(GameMode mode) {
        return loadScores(getFileForMode(mode));
    }

    private List<Integer> loadScores(String filename) {
        List<Integer> scores = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            return scores;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextInt()) {
                scores.add(scanner.nextInt());
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải high score: " + e.getMessage());
        }
        return scores;
    }

    private void saveScores(List<Integer> scores, String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            for (int score : scores) {
                writer.println(score);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu high score: " + e.getMessage());
        }
    }
}