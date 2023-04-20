package base.gameobjects.npc;

import base.Game;
import base.gameobjects.Animal;
import base.gameobjects.AnimatedSprite;
import base.gameobjects.interactionzones.InteractionZoneAdoptionNpc;
import base.graphicsservice.ImageLoader;
import base.navigationservice.Direction;
import base.navigationservice.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static base.constants.FilePath.*;
import static base.constants.MapConstants.MAIN_MAP;
import static base.gameobjects.npc.NpcType.ADOPTION;
import static base.navigationservice.Direction.*;

public class NpcAdoption extends Npc {

    private static final transient Logger logger = LoggerFactory.getLogger(NpcAdoption.class);

    private Animal wantedAnimal;
    private boolean isGoingAway;
    private boolean arrived;

    public NpcAdoption(int startX, int startY, Animal wantedAnimal) {
        super(startX, startY);
        type = ADOPTION;
        setWantedAnimal(wantedAnimal);
        interactionZone = new InteractionZoneAdoptionNpc(rectangle.getX() + 32, rectangle.getY() + 32, 100, wantedAnimal);
    }

    @Override
    public void update(Game game) {
        boolean isMoving = false;
        Direction nextDirection = direction;

        if (route.isEmpty() && !arrived) {
            if (rectangle.intersects(game.getNpcSpot(ADOPTION).getRectangle())) {
                arrived = true;
            } else {
                route = game.calculateRouteToNpcSpot(this);
                route.addStep(UP);
                route.addStep(LEFT);
            }
        }

        if (movingTicks < 1 && !route.isEmpty()) {
            nextDirection = route.getNextStep();
            movingTicks = 32 / speed;
            logger.debug(String.format("Direction: %s, moving ticks: %d", direction.name(), movingTicks));
        }

        if (route.isEmpty() && !isGoingAway) {
            nextDirection = STAY;
        }

        handleMoving(game.getGameMap(MAIN_MAP), nextDirection);
        if (nextDirection != STAY) {
            isMoving = true;
        }

        if (nextDirection != direction) {
            direction = nextDirection;
            updateDirection();
        }
        if (animatedSprite != null) {
            if (isMoving) {
                animatedSprite.update(game);
                interactionZone.changePosition(rectangle.getX() + 32, rectangle.getY() + 32);
            } else {
                animatedSprite.reset();
            }
        }

        interactionZone.update(game);
        movingTicks--;

        if (isGoingAway && route.isEmpty()) {
            game.removeNpc(this);
        }
    }

    @Override
    protected AnimatedSprite getAnimatedSprite() {
        int randomNumber = new Random().nextInt(4);
        switch (randomNumber) {
            case 0:
                return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY, 64, 3);
            case 1:
                return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY1, 64, 3);
            case 2:
                return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY2, 64, 3);
            case 3:
                return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY3, 64, 3);
        }
        return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY, 64, 3);
    }

    public void goAway(Route route) {
        isGoingAway = true;
        this.route = route;
        logger.info("SENDING NPC AWAY");
    }

    public Animal getWantedAnimal() {
        return wantedAnimal;
    }

    public void setWantedAnimal(Animal wantedAnimal) {
        this.wantedAnimal = wantedAnimal;
    }

    public void checkWantedAnimal(Game game) {
        if (!game.getAnimalService().isAnimalAvailableForAdoption(wantedAnimal)) {
            wantedAnimal = game.getAnimalService().pickAvailableAnimal(game);
            InteractionZoneAdoptionNpc zone = (InteractionZoneAdoptionNpc) interactionZone;
            zone.setWantedAnimal(wantedAnimal);
        }
    }
}
