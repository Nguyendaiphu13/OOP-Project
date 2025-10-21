public class Ball extends MovableObject{
        public int radius; // kích cỡ quả bóng
        public double speed = 3.0;
        public double directionX = 1;
        public double directionY = -1 ;
        public boolean alive = true;

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public Ball(double x, double y, int width, int height, double speed, double directionX, double directionY, int radius) {
        super(x, y, width, height, directionX * speed, directionY * speed);
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;
        this.radius = radius;
    }

    public boolean checkCollision(GameObject other) {

        double ballLeft = this.x;
        double ballRight = this.x + this.width;
        double ballTop = this.y;
        double ballBottom = this.y + this.height;

        double otherLeft = other.getX();
        double otherRight = other.getX() + other.getWidth();
        double otherTop = other.getY();
        double otherBottom = other.getY() + other.getHeight();
        if (ballRight <= otherLeft || ballLeft >= otherRight || ballBottom <= otherTop || ballTop >= otherBottom)
        {
            return false;
        }
        bounceOff(other);
        return true;
    }

    // va chạm với tường
    public void checkWallCollision(int screenWidth, int screenHeight) {
        if(x <= 0 || x + width >= screenWidth ){
            directionX *= -1;
            dx = directionX * speed ;
        }

        if (y <= 0) {
            directionY *= -1;
            dy = directionY * speed;
        }

        if(y + height >= screenHeight){
            alive = false ;
        }
    }

    // Va chạm với các Object khác
    public void bounceOff(GameObject other) {
        if(other instanceof Brick) { // Va chạm với gạch
            directionY *= -1;
        }
        // Va chạm với paddle
        if (other instanceof Paddle) {
            Paddle paddle = (Paddle) other;

            double paddleCenter = paddle.x + paddle.width / 2.0;
            double ballCenter = x + width / 2.0;

            // Hit position trong [-1, 1]
            double hitPos = (ballCenter - paddleCenter) / (paddle.width / 2.0);
            hitPos = Math.max(-1.0, Math.min(1.0, hitPos));

            double maxAngle = Math.toRadians(60); // tối đa ±60 độ
            double angle = hitPos * maxAngle;

            directionX = Math.sin(angle);
            directionY = -Math.cos(angle); // hướng lên trên

            dx = directionX * speed;
            dy = directionY * speed;
        }
    }

}
