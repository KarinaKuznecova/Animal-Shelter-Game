package base.events;

import base.Game;
import base.gameobjects.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static base.constants.MapConstants.MAIN_MAP;

public class AdoptionNPCEvent extends Event {

    private static final Logger logger = LoggerFactory.getLogger(AdoptionNPCEvent.class);

    public AdoptionNPCEvent() {
        repeatable = true;
        coolDown = 5;
        currentCoolDown = 2;
    }

    @Override
    void calculateChance(Game game) {
        currentCoolDown--;
        chance = random.nextInt(4);

        if (isThereNpcAlready(game)) {
            currentCoolDown = coolDown;
            chance = 0;
            return;
        }
        if ((!repeatable && happened) || currentCoolDown > 0) {
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
        return game.isThereNpc();
    }

    @Override
    void startEvent(Game game) {
        game.spawnAdoptionNpc(pickAnimal(game), MAIN_MAP);
        happened = true;
        currentCoolDown = coolDown;
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
        return availableAnimals.get(random.nextInt(availableAnimals.size()));
    }
}
