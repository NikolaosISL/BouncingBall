package brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import gameobjects.Brick;
import gameobjects.Ball;

public class BasicCollisionStrategy implements CollisionStrategy {
    GameObjectCollection gameObjectCollection;

    public BasicCollisionStrategy(GameObjectCollection gameObjectCollection) {
        this.gameObjectCollection = gameObjectCollection;
    }

    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        if (otherObj instanceof Ball) {
            gameObjectCollection.removeGameObject(thisObj);
        }

        System.out.println("Collision detected sir!");
    }
}
