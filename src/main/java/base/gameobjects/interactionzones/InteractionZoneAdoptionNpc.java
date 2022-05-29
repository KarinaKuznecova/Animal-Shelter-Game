package base.gameobjects.interactionzones;

import base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InteractionZoneAdoptionNpc extends InteractionZone {

    private static final Logger logger = LoggerFactory.getLogger(InteractionZoneAdoptionNpc.class);

    public InteractionZoneAdoptionNpc(int centerPointX, int centerPointY, int radius) {
        super(centerPointX, centerPointY, radius);
    }

    @Override
    public void action(Game game) {
        logger.info("adoption Npc interaction coming soon");
    }
}
