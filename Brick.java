package org.example.demo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image; // Thêm import
import javafx.scene.paint.Color;
import java.net.URL; // Thêm import


public class Brick extends GameObject {
    private int hitPoints;
    private String type;
    private Image brickImage; // Thêm biến cho ảnh

    public int BRICK_ROWS = 6;
    public int BRICK_COLS = 11;
    public int BRICK_WIDTH = 70;
    public int BRICK_HEIGHT = 20;
    public double BRICK_START_X = 45;
    public double BRICK_START_Y = 90;
    public double BRICK_GAP_X = 3;
    public double BRICK_GAP_Y = 3;

    public Brick(double x, double y, int width, int height, String type) {
        super(x, y, width, height);
        this.type = type;

        switch (this.type) {
            case "2normal":
                this.hitPoints = 2;
                break;
            case "expand":
            case "normal":
            default:
                this.hitPoints = 1;
                break;
        }

        loadImage(); // Tải ảnh dựa trên loại và máu
    }

    public int getHitPoints() {
        return this.hitPoints;
    }

    private void loadImage() {
        String imageName; // Khai báo biến ở ngoài
        switch (this.type) {
            case "2normal":
                // Chọn ảnh dựa trên số máu còn lại
                imageName = (this.hitPoints == 2) ? "2normalBrick.png" : "Brick.png";
                break;
            case "expand":
                imageName = "ExpandBrick.png";
                break;
            default:
                imageName = "Brick.png";
                break;
        }

        String imagePath = "/com/mygame/mygamearkanoid/images/" + imageName;
        try {
            URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                this.brickImage = new Image(imgUrl.toExternalForm());
            } else {
                System.err.println("Không tìm thấy ảnh: " + imagePath);
                this.brickImage = null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + imagePath);
            this.brickImage = null;
        }
    }

    public String getType() {
        return type;
    }

    public void takeHit() {
        if (this.hitPoints > 0) {
            this.hitPoints--;

            // Nếu là gạch 2 máu, tải lại ảnh (Brick_1HP.png)
            if (this.type.equals("2normal") && this.hitPoints == 1) {
                loadImage();
            }
        }
    }

    public boolean isDestroyed() {
        return hitPoints == 0;
    }

    // Hàm này giờ chỉ dùng làm dự phòng
    private Color getColor() {
        switch (this.type) {
            case "2normal":
                return (this.hitPoints == 2) ? Color.FIREBRICK : Color.ORANGE;
            case "expand":
                return Color.CYAN;
            default:
                return Color.GREEN;
        }
    }

    @Override // Thêm annotation @Override
    public void render(GraphicsContext gc) {
        if (this.brickImage != null) {
            gc.drawImage(this.brickImage, this.x, this.y, this.width, this.height);
        } else {
            // Vẽ hình dự phòng nếu không tải được ảnh
            gc.setFill(this.getColor());
            gc.fillRect(this.x, this.y, this.width, this.height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(this.x, this.y, this.width, this.height);
        }
    }
}

