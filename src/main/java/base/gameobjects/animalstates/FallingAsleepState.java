package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;

import static base.navigationservice.Direction.*;
import static base.navigationservice.Direction.SLEEP_RIGHT;

public class FallingAsleepState implements AnimalState {

    private int stepsToFallAsleep = 0;

    @Override
    public void update(Animal animal, Game game) {
        if (stepsToFallAsleep == 0) {
            initializeFallingAsleep(animal);
        }
        if (stepsToFallAsleep == 1) {
            stepsToFallAsleep--;
            animal.setSleepingState();
        } else {
            animal.getAnimatedSprite().update(game);
            stepsToFallAsleep--;
        }
    }

    private void initializeFallingAsleep(Animal animal) {
        stepsToFallAsleep = (3 * animal.getAnimatedSprite().getSpeed()) + 1;
        if (animal.getDirection() == UP || animal.getDirection() == LEFT) {
            animal.setDirection(SLEEP_LEFT);
        } else {
            animal.setDirection(SLEEP_RIGHT);
        }
        animal.updateDirection();
    }
}
