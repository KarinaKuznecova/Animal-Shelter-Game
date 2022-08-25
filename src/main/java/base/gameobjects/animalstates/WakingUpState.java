package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;

import static base.gameobjects.Animal.MAX_ENERGY;
import static base.navigationservice.Direction.*;
import static base.navigationservice.Direction.WAKEUP_RIGHT;

public class WakingUpState implements AnimalState {

    private int movingTicks = 0;

    @Override
    public void update(Animal animal, Game game) {
        if (movingTicks == 0) {
            initializeWakingUp(animal);
        } else if (movingTicks == 1) {
            movingTicks--;
            animal.setWaitingState();
        } else {
            movingTicks--;
        }
    }

    private void initializeWakingUp(Animal animal) {
        animal.setCurrentEnergy(MAX_ENERGY);
        if (animal.getDirection() == SLEEP_LEFT) {
            animal.setDirection(WAKEUP_LEFT);
        }
        if (animal.getDirection() == SLEEP_RIGHT) {
            animal.setDirection(WAKEUP_RIGHT);
        }
        animal.updateDirection();
        movingTicks = (3 * animal.getAnimatedSprite().getSpeed()) + 1;
    }
}
