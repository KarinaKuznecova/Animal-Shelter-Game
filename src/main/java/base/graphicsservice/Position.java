package base.graphicsservice;

import java.util.Objects;

public class Position {

    private final int xPosition;
    private final int yPosition;

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public Position(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Math.abs(xPosition - position.xPosition) < 2
                && Math.abs(yPosition - position.yPosition) < 2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xPosition, yPosition);
    }
}
