package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.Animal;
import base.gameobjects.FoodBowl;
import base.gameobjects.Item;
import base.gameobjects.WaterBowl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.gameobjects.AgeStage.BABY;
import static base.gameobjects.Animal.MAX_HUNGER;
import static base.gameobjects.Animal.MAX_THIRST;
import static base.navigationservice.Direction.*;

public class EatingState implements AnimalState {

    protected static final Logger logger = LoggerFactory.getLogger(EatingState.class);

    private int movingTicks = 0;

    @Override
    public void update(Animal animal, Game game) {
        if (isHungerLow(animal) && isNearFood(game, animal)) {
            eatFood(animal);
            updateEatingDirection(animal);
            movingTicks = (10 * animal.getAnimatedSprite().getSpeed());
        } else if (isThirstLow(animal) && isNearWater(game, animal)) {
            drink(animal);
            updateEatingDirection(animal);
            movingTicks = (12 * animal.getAnimatedSprite().getSpeed());
        }

        if (!animal.isHungerLow() && !animal.isThirstLow()) {
            resetSpeedToDefault(animal);
        }

        movingTicks--;

        if (movingTicks > 0) {
            animal.getAnimatedSprite().update(game);
        } else {
            animal.setWaitingState();
        }
    }

    public boolean isHungerLow(Animal animal) {
        return animal.getCurrentHunger() < MAX_HUNGER / 100 * 70;
    }

    public boolean isThirstLow(Animal animal) {
        return animal.getCurrentThirst() < MAX_THIRST / 100 * 70;
    }

    private boolean isNearFood(Game game, Animal animal) {
        for (Item item : game.getGameMap(animal.getCurrentMap()).getItems()) {
            if (item != null && animal.getRectangle().intersects(item.getRectangle())) {
                game.getGameMap(animal.getCurrentMap()).removeItem(item.getItemName(), item.getRectangle());
                return true;
            }
        }
        for (FoodBowl bowl : game.getGameMap(animal.getCurrentMap()).getFoodBowls()) {
            if (bowl.isFull() && animal.getRectangle().intersects(bowl.getRectangle())) {
                bowl.emptyBowl();
                return true;
            }
        }
        return false;
    }

    private void eatFood(Animal animal) {
        logger.info(String.format("%s ate food", animal));
        int hungerInPercent = animal.getCurrentHungerInPercent() >= 50 ? 100 : animal.getCurrentHungerInPercent() + 50;
        animal.setHungerInPercent(hungerInPercent);
        logger.debug(String.format("Hunger level for %s is %d percent", animal, animal.getCurrentHungerInPercent()));
    }

    private boolean isNearWater(Game game, Animal animal) {
        for (WaterBowl bowl : game.getGameMap(animal.getCurrentMap()).getWaterBowls()) {
            if (bowl.isFull() && animal.getRectangle().intersects(bowl.getRectangle())) {
                bowl.emptyBowl();
                return true;
            }
        }
        if (game.isNearWater(animal)) {
            return true;
        }
        return false;
    }

    private void drink(Animal animal) {
        logger.info(String.format("%s drank water", animal));
        animal.setCurrentThirst(MAX_THIRST);
        logger.debug(String.format("Thirst level for %s is 100 percent", animal));
    }

    private void updateEatingDirection(Animal animal) {
        if (animal.getDirection() == UP) {
            animal.setDirection(EAT_UP);
        }
        if (animal.getDirection() == DOWN) {
            animal.setDirection(EAT_DOWN);
        }
        if (animal.getDirection() == LEFT) {
            animal.setDirection(EAT_LEFT);
        }
        if (animal.getDirection() == RIGHT) {
            animal.setDirection(EAT_RIGHT);
        }
        if (animal.getAnimatedSprite().getSpritesSize() < 28) {
            animal.setDirection(STAY);
        }
        animal.updateDirection();
    }

    protected void resetSpeedToDefault(Animal animal) {
        if (BABY.equals(animal.getAge())) {
            animal.setSpeed(2);
        } else {
            animal.setSpeed(3);
        }
    }
}
