package base.gameobjects.player;

import java.util.Arrays;
import java.util.List;

import static base.constants.VisibleText.cooking;
import static base.constants.VisibleText.gardening;

public class Skills {

    private final GardeningSkill gardeningSkill;
    private final CookingSkill cookingSkill;

    public Skills() {
        gardeningSkill = new GardeningSkill();
        cookingSkill = new CookingSkill();
    }

    public GardeningSkill getGardeningSkill() {
        return gardeningSkill;
    }

    public CookingSkill getCookingSkill() {
        return cookingSkill;
    }

    public List<Skill> getAllSkills() {
        gardeningSkill.setName(gardening);
        cookingSkill.setName(cooking);
        return Arrays.asList(gardeningSkill, cookingSkill);
    }
}
