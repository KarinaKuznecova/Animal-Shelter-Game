package base.gui;

import base.Game;

import java.io.Serializable;
import java.util.List;

import static base.constants.MultiOptionalObjects.MASTER_TILE_LIST;

public class GuiService implements Serializable {

    public static final int BACKPACK_ROWS = 5;
    public static final int BACKPACK_COLUMNS = 5;

    public void decreaseNumberOnButton(Game game, BackpackButton button) {
        if (button == null) {
            return;
        }
        button.setObjectCount(button.getObjectCount() - 1);
        if (button.getObjectCount() <= 0) {
            button.makeEmpty();
            game.deselectItem();
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
