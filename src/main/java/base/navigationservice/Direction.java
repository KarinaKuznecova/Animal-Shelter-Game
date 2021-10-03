package base.navigationservice;

public enum Direction {
    DOWN(0),
    LEFT(1),
    UP(2),
    RIGHT(3),
    STAY(4);

    public final int directionNumber;

    Direction(int directionNumber) {
        this.directionNumber = directionNumber;
    }
}
