package base.gui;

import base.gameobjects.player.Skill;
import base.gameobjects.player.Skills;
import base.graphicsservice.RenderHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static base.constants.Constants.DEBUG_MODE;

public class SkillsInfo {

    List<String> lines = new ArrayList<>();

    public void updateSkillsLevel(Skills skills) {
        lines.clear();
        for (Skill skill : skills.getAllSkills()) {
            String line = skill.getName() + " - " + skill.getCurrentLevel() + "/" + skill.getMaxLevel() + ", progress: " + getSkillProgressInPercents(skill) + "%";
            if (DEBUG_MODE) {
                line = line.concat(" - exp to level: " + skill.getExperienceToLevel());
            }
            lines.add(line);
        }
    }

    private int getSkillProgressInPercents(Skill skill) {
        int experienceToLevel = skill.getExperienceToLevel();
        int onePercent = Skill.EXPERIENCE_TO_LEVEL / 100;
        int result = 100 - (experienceToLevel / onePercent) - 1;
        return Math.max(result, 0);
    }

    public void render(RenderHandler renderer) {
        renderer.setTextToDrawInCenter(getSkillsLines());
    }

    private List<String> getSkillsLines() {
        if (!lines.isEmpty()) {
            return lines;
        }
        return Arrays.asList("Cooking skill - 0/7", "Gardening skill - 0/7");
    }
}
