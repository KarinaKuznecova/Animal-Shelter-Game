package base.navigationservice;

public enum Direction {
    DOWN(0),
    LEFT(1),
    UP(2),
    RIGHT(3),
    STAY(4),
    EAT_DOWN(5),
    EAT_LEFT(6),
    EAT_UP(7),
    EAT_RIGHT(8),
    SLEEP_LEFT(9),
    WAKEUP_LEFT(10),
    SLEEP_RIGHT(11),
    WAKEUP_RIGHT(12);

    public final int directionNumber;

    Direction(int directionNumber) {
        this.directionNumber = directionNumber;
    }
}
