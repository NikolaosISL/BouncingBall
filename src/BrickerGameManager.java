import brick_strategies.BasicCollisionStrategy;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import gameobjects.AIPaddle;
import gameobjects.Ball;
import gameobjects.Brick;
import gameobjects.UserPaddle;

import java.awt.*;
import java.util.Random;

public class BrickerGameManager extends GameManager {
    private static final float BORDER_WIDTH = 10;
    private static final float PADDLE_HEIGHT = 20;
    private static final float PADDLE_WIDTH = 150;
    private static final float BALL_RADIUS = 35;
    private static final float BALL_SPEED = 150f;
    private static final int BRICK_HEIGHT = 15;
    private static final int DEFAULT_BRICKS_COLUMNS = 7;
    private static final int DEFAULT_BRICKS_PER_ROW = 8;

    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;

    private int playerLives = 3;

    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowDimensions = windowController.getWindowDimensions();
        this.windowController = windowController;
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop_cut_silenced.wav");
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS), ballImage, collisionSound);
        float ballVelY = BALL_SPEED;
        float ballVelX = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            ballVelX *= -1;
        }
        if (rand.nextBoolean()) {
            ballVelY *= -1;
        }

        ball.setVelocity(new Vector2(ballVelX, ballVelY));

        Vector2 windowDimensions = windowController.getWindowDimensions();
        ball.setCenter(windowDimensions.mult(0.5f));
        this.gameObjects().addGameObject(ball);

        // create background
        Renderable backgroundImage = imageReader.readImage("assets/DARK_BG2_small.jpeg", true);
        GameObject background = new GameObject(Vector2.ZERO, new Vector2(windowDimensions.x(), windowDimensions.y()), backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.gameObjects().addGameObject(background, Layer.BACKGROUND);

        // create walls
        GameObject rightWall = new GameObject(Vector2.ZERO, new Vector2(BORDER_WIDTH, windowDimensions.y()), new danogl.gui.rendering.RectangleRenderable(Color.blue));
        GameObject leftWall = new GameObject(Vector2.ZERO, new Vector2(BORDER_WIDTH, windowDimensions.y()), new danogl.gui.rendering.RectangleRenderable(Color.blue));
        leftWall.setTopLeftCorner(new Vector2(windowDimensions.x()-BORDER_WIDTH, 0));

        this.gameObjects().addGameObject(rightWall, Layer.STATIC_OBJECTS);
        this.gameObjects().addGameObject(leftWall, Layer.STATIC_OBJECTS);

        // create user paddle
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        GameObject userPaddle = new UserPaddle(
                Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage,
                inputListener);
        userPaddle.setCenter(new Vector2(windowDimensions.x()/2, windowDimensions.y()- 30));
        this.gameObjects().addGameObject(userPaddle);

        // create brick
        Renderable brickImage = imageReader.readImage("assets/brick.png", false);
        int brick_columns = DEFAULT_BRICKS_COLUMNS;
        int brick_rows = DEFAULT_BRICKS_PER_ROW;
        int brick_height = BRICK_HEIGHT;
        int brick_width = (int) (windowDimensions.x() / DEFAULT_BRICKS_COLUMNS);
        for (int row = 0; row < brick_rows; row++) {
            for (int col = 0; col < brick_columns; col++) {
                Vector2 newBrickPosition = new Vector2(col * brick_width, row * brick_height);
                Brick newBrick = new Brick(newBrickPosition,
                        new Vector2(brick_width, brick_height),
                        brickImage,
                        new BasicCollisionStrategy(gameObjects()));

                this.gameObjects().addGameObject(newBrick, Layer.DEFAULT);
            }
        }
//        this.gameObjects().addGameObject(brick, Layer.DEFAULT);

        // create ai paddle
//        GameObject aiPaddle = new AIPaddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT), paddleImage, ball);
//        aiPaddle.setCenter(new Vector2(windowDimensions.x()/2, 30));
//        this.gameObjects().addGameObject(aiPaddle);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        checkWinCondition();
    }

    private void checkWinCondition() {
        float ballHeight = ball.getCenter().y();
        String prompt = "";
        if (ballHeight < 0) {
            prompt = "YOU WIN!";
        }

        if (ballHeight > windowDimensions.y()) {
            playerLives--;
            ball.setCenter(new Vector2(windowDimensions.x() / 2, windowDimensions.y() / 2));
            if (playerLives <= 0) {
                prompt = "YOU LOSE!";
            }
        }

        if (!prompt.isEmpty()) {
            prompt += " Play again?";
            if(windowController.openYesNoDialog(prompt)) {
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        // create user interface
        TextRenderable healthInterface = new TextRenderable(Integer.toString(playerLives));
        healthInterface.render(g, new Vector2(30, windowDimensions.y() - 30), new Vector2(30, 30));
//        render(healthInterface);
    }

    public static void main(String[] args) {
        GameManager gameManager = new BrickerGameManager("Bouncing Ball",
                new Vector2(700, 500));

        gameManager.run();

        System.out.println("finished...");
    }
}