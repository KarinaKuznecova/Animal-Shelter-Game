package base.gui;

import base.graphicsservice.Position;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;

import java.util.List;

public class Backpack extends GUI {

    MoneyIcon moneyIcon;
    private int coins;

    public Backpack(List<GUIButton> buttons, int xPosition, int yPosition, int coins) {
        super(buttons, xPosition, yPosition, true);
        this.coins = coins;
        moneyIcon = new MoneyIcon();
        Rectangle moneyIconRectangle = new Rectangle();
        moneyIconRectangle.setX(xPosition + 4);
        moneyIconRectangle.setY(yPosition - 32);
        moneyIcon.setRectangle(moneyIconRectangle);
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        super.render(renderer, zoom);
        moneyIcon.render(renderer);
        renderer.renderText(String.valueOf(coins), new Position(moneyIcon.getRectangle().getX() + 42, moneyIcon.getRectangle().getY() + 18));
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void removeCoins(int amount) {
        coins -= amount;
    }
}
