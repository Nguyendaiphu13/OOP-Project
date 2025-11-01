package com.mygame.mygamearkanoid;

import javafx.scene.image.Image;
import java.net.URL;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color; // Thêm import này

public class Paddle extends MovableObject {
    protected double speed;
    private Image paddleImage;

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public double getSpeed() {
        return speed;
    }
    public Paddle(double x, double y, int width, int height,double speed) {
        super(x, y, width, height);
        this.speed = speed;
        loadImage();
    }

    private void loadImage() {
        // Sửa lại đường dẫn thành đường dẫn tuyệt đối từ gốc resources
        String imagePath = "/com/mygame/mygamearkanoid/images/Paddle.png";
        try {
            URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                this.paddleImage = new Image(imgUrl.toExternalForm());
            } else {
                System.err.println("Không thể tìm thấy ảnh: " + imagePath);
                this.paddleImage = null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + imagePath);
            e.printStackTrace();
            this.paddleImage = null;
        }
    }

    @Override // Thêm annotation @Override
    public void render(GraphicsContext gc) {
        if (this.paddleImage != null) {
            gc.drawImage(this.paddleImage, this.x, this.y, this.width, this.height);
        } else {
            // Vẽ hình dự phòng nếu không tải được ảnh
            gc.setFill(Color.BLUE);
            gc.fillRect(this.x, this.y, this.width, this.height);
        }
    }

    @Override // Thêm annotation @Override
    public void update() {
        move();
        // Logic giới hạn tường đã được chuyển sang Main1 (hoặc GameEngine)
        // nên không cần check ở đây nữa.
    }

    // Các hàm moveLeft/moveRight không cần thiết vì Main1 setDx trực tiếp
    // public void moveLeft() { ... }
    // public void moveRight() { ... }
}