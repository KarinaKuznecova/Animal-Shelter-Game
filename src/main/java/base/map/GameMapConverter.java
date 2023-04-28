package base.map;

import static base.constants.Constants.CURRENT_GAME_VERSION;

public class GameMapConverter {

    public GameMap getGameMap(GameMapDTO gameMapDTO) {
        GameMap gameMap = new GameMap(gameMapDTO.getMapName());

        gameMap.setBackGroundTileId(gameMapDTO.getBackGroundTileId());
        gameMap.setMapWidth(gameMapDTO.getMapWidth());
        gameMap.setMapHeight(gameMapDTO.getMapHeight());
        gameMap.setMaxLayer(gameMapDTO.getMaxLayer());

        gameMap.setLayeredTiles(gameMapDTO.getLayeredTiles());
        gameMap.addGameObjects(gameMapDTO.getPlants());
        gameMap.addGameObjects(gameMapDTO.getItems());
        gameMap.addGameObjects(gameMapDTO.getFoodBowls());
        gameMap.addGameObjects(gameMapDTO.getWaterBowls());
        gameMap.addGameObjects(gameMapDTO.getStorageChests());
        gameMap.addGameObjects(gameMapDTO.getFeathers());
        gameMap.addGameObjects(gameMapDTO.getMushrooms());
        gameMap.addGameObjects(gameMapDTO.getWoods());
        gameMap.addGameObjects(gameMapDTO.getBushes());
        gameMap.addGameObjects(gameMapDTO.getOaks());
        gameMap.addGameObjects(gameMapDTO.getSpruces());
        gameMap.addGameObjects(gameMapDTO.getCookingStoves());
        gameMap.addGameObjects(gameMapDTO.getFridges());
        gameMap.addGameObjects(gameMapDTO.getNpcSpots());
        gameMap.addGameObjects(gameMapDTO.getNpcSpawnSpots());
        gameMap.addGameObjects(gameMapDTO.getPortals());

        return gameMap;
    }

    public GameMapDTO getGameMapDTO(GameMap gameMap) {
        GameMapDTO gameMapDTO = new GameMapDTO();

        gameMapDTO.setGameVersion(CURRENT_GAME_VERSION);
        gameMapDTO.setMapName(gameMap.getMapName());

        gameMapDTO.setBackGroundTileId(gameMap.getBackGroundTileId());
        gameMapDTO.setMapWidth(gameMap.getMapWidth());
        gameMapDTO.setMapHeight(gameMap.getMapHeight());
        gameMapDTO.setMaxLayer(gameMap.getMaxLayer());

        gameMapDTO.setLayeredTiles(gameMap.getLayeredTiles());
        gameMapDTO.setPlants(gameMap.getPlants());
        gameMapDTO.setItems(gameMap.getItems());
        gameMapDTO.setFoodBowls(gameMap.getFoodBowls());
        gameMapDTO.setWaterBowls(gameMap.getWaterBowls());
        gameMapDTO.setStorageChests(gameMap.getStorageChests());
        gameMapDTO.setFeathers(gameMap.getFeathers());
        gameMapDTO.setMushrooms(gameMap.getMushrooms());
        gameMapDTO.setWoods(gameMap.getWoods());
        gameMapDTO.setBushes(gameMap.getBushes());
        gameMapDTO.setOaks(gameMap.getOaks());
        gameMapDTO.setSpruces(gameMap.getSpruces());
        gameMapDTO.setCookingStoves(gameMap.getCookingStoves());
        gameMapDTO.setFridges(gameMap.getFridges());
        gameMapDTO.setNpcSpots(gameMap.getNpcSpots());
        gameMapDTO.setNpcSpawnSpots(gameMap.getNpcSpawnSpots());
        gameMapDTO.setPortals(gameMap.getPortals());

        return gameMapDTO;
    }
}
