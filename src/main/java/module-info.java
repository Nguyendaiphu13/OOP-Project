module com.mygame.mygamearkanoid {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;

    exports com.mygame.mygamearkanoid.data;
    exports com.mygame.mygamearkanoid.entities;
    exports com.mygame.mygamearkanoid.ui;
    exports com.mygame.mygamearkanoid.core;
}