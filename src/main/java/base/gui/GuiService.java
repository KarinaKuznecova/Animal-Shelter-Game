package base.gui;

import base.Game;
import base.gameobjects.Animal;
import base.graphicsservice.Rectangle;
import base.graphicsservice.Sprite;
import base.map.Tile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static base.constants.Constants.TILE_SIZE;
import static base.constants.Constants.ZOOM;
import static base.constants.MultiOptionalObjects.MASTER_TILE_LIST;

public class GuiService implements Serializable {

    public GUI loadYourAnimals(Game game) {
        List<Animal> animalsOnAllMaps = new ArrayList<>();
        for (List<Animal> animalsOnMaps : game.getAnimalsOnMaps().values()) {
            animalsOnAllMaps.addAll(animalsOnMaps);
        }
        List<GUIButton> buttons = new CopyOnWriteArrayList<>();

        for (int i = 0; i < animalsOnAllMaps.size(); i++) {
            Animal animal = animalsOnAllMaps.get(i);
            Sprite animalSprite = animal.getPreviewSprite();
            Rectangle tileRectangle = new Rectangle(game.getWidth() - (TILE_SIZE * ZOOM + TILE_SIZE), i * (TILE_SIZE * ZOOM + 2), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
            buttons.add(new AnimalIcon(game, animal, animalSprite, tileRectangle));
        }
        return new GUI(buttons, 5, 5, true);
    }

    public GUI loadPossibleAnimalsPanel(Game game, Map<String, Sprite> previews) {
        List<GUIButton> buttons = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Sprite> entry : previews.entrySet()) {
            Sprite animalSprite = entry.getValue();
            Rectangle tileRectangle = new Rectangle(i * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //horizontal on top left
            buttons.add(new NewAnimalButton(game, entry.getKey(), animalSprite, tileRectangle));
            i++;
        }
        Rectangle tileRectangle = new Rectangle((previews.size()) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);  //one more horizontal on top left
        buttons.add(new NewAnimalButton(game, "", null, tileRectangle));
        game.changeAnimal("");

        return new GUI(buttons, 5, 5, true);
    }

    public GUI loadPlantsPanel(Game game, Map<String, Sprite> previews) {
        List<GUIButton> buttons = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Sprite> entry : previews.entrySet()) {
            Sprite previewSprite = entry.getValue();
            Rectangle tileRectangle = new Rectangle(i * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
            buttons.add(new PlantButton(game, entry.getKey(), previewSprite, tileRectangle));
            i++;
        }
        Rectangle oneMoreTileRectangle = new Rectangle((previews.size()) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
        buttons.add(new PlantButton(game, "", null, oneMoreTileRectangle));
        game.changeSelectedPlant("");

        return new GUI(buttons, 5, 5, true);
    }

    public GUI[] loadTerrainGui(Game game, List<Tile> tiles, int buttonCount) {
        GUI[] terrainButtonsArray = new GUI[11];

        List<GUIButton> buttons = new ArrayList<>();
        for (int i = 0, j = 0, k = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            if (!tile.isVisible()) {
                continue;
            }
            Rectangle tileRectangle = new Rectangle(j * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
            buttons.add(new SDKButton(game, i, tiles.get(i).getSprite(), tileRectangle));
            if (k != 0 && k % buttonCount == 0) {
                Rectangle oneMoreTileRectangle = new Rectangle((j + 1) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                buttons.add(new SDKButton(game, -1, null, oneMoreTileRectangle));
                terrainButtonsArray[k / buttonCount - 1] = new GUI(buttons, 5, 5, true);

                buttons = new ArrayList<>();
                j = -1;
            }
            if (i == tiles.size() - 1) {
                Rectangle oneMoreTileRectangle = new Rectangle((j + 1) * (TILE_SIZE * ZOOM + 2), 0, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                buttons.add(new SDKButton(game, -1, null, oneMoreTileRectangle));
                int temp = (k - (k % buttonCount)) / buttonCount;
                terrainButtonsArray[temp] = new GUI(buttons, 5, 5, true);
            }
            j++;
            k++;
        }
        return terrainButtonsArray;
    }

    public GUI loadEmptyBackpack(Game game) {
        List<GUIButton> buttons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Rectangle buttonRectangle = new Rectangle(j * (TILE_SIZE * ZOOM + 2), i * (TILE_SIZE * ZOOM), TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                buttons.add(new BackpackButton(String.valueOf(i) + j, null, buttonRectangle, String.valueOf(i) + j));
            }
        }
        return new GUI(buttons, 5, game.getHeight() - (4 * (TILE_SIZE * ZOOM + 2)), true);
    }

    public void decreaseNumberOnButton(Game game, BackpackButton button) {
        button.setObjectCount(button.getObjectCount() - 1);
        if (button.getObjectCount() == 0) {
            button.makeEmpty();
            game.changeSelectedItem(button.getDefaultId());
        }
    }

    public int getNextId(int id) {

        for (List<Integer> list : MASTER_TILE_LIST) {
            if (list.contains(id)) {
                if (list.indexOf(id) == list.size() - 1) {
                    return list.get(0);
                }
                return list.get(list.indexOf(id) + 1);
            }
        }
        return id;
    }

}
