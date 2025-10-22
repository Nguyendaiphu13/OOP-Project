package org.example.demo;

// check va chạm

public class CheckCollision {

    // kiểm tra va chạm
    public static boolean intersects(GameObject obj1, GameObject obj2) {
        return !(obj1.getX() + obj1.getWidth() < obj2.getX()
                || obj1.getX() > obj2.getX() + obj2.getWidth()
                || obj1.getY() + obj1.getHeight() < obj2.getY()
                || obj1.getY() > obj2.getY() + obj2.getHeight());
    }

    // va chạm với tường
    public static void checkWallCollision(Ball ball, int screenWidth, int screenHeight) {
        boolean directionChanged = false;
        // chạm tường trái hoặc phải thì đổi hướng x
        if (ball.getX() <= 0) {
            ball.directionX *= -1;
            ball.setX(0);
            directionChanged = true;
        } else if (ball.getX() + ball.getWidth() >= screenWidth) {
            ball.directionX *= -1;
            ball.setX(screenWidth - ball.getWidth());
            directionChanged = true;
        }

        // va chạm tường trên đổi hướng y
        if (ball.getY() <= 0) {
            ball.directionY *= -1;
            ball.setY(0);
            directionChanged = true;
        }

        // rơi ra màn hình thì - 1 mạng
        if (ball.getY() + ball.getHeight() >= screenHeight) {
            ball.alive = false;
        }

        // cập nhật lại vận tốc nếu hướng thay đổi
        if (directionChanged) {
            ball.updateVelocity();
        }
    }

    // check va chạm với obj khác như brick và paddle
    public static void bounceOff(Ball ball, GameObject other) {
        if (other instanceof Brick) {
            // lấy tọa độ tâm của bóng và gạch
            double ballCenterX = ball.getX() + ball.getWidth() / 2.0;
            double ballCenterY = ball.getY() + ball.getHeight() / 2.0;
            double brickCenterX = other.getX() + other.getWidth() / 2.0;
            double brickCenterY = other.getY() + other.getHeight() / 2.0;

            /* (ball.getWidth() / 2.0 + other.getWidth() / 2.0) đây là khoảng cách tối thiểu giữa 2 tâm
                Math.abs(ballCenterX - brickCenterX) : đây là khoảng cách thực tế giữa 2 ta
             */

            /* overlapX là độ lún của bóng so với bề ngang của gạch, nếu
            overlapX mà > 0 thì nghĩa là nó đang lún vào theo chiều ngang
             */
            double overlapX = (ball.getWidth() / 2.0 + other.getWidth() / 2.0) - Math.abs(ballCenterX - brickCenterX);
            double overlapY = (ball.getHeight() / 2.0 + other.getHeight() / 2.0) - Math.abs(ballCenterY - brickCenterY);

            if (overlapX < overlapY) {
                // overlapX < overlapY là độ lún chiều ngang lớn hơn chiều dọc -> va chạm xảy ra ở cạnh bên
                ball.directionX *= -1;
                /* nếu vận tốc dx trước va chạm là dương (bóng đang đi sang phải)
                thì có nghĩa là nó đã va vào cạnh trái của gạch :
                -> cho bóng lùi lại
                 */
                if (ball.dx > 0) {
                    ball.setX(ball.getX() - overlapX);
                } else {
                    ball.setX(ball.getX() + overlapX);
                }
            } else {
                ball.directionY *= -1;
                if (ball.dy > 0) {
                    // tương tự
                    ball.setY(ball.getY() - overlapY);
                } else {
                    ball.setY(ball.getY() + overlapY);

                }
            }

            } else if (other instanceof Paddle paddle) {
                // va chạm với paddle
                double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
                double ballCenter = ball.getX() + ball.getWidth() / 2.0;

                // Tính toán vị trí va chạm tương đối trên thanh trượt (-1 đến 1)
                double hitPos = (ballCenter - paddleCenter) / (paddle.getWidth() / 2.0);
                hitPos = Math.max(-1.0, Math.min(1.0, hitPos)); // Giới hạn giá trị

                // góc nảy tối đa
                double maxAngle = Math.toRadians(60);
                double angle = hitPos * maxAngle;

                // tính toán hướng mới dựa trên góc
                ball.directionX = Math.sin(angle);
                ball.directionY = -Math.cos(angle); // Hướng lên trên
            }

            // ập nhật lại vận tốc của bóng sau khi đổi hướng
            ball.updateVelocity();
        }
    }
