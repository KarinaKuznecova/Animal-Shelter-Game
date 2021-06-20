package base.map;

public class MapTile {

    private final int layer;
    private final int id;
    private final int x;
    private final int y;
    private boolean isPortal;
    private String portalDirection;

    public MapTile(int layer, int id, int x, int y) {
        this.layer = layer;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getLayer() {
        return layer;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isPortal() {
        return isPortal;
    }

    public void setPortal(boolean portal) {
        isPortal = portal;
    }

    public String getPortalDirection() {
        return portalDirection;
    }

    public void setPortalDirection(String portalDirection) {
        this.portalDirection = portalDirection;
    }
}
