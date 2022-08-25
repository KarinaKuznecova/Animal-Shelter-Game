package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;

public interface AnimalState {

    void update(Animal animal, Game game);
}
