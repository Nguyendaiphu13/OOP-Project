package com.mygame.mygamearkanoid.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.net.URL;


public class Brick extends MovableObject {
    private int hitPoints;
    private String type;
    private Image brickImage;

    public Brick(double x, double y, int width, int height, String type) {
        super(x, y, width, height);

        this.type = type;

        switch (this.type) {
            case "2normal":
                this.hitPoints = 2;
                break;
            case "expand":
            case "fast_ball":
            case "normal":
            default:
                this.hitPoints = 1;
                break;
        }

        loadImage();
    }

    public int getHitPoints() {
        return this.hitPoints;
    }

    private void loadImage() {
        String imageName = switch (this.type) {
            case "2normal" ->
                    (this.hitPoints == 2) ? "2normalBrick.png" : "Brick.png";
            case "expand" -> "ExpandBrick.png";
            case "fast_ball" -> "ExpandBrick.png";
            default -> "Brick.png";
        };

        String imagePath = "/com/mygame/mygamearkanoid/images/" + imageName;
        try {
            URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                this.brickImage = new Image(imgUrl.toExternalForm());
            } else {
                System.err.println("Không thể tìm thấy ảnh: " + imagePath);
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
            if (this.type.equals("2normal") && this.hitPoints == 1) {
                loadImage();
            }
        }
    }

    public boolean isDestroyed() {
        return hitPoints == 0;
    }


    @Override
    public void render(GraphicsContext gc) {
        if (this.brickImage != null) {
            gc.drawImage(this.brickImage, this.x, this.y, this.width, this.height);
        } else {
            gc.fillRect(this.x, this.y, this.width, this.height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(this.x, this.y, this.width, this.height);
        }
    }

    // Brick kế thừa update() từ MovableObject.
    // Vì dx, dy = 0, nên update() sẽ không làm gì,
    // trừ khi nó là một MoveBrick.
}