package base.gameobjects.animals;

import base.gameobjects.Animal;
import base.graphicsservice.ImageLoader;

import java.util.Arrays;
import java.util.List;

public class Cat extends Animal {

    public static final String NAME = "cat";
    public static final String ANIMATION_SHEET_PATH = "img/cat.png";
    public static final String PREVIEW = null;

    public static final List<String> colors = Arrays.asList("cat-white", "cat-brown", "cat-caramel", "cat-black");

    public Cat(int startX, int startY, int speed, String color) {
        super(ImageLoader.getAnimatedSprite(getAdjustedAnimationPath(color != null ? color : "black"), 32), null, startX, startY, speed, 32);
        setColor(color);
    }

    private static String getAdjustedAnimationPath(String color) {
        String temp = ANIMATION_SHEET_PATH.substring(0, ANIMATION_SHEET_PATH.length() - 4);
        return temp + "-" + color + ".png";
    }

}
