package com.mygame.mygamearkanoid.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class HighScoreManager {

    /**
     * HÀM PHỤ 1 (BỊ THIẾU):
     * Trả về tên file .txt dựa trên chế độ chơi.
     */
    private String getFileForMode(GameMode mode) {
        if (mode == GameMode.ADVENTURE) {
            return StaticFinal.ADVENTURE_HIGHSCORE_FILE;
        } else {
            return StaticFinal.CLASSIC_HIGHSCORE_FILE;
        }
    }

    /**
     * HÀM BẠN ĐÃ SỬA (ĐÚNG):
     * Thêm điểm mới vào file high score của chế độ chơi (mode) tương ứng.
     */
    public void addScore(int score, GameMode mode) {
        if (score <= 0) return;

        String filename = getFileForMode(mode);
        List<Integer> scores = loadScores(filename); // <-- Gọi hàm bị thiếu
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());

        // Giới hạn 10 điểm
        while (scores.size() > 10) {
            scores.remove(scores.size() - 1);
        }

        saveScores(scores, filename); // <-- Gọi hàm bị thiếu
    }

    /**
     * HÀM ĐÃ SỬA (QUAN TRỌNG):
     * Lấy danh sách điểm cao của chế độ chơi (mode) tương ứng.
     */
    public List<Integer> getHighScores(GameMode mode) {
        return loadScores(getFileForMode(mode));
    }

    /**
     * HÀM PHỤ 2 (BỊ THIẾU):
     * Tải danh sách điểm từ một file.
     */
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

    /**
     * HÀM PHỤ 3 (BỊ THIẾU):
     * Lưu danh sách điểm vào một file.
     */
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