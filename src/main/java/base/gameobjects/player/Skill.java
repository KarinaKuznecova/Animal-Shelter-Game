package base.gameobjects.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public abstract class Skill {

    protected static final Logger logger = LoggerFactory.getLogger(Skill.class);

    public static final int EXPERIENCE_TO_LEVEL = 2000;

    private String name;

    private int maxLevel;
    private int currentLevel;
    private int experienceToLevel;

    protected final transient Random random = new Random();

    public void getExperienceSmall() {
        int amount = random.nextInt(5) + 5;
        getExperience(amount);
    }

    public void getExperienceMedium() {
        int amount = random.nextInt(10) + 20;
        getExperience(amount);
    }

    public void getExperienceLarge() {
        int amount = random.nextInt(15) + 35;
        getExperience(amount);
    }

    private void getExperience(int amount) {
        logger.info(String.format("Adding %s exp to %s skill", amount, name));
        if (currentLevel >= maxLevel) {
            return;
        }
        experienceToLevel -= amount;
        if (experienceToLevel <= 0) {
            levelUp();
            resetExperienceToLevel();
        }
    }

    public void levelUp() {
        currentLevel++;
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
