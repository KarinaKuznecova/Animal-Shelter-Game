package base.events;

import base.Game;
import base.gameobjects.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPCEvent extends Event {

    private static final Logger logger = LoggerFactory.getLogger(NPCEvent.class);

    public NPCEvent() {
        repeatable = true;
    }

    @Override
    void calculateChance(Game game) {
        chance = 1;

        if ((!repeatable && happened) || isThereNpcAlready(game)) {
            chance = 0;
            return;
        }
        if (happened) {
            chance--;
        }
        boolean isAvailableAnimal = false;
        for (List<Animal> animalList : game.getAnimalsOnMaps().values()) {
            for (Animal animal : animalList) {
                if (!animal.isFavorite()) {
                    chance++;
                    isAvailableAnimal = true;
                }
            }
        }
        if (!isAvailableAnimal) {
            chance = 0;
            return;
        }
        if (game.isBackpackEmpty()) {
            chance++;
        }
        logger.info(String.format("Event 'NPC comes to adopt' chance is %d", chance));
    }

    private boolean isThereNpcAlready(Game game) {
        return !game.getNpcs().isEmpty();
    }

    @Override
    void startEvent(Game game) {
        game.spawnNpc(pickAnimal(game));
        happened = true;
    }

    public Animal pickAnimal(Game game) {
        List<Animal> availableAnimals = new ArrayList<>();
        for (List<Animal> animalList : game.getAnimalsOnMaps().values()) {
            for (Animal animal : animalList) {
                if (!animal.isFavorite()) {
                    availableAnimals.add(animal);
                }
            }
        }
        return availableAnimals.get(new Random().nextInt(availableAnimals.size()));
    }
}
