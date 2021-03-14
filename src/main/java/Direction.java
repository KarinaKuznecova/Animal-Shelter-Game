public enum Direction {
    RIGHT(3),
    LEFT(1),
    UP(2),
    DOWN(0);

    public final int directionNumber;

    Direction(int directionNumber) {
        this.directionNumber = directionNumber;
    }
}
