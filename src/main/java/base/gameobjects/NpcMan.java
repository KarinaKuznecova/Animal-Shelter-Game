package base.gameobjects;

import base.graphicsservice.ImageLoader;

import static base.constants.FilePath.NPC_SHEET_PATH_MAN;

public class NpcMan extends Npc {

    public NpcMan(int startX, int startY, Animal wantedAnimal) {
        super(startX, startY, wantedAnimal);
    }

    @Override
    protected AnimatedSprite getAnimatedSprite() {
        return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_MAN, 64);
    }
}
