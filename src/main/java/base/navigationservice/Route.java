package base.navigationservice;

import java.util.LinkedList;
import java.util.List;

public class Route {

    private final List<Direction> route = new LinkedList<>();

    public void addStep(Direction direction) {
        route.add(direction);
    }

    public Direction getNextStep() {
        Direction nextStep = route.get(0);
        route.remove(0);
        return nextStep;
    }

    public boolean isEmpty() {
        return route.isEmpty();
    }

    public List<Direction> getAllSteps() {
        return route;
    }
}
