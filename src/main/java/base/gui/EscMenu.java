package base.gui;

import base.Game;
import base.graphicsservice.Rectangle;
import base.graphicsservice.RenderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

import static base.constants.ColorConstant.*;

public class EscMenu extends GUI {

    protected static final Logger logger = LoggerFactory.getLogger(EscMenu.class);

    private GameTips gameTips;

    private JFrame popupWindow;

    private Rectangle backGroundRectangle;
    private int color;

    public EscMenu(List<GUIButton> buttons, int x, int y, int width, int height, int color) {
        super(buttons, x, y, true);
        this.color = color;
        gameTips = new GameTips();

        backGroundRectangle = new Rectangle(x, y, width, height);
        backGroundRectangle.generateBorder(2, BROWN, color);
    }

    @Override
    public void update(Game game) {
        for (GUIButton button : buttons) {
            button.update(game);
        }
        if (CREAMY_PEACH == color) {
            if (popupWindow == null || !popupWindow.isActive()) {
                areYouSure(game);
            }
        }
    }

    @Override
    public void render(RenderHandler renderer, int zoom) {
        super.render(renderer, zoom);
        renderer.renderRectangle(backGroundRectangle, zoom, true);

        for (GUIButton button : buttons) {
            button.render(renderer, zoom, rectangle);
        }

        if (SOFT_PINK == color) {
            renderer.setTextToDrawInCenter(gameTips.getLines());
        } else {
            renderer.removeText();
        }
    }

    private void areYouSure(Game game) {
        popupWindow = new JFrame("Are you sure?");
        popupWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        popupWindow.setSize(250, 100);
        popupWindow.setLocationRelativeTo(null);

        // Create two buttons and add them to the pop-up window
        JLabel jLabel = new JLabel("Are you sure you want to exit?");
        jLabel.setHorizontalAlignment(JLabel.CENTER);
        popupWindow.add(jLabel, "North");
        JButton button1 = new JButton("Yes");
        JButton button2 = new JButton("No");
        JPanel panel = new JPanel();

        panel.add(button1);
        panel.add(button2);
        popupWindow.add(panel, "Center");

        // Add action listeners to the buttons
        button1.addActionListener(e -> {
            popupWindow.dispose();
            game.dispose();
        });

        button2.addActionListener(e -> {
            changeColor(SOFT_PINK);
            popupWindow.dispose();
        });

        popupWindow.setVisible(true);
    }

    public void changeColor(int newColor) {
        color = newColor;
        backGroundRectangle.generateBorder(2, BROWN, color);
    }
}
