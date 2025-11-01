package com.mygame.mygamearkanoid;

import java.io.Serializable;

// Lớp này dùng để lưu thông tin cốt lõi của một viên gạch
public class BrickSave implements Serializable {
    private static final long serialVersionUID = 1L; // Cần cho Serializable

    double x, y;
    int width, height;
    String type;
    int hitPoints;

    // Cần một constructor rỗng
    public BrickSave() {}

    // Constructor để gán dữ liệu từ một đối tượng Brick thật
    public BrickSave(Brick brick) {
        this.x = brick.getX();
        this.y = brick.getY();
        this.width = brick.getWidth();
        this.height = brick.getHeight();
        this.type = brick.getType();

        // Chúng ta cần thêm một getter cho hitPoints trong file Brick.java
        this.hitPoints = brick.getHitPoints();
    }
}