package gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class UserPaddle extends GameObject {

    private static final float MOVEMENT_SPEED = 300f;
    private final UserInputListener inputListener;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param inputListener
     */
    public UserPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, UserInputListener inputListener) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDir = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }

        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }

        setVelocity(movementDir.mult(MOVEMENT_SPEED));

        if (getTopLeftCorner().x() < 0) {
            setTopLeftCorner(new Vector2(0, getTopLeftCorner().y()));
        }
    }
}
