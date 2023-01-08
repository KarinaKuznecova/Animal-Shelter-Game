package base.map.bigobjects;

import base.map.MapTile;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BigObject {

    private final int x;
    private final int y;

    protected List<MapTile> objectParts = new CopyOnWriteArrayList<>();

    public BigObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void addPart(int layer, int id, int x, int y, boolean regular) {
        MapTile tile = new MapTile(layer, id, x, y, regular);
        objectParts.add(tile);
    }

    public List<MapTile> getObjectParts() {
        return objectParts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigObject bigObject = (BigObject) o;
        return x == bigObject.x && y == bigObject.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
