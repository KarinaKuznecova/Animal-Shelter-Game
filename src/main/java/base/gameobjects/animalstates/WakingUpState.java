package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;

import static base.constants.Constants.MAX_ENERGY;
import static base.navigationservice.Direction.*;

public class WakingUpState implements AnimalState {

    private int movingTicks = 0;

    @Override
    public void update(Animal animal, Game game) {
        if (movingTicks >= 1) {
            animal.getAnimatedSprite().update(game);
        } else {
            if (animal.getDirection() == WAKEUP_LEFT) {
                animal.setDirection(LEFT);
            }
            if (animal.getDirection() == WAKEUP_RIGHT) {
                animal.setDirection(RIGHT);
            }
            animal.getAnimatedSprite().setSpeed(10);
            animal.setWaitingState(20);
        }
        movingTicks--;
    }

    public void initializeWakingUp(Animal animal) {
        animal.setCurrentEnergy(MAX_ENERGY);
        if (animal.getDirection() == SLEEP_LEFT) {
            animal.setDirection(WAKEUP_LEFT);
        }
        if (animal.getDirection() == SLEEP_RIGHT) {
            animal.setDirection(WAKEUP_RIGHT);
        }
        animal.updateDirection();
        animal.getAnimatedSprite().reset();
        animal.getAnimatedSprite().setSpeed(20);
        movingTicks = (4 * animal.getAnimatedSprite().getSpeed()) - 1;
    }
}
