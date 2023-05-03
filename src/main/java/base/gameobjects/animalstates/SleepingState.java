package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;

public class SleepingState implements AnimalState {

    public static final int MAX_ENERGY = 40_000;
    protected static final int SLEEPING_SPEED = 15;

    @Override
    public void update(Animal animal, Game game) {
        if (!animal.isFeral()) {
            animal.setCurrentEnergy(animal.getCurrentEnergy() + SLEEPING_SPEED);
        }
        if (animal.getCurrentEnergy() >= MAX_ENERGY) {
            animal.setWakingUpState();
        }
    }
}
