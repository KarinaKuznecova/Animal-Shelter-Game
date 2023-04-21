package base.gameobjects.animalstates;

import base.Game;
import base.gameobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static base.constants.Constants.MAX_HUNGER;
import static base.constants.Constants.MAX_THIRST;
import static base.navigationservice.Direction.*;

public class EatingState implements AnimalState {

    protected static final Logger logger = LoggerFactory.getLogger(EatingState.class);

    private int movingTicks = 0;

    private final String RAW_MEAL = "rawMeal";
    private String foodTypeEating = RAW_MEAL;

    @Override
    public void update(Animal animal, Game game) {
        if (isHungerLow(animal) && isNearFood(game, animal)) {
            eatFood(animal, getSaturationAmount(foodTypeEating));
            updateEatingDirection(animal);
            movingTicks = (16 * animal.getAnimatedSprite().getSpeed());
        } else if (isThirstLow(animal) && isNearWater(game, animal)) {
            drink(animal);
            updateEatingDirection(animal);
            movingTicks = (16 * animal.getAnimatedSprite().getSpeed());
        }

        movingTicks--;

        if (movingTicks > 0) {
            animal.getAnimatedSprite().update(game);
        } else {
            animal.setWaitingState(20);
        }
    }

    private int getSaturationAmount(String foodType) {
        if (foodType == null){
            return 0;
        }
        switch (foodType) {
            case PetFood.SIMPLE_MEAL:
                return 50;
            case PetFood.TASTY_MEAL:
                return 75;
            case PetFood.PERFECT_MEAL:
                return 100;
            case RAW_MEAL:
                return 25;
            default:
                return 0;
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
                foodTypeEating = RAW_MEAL;
                return true;
            }
        }
        for (FoodBowl bowl : game.getGameMap(animal.getCurrentMap()).getFoodBowls()) {
            if (bowl.isFull() && animal.getRectangle().intersects(bowl.getRectangle())) {
                foodTypeEating = bowl.getFoodType();
                bowl.emptyBowl();
                return true;
            }
        }
        return false;
    }

    private void eatFood(Animal animal, int amount) {
        logger.info(String.format("%s ate food", animal));
        int hungerInPercent = Math.min(animal.getCurrentHungerInPercent() + amount, 100);
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
}
