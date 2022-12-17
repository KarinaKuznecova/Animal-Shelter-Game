package base.gameobjects;

import base.graphicsservice.ImageLoader;

import java.util.Random;

import static base.constants.FilePath.*;

public class NpcLady extends Npc{

    public NpcLady(int startX, int startY, Animal wantedAnimal) {
        super(startX, startY, wantedAnimal);
    }

    @Override
    protected AnimatedSprite getAnimatedSprite() {
        int randomNumber = new Random().nextInt(4);
        switch (randomNumber) {
            case 0:return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY, 64);
            case 1:return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY1, 64);
            case 2:return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY2, 64);
            case 3:return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY3, 64);
        }
        return ImageLoader.getAnimatedSprite(NPC_SHEET_PATH_LADY, 64);
    }
}
