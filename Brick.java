package org.example.demo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends GameObject {
    private int hitPoints;
    private String type;

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
    }

    public String getType() {
        return type;
    }

    public void takeHit() {
        if (this.hitPoints > 0) {
            this.hitPoints--;
        }
    }

    public boolean isDestroyed() {
        return hitPoints == 0;
    }

    @Override
    public void update() {
    }

    private Color getColor() {
        switch (this.type) {
            case "2normal":
                return (this.hitPoints == 2) ? Color.FIREBRICK : Color.GREEN; // Sửa RED thành FIREBRICK (JavaFX có)
            case "expand":
                return Color.CYAN;
            case "normal":
            default:
                return Color.GREEN ;
        }
    }

    public void render(GraphicsContext gc) {

        gc.setFill(this.getColor());

        gc.fillRect(this.x, this.y, this.width, this.height);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(this.x, this.y, this.width, this.height);
    }
}