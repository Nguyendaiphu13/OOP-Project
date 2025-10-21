import java.util.List;
import java.util.ArrayList;
public class GameManager {
    public Paddle paddle;
    public Ball ball;
    public List<Brick> bricks;
    public List<PowerUp> powerUps;
    public int score;
    public int lives;
    public String gameState; // trang thái game

    public GameManager() {
        this.bricks = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.score = 0;
        this.lives = 3;
        this.gameState = "Đang chơi"; // trạng thái đang  chs game
    }

    public void startGame() {
        System.out.println("Chơi");
    }

    public void updateGame() {
        // chưa cập nhật

        if (lives <= 0) {
            gameOver(); // thuaaaaa
        }
    }



    public void gameOver() {
        gameState = "Thua"; // sau khi thua đổi trạng thái
        System.out.println("Final score: " + score);
    }
}
