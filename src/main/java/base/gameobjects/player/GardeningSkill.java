package base.gameobjects.player;

import base.graphicsservice.RenderHandler;

import static base.constants.VisibleText.gardening;
import static base.constants.VisibleText.levelUpLine;

public class GardeningSkill extends Skill {

    public GardeningSkill() {
        setMaxLevel(7);
        setCurrentLevel(1);
        resetExperienceToLevel();
        setName(gardening);
    }

    public int getHarvestedAmount() {
        if (getCurrentLevel() <= 2) {
            return 1;
        }
        return 1 + random.nextInt(3);
    }

    public int getSeedsAmount() {
        if (getCurrentLevel() == 1) {
            return random.nextInt(2);
        }
        if (getCurrentLevel() <= 3) {
            return 1;
        }
        return 1 + random.nextInt(2);
    }

    public boolean keepPlant() {
        if (getCurrentLevel() <= 3) {
            return false;
        }
        if (getCurrentLevel() <= 5) {
            return random.nextInt(10) <= 3;
        }
        if (getCurrentLevel() == 6) {
            return random.nextBoolean();
        }
        return true;
    }

    @Override
    public void levelUp(RenderHandler renderHandler) {
        super.levelUp(renderHandler);
        String lineToDraw = String.format(levelUpLine, gardening, getCurrentLevel());
        renderHandler.setTextToDraw(lineToDraw, 170);
    }
}
