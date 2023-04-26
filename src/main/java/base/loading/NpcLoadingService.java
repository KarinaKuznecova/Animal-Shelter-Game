package base.loading;

import base.Game;
import base.gameobjects.npc.NpcType;
import base.gameobjects.npc.NpcVendor;
import base.graphicsservice.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.TEST_MAP_MODE;
import static base.constants.MapConstants.CITY_MAP;

public class NpcLoadingService {

    private static final Logger logger = LoggerFactory.getLogger(NpcLoadingService.class);

    public void loadVendorNpc(Game game) {
        if (!TEST_MAP_MODE) {
            NpcVendor vendor = createVendorNpc(game);

            if (game.getGameMap(CITY_MAP).getNpcs() == null) {
                game.getGameMap(CITY_MAP).setNpcs(new CopyOnWriteArrayList<>());
            }
            game.getGameMap(CITY_MAP).addObject(vendor);
            game.refreshCurrentMapCache();
            game.getGameObjectsList().add(vendor);
            game.addToInteractionZones(vendor.getInteractionZone());
        }
    }

    private NpcVendor createVendorNpc(Game game) {
        logger.info("Spawning vendor npc");

        NpcVendor vendorNpc;
        Rectangle spot = game.getGameMap(CITY_MAP).getNpcSpot(NpcType.VENDOR).getRectangle();
        vendorNpc = new NpcVendor(spot.getX(), spot.getY());
        vendorNpc.setShopMenu(game.getShopService().createShopMenu(game, vendorNpc.getRectangle()));
        vendorNpc.setCurrentMap(CITY_MAP);

        return vendorNpc;
    }
}
