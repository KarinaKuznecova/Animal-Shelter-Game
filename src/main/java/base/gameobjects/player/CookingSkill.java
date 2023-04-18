package base.gameobjects.player;

import static base.constants.VisibleText.gardening;

public class CookingSkill extends Skill {

    public CookingSkill() {
        setMaxLevel(7);
        setCurrentLevel(1);
        resetExperienceToLevel();
        setName(gardening);
    }

    public int getSimpleMealAmount() {
        if (getCurrentLevel() == 1) {
            return 1;
        } else if (getCurrentLevel() == 2) {
            return random.nextInt(2) + 1;
        }
        return Math.max(random.nextInt(4) + 1, 2);
    }

    public int getTastyMealAmount() {
        if (getCurrentLevel() == 3) {
            return 1;
        } else if (getCurrentLevel() == 4) {
            return random.nextInt(2) + 1;
        }
        return Math.max(random.nextInt(4) + 1, 2);
    }

    public int getPerfectMealAmount() {
        if (getCurrentLevel() == 6) {
            return 1;
        }
        return random.nextInt(2) + 1;
    }
}
