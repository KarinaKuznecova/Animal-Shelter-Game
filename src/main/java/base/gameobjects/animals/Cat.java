package base.gameobjects.animals;

import base.gameobjects.Animal;

import java.util.Arrays;
import java.util.List;

public class Cat extends Animal {

    public static final String NAME = "cat";

    public static final List<String> colors = Arrays.asList("cat-white", "cat-brown", "cat-caramel", "cat-black");

    public Cat(int startX, int startY, int speed, String color) {
        super(color != null ? NAME + "-" + color : "cat-black", startX, startY, speed, 32);
        setColor(color);
    }
}
