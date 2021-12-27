package base.events;

import base.Game;

import java.util.Random;

public abstract class Event {

    Random random = new Random();
    int chance;
    boolean happened;
    boolean repeatable;

    abstract void calculateChance(Game game);

    abstract void startEvent(Game game);

    public int getChance() {
        return chance;
    }
}
