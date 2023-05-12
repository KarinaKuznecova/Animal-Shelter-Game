package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;
import base.gameobjects.animaltraits.Trait;
import base.graphicsservice.RenderHandler;

import java.util.Random;

public class LoveState implements AnimalState {

    private int loveTimer = 100;
    private final Random random = new Random();

    @Override
    public void update(Animal animal, Game game) {
        animal.updateHeart(game);
        if (loveTimer > 0) {
            loveTimer--;
        }
        if (loveTimer <= 0) {
            if (animal.getPersonality().contains(Trait.WILD) && random.nextInt(12) < 1) {
                animal.getPersonality().remove(Trait.WILD);
            } else if (!animal.getPersonality().contains(Trait.FRIENDLY) && !animal.getPersonality().contains(Trait.WILD) && random.nextInt(12) < 1) {
                animal.getPersonality().add(Trait.FRIENDLY);
            }
            animal.setWaitingState(30);
            loveTimer = 100;
        }
    }

    public void render(Animal animal, RenderHandler renderHandler) {
        animal.getHeartIcon().render(renderHandler, 1);
    }
}
