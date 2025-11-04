package com.mygame.mygamearkanoid.ui;

import com.mygame.mygamearkanoid.core.StaticFinal;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.InputStream;

public class UIManager {

    public static Button createIconButton(ImageView icon) {
        Button button = new Button();
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        button.setOnMouseEntered(e -> icon.setOpacity(0.8));
        button.setOnMouseExited(e -> icon.setOpacity(1.0));
        button.setOnMousePressed(e -> {
            button.setScaleX(0.95);
            button.setScaleY(0.95);
        });
        button.setOnMouseReleased(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        return button;
    }

    public static Image loadImage(String path) {
        try {
            InputStream is = UIManager.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("KHÔNG THỂ TÌM THẤY TỆP: " + path);
                return null;
            }
            return new Image(is);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải ảnh: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static Background createBackgroundImage(Image image) {
        if (image == null) {
            return null;
        }
        BackgroundImage bgImg = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                // Sửa: Dùng biến static từ StaticFinal
                new BackgroundSize(StaticFinal.SCREEN_WIDTH, StaticFinal.SCREEN_HEIGHT, false, false, false, false)
        );
        return new Background(bgImg);
    }
}