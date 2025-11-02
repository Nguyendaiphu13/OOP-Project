package org.example.demo;

import javafx.scene.canvas.GraphicsContext; // Thêm import
import javafx.scene.image.Image; // Thêm import
import javafx.scene.paint.Color; // Thêm import
import java.net.URL; // Thêm import

public class Ball extends MovableObject {
    public int radius;
    public double speed ;
    public double directionX ;
    public double directionY ;
    public boolean alive = true;

    private Image ballImage; // Thêm biến cho ảnh

    // constructor
    public Ball(double x, double y, int width, int height, double speed, double directionX, double directionY, int radius) {
        super(x, y, width, height, directionX * speed, directionY * speed);
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;
        this.radius = radius;
        loadImage(); // Gọi hàm tải ảnh
    }

    private void loadImage() {
        String imagePath = "/com/mygame/mygamearkanoid/images/Ball.png";
        try {
            URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                this.ballImage = new Image(imgUrl.toExternalForm());
            } else {
                System.err.println("Không thể tìm thấy ảnh: " + imagePath);
                this.ballImage = null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + imagePath);
            e.printStackTrace();
            this.ballImage = null;
        }
    }

    // Thêm hàm render()
    @Override
    public void render(GraphicsContext gc) {
        if (this.ballImage != null) {
            gc.drawImage(this.ballImage, this.x, this.y, this.width, this.height);
        } else {
            // Vẽ hình dự phòng nếu không tải được ảnh
            gc.setFill(Color.WHITE);
            gc.fillOval(this.x, this.y, this.width, this.height);
        }
    }

    // getter setter
    public int getRadius() {
        return radius;
    }
    // ... (Các hàm getter/setter khác giữ nguyên) ...
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if (speed <= 0) return;
        this.speed = speed;
        updateVelocity(); // cập nhật vận tốc khi tốc độ thay đổi
    }

    public void updateVelocity() {
        this.dx = this.directionX * this.speed;
        this.dy = this.directionY * this.speed;
    }

    @Override
    public void update() {
        this.x += this.dx;
        this.y += this.dy;
    }
}
