package base.map;

public class MapTile {

    private int layer;
    private int id;
    private final int x;
    private final int y;
    private boolean isPortal;
    private String portalDirection;
    private boolean regularTile;

    public MapTile(int layer, int id, int x, int y, boolean regularTile) {
        this.layer = layer;
        this.id = id;
        this.x = x;
        this.y = y;
        this.regularTile = regularTile;
    }

    public int getLayer() {
        return layer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isRegularTile() {
        return regularTile;
    }

    public void setRegularTile(boolean regularTile) {
        this.regularTile = regularTile;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
