package base.gameobjects.player;

import base.graphicsservice.RenderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static base.constants.VisibleText.levelUpLine;

public abstract class Skill {

    protected static final Logger logger = LoggerFactory.getLogger(Skill.class);

    public static final int EXPERIENCE_TO_LEVEL = 2000;

    private String name;

    private int maxLevel;
    private int currentLevel;
    private int experienceToLevel;

    protected final transient Random random = new Random();

    public void getExperienceSmall(RenderHandler renderHandler) {
        int amount = random.nextInt(5) + 5;
        getExperience(amount, renderHandler);
        renderHandler.setTextToDraw("+ " + amount + " exp", 70);
    }

    public void getExperienceMedium(RenderHandler renderHandler) {
        int amount = random.nextInt(10) + 20;
        getExperience(amount, renderHandler);
        renderHandler.setTextToDraw("+ " + amount + " exp", 70);
    }

    public void getExperienceLarge(RenderHandler renderHandler) {
        int amount = random.nextInt(15) + 35;
        getExperience(amount, renderHandler);
        renderHandler.setTextToDraw("+ " + amount + " exp", 70);
    }

    private void getExperience(int amount, RenderHandler renderHandler) {
        logger.info(String.format("Adding %s exp to %s skill", amount, name));
        if (currentLevel >= maxLevel) {
            return;
        }
        experienceToLevel -= amount;
        if (experienceToLevel <= 0) {
            levelUp(renderHandler);
            resetExperienceToLevel();
        }
    }

    public void levelUp(RenderHandler renderHandler) {
        currentLevel++;
        String lineToDraw = String.format(levelUpLine, name, getCurrentLevel());
        renderHandler.setTextToDraw(lineToDraw, 170);
    }

    public void resetExperienceToLevel() {
        experienceToLevel = EXPERIENCE_TO_LEVEL;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getExperienceToLevel() {
        return experienceToLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
