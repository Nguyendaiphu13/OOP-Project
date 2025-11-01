package com.mygame.mygamearkanoid;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image; // Thêm import
import javafx.scene.paint.Color;
import java.net.URL; // Thêm import
import java.util.ArrayList;
import java.util.List;

public class Brick extends GameObject {
    // 'bricks' list không nên ở đây, nó nên ở trong GameManager
    // public List<Brick> bricks ;

    private int hitPoints;
    private String type;
    private Image brickImage; // Thêm biến cho ảnh

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

    private void loadImage() {
        String imageName = "";
        switch (this.type) {
            case "2normal":
                // Chọn ảnh dựa trên số máu còn lại
                imageName = (this.hitPoints == 2) ? "2normalBrick.png" : "Brick.png";
                break;
            case "expand":
                imageName = "ExpandBrick.png";
                break;
            case "normal":
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

    @Override
    public void update() {
    }

    // Hàm này giờ chỉ dùng làm dự phòng
    private Color getColor() {
        switch (this.type) {
            case "2normal":
                return (this.hitPoints == 2) ? Color.FIREBRICK : Color.ORANGE; // Sửa màu
            case "expand":
                return Color.CYAN;
            case "normal":
            default:
                return Color.GREEN ;
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