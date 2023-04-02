package base.gameobjects.player;

import java.util.Arrays;
import java.util.List;

import static base.constants.VisibleText.gardening;

public class Skills {

    private final GardeningSkill gardeningSkill;

    public Skills() {
        gardeningSkill = new GardeningSkill();
    }

    public GardeningSkill getGardeningSkill() {
        return gardeningSkill;
    }

    public List<Skill> getAllSkills() {
        gardeningSkill.setName(gardening);
        return Arrays.asList(gardeningSkill);
    }
}
