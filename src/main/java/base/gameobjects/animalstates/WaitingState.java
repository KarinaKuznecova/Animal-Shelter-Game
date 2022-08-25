package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class WaitingState implements AnimalState {

    protected static final Logger logger = LoggerFactory.getLogger(WaitingState.class);

    private int currentWaitingTicks;

    @Override
    public void update(Animal animal, Game game) {
        if (currentWaitingTicks == 0) {
            initializeWaiting();
        } else if (currentWaitingTicks == 1) {
            animal.setWalkingState();
        } else {
            logger.debug(String.format("%s is waiting for %d", animal, currentWaitingTicks));
            currentWaitingTicks--;
        }
    }

    private void initializeWaiting() {
        currentWaitingTicks = new Random().nextInt(40) + 10;
    }
}
