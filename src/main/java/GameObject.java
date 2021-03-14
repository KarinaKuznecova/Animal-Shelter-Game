public interface GameObject {

    //should be called as often as possible
    void render(RenderHandler renderer, int xZoom, int yZoom);

    //called 60fps
    void update(Game game);

    int getLayer();
}
