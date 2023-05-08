package base.gui;

import base.gameobjects.player.Skill;
import base.gameobjects.player.Skills;
import base.graphicsservice.Position;
import base.graphicsservice.RenderHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static base.constants.Constants.DEBUG_MODE;

public class SkillsInfo {

    List<String> lines = new ArrayList<>();

    private final int xPosition;
    private final int yPosition;

    public SkillsInfo(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

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
        int interval = 40;
        int spacingToSides = 80;
        int buttonHeight = 60;
        for (int i = 0; i < getSkillsLines().size(); i++) {
            int yPos = yPosition + spacingToSides + buttonHeight + (interval * i);
            int xPos = xPosition + spacingToSides;
            renderer.renderText(getSkillsLines().get(i), new Position(xPos, yPos));
        }
    }

    private List<String> getSkillsLines() {
        if (!lines.isEmpty()) {
            return lines;
        }
        return Arrays.asList("Cooking skill - 0/7", "Gardening skill - 0/7");
    }
}
