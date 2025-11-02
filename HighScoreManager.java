package com.mygame.mygamearkanoid;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Quản lý việc Tải, Lưu, và Sắp xếp Bảng Xếp Hạng.
 * Lớp này chỉ làm việc với điểm số (Integer).
 */
public class HighScoreManager {

    // Danh sách chỉ chứa điểm số
    private List<Integer> highScores;

    // Tên file để lưu
    private static final String HIGH_SCORE_FILE = "ark_highscores.dat";

    // Giới hạn chỉ 10 điểm cao nhất
    private static final int MAX_SCORES_TO_KEEP = 5;

    public HighScoreManager() {
        this.highScores = new ArrayList<>();
        loadHighScores(); // Tải điểm cũ ngay khi khởi tạo
    }

    /**
     * Tải danh sách điểm từ file
     */
    @SuppressWarnings("unchecked") // Bỏ qua cảnh báo khi ép kiểu
    private void loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            // Đọc toàn bộ ArrayList<Integer> từ file
            this.highScores = (ArrayList<Integer>) ois.readObject();
            System.out.println("Đã tải " + highScores.size() + " điểm cao.");
        } catch (FileNotFoundException e) {
            // Lỗi này là bình thường nếu đây là lần đầu chạy
            System.out.println("Không tìm thấy file high score. Sẽ tạo file mới khi game over.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi khi tải high scores: " + e.getMessage());
            this.highScores = new ArrayList<>(); // Tạo list mới nếu file bị lỗi
        }
    }

    /**
     * Ghi đè danh sách điểm hiện tại vào file
     */
    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(this.highScores);
            System.out.println("Đã lưu high scores vào file.");
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu high scores: " + e.getMessage());
        }
    }

    /**
     * Hàm quan trọng: Thêm một điểm mới vào danh sách
     */
    public void addScore(int score) {
        if (score <= 0) return; // Không lưu điểm 0

        highScores.add(score);

        // Sắp xếp danh sách: từ cao xuống thấp
        highScores.sort(Collections.reverseOrder());

        // Cắt danh sách, chỉ giữ lại TOP 10
        if (highScores.size() > MAX_SCORES_TO_KEEP) {
            this.highScores = new ArrayList<>(highScores.subList(0, MAX_SCORES_TO_KEEP));
        }

        // Lưu lại file
        saveHighScores();
    }

    /**
     * Lấy danh sách điểm hiện tại (đã sắp xếp)
     */
    public List<Integer> getHighScores() {
        return this.highScores;
    }
}