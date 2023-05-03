package base.graphicsservice;

import base.Game;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Environment {

    BufferedImage drkness;

    public Environment(Game game) {
        drkness = new BufferedImage(game.getWidth(), game.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = (Graphics2D) drkness.getGraphics();

        Area darkArea = new Area(new Rectangle2D.Double(0,0,game.getWidth(), game.getHeight()));

        int centerX = game.getPlayer().getRectangle().getX() + (game.getPlayer().getRectangle().getWidth() / 2);
        int centerY = game.getPlayer().getRectangle().getY() + (game.getPlayer().getRectangle().getHeight() / 2);

        int circleSize = 300;

        double circleX = centerX - (circleSize / 2);
        double circleY = centerY - (circleSize / 2);

        Shape circle = new Ellipse2D.Double(circleX, circleY, circleSize, circleSize);

        Area lightArea = new Area(circle);

        darkArea.subtract(lightArea);

        Color color[] = new Color[5];
        float fraction[] = new float[5];

        color[0] = new Color(0,0,0, 0.0f);
        color[1] = new Color(0,0,0, 0.25f);
        color[2] = new Color(0,0,0, 0.5f);
        color[3] = new Color(0,0,0, 0.75f);
        color[4] = new Color(0,0,0, 0.95f);

        fraction[0] = 0f;
        fraction[1] = 0.25f;
        fraction[2] = 0.5f;
        fraction[3] = 0.75f;
        fraction[4] = 1f;

        RadialGradientPaint radialGradientPaint = new RadialGradientPaint(centerX, centerY, circleSize / 2, fraction, color);
        graphics2d.setPaint(radialGradientPaint);
        graphics2d.fill(lightArea);

//        graphics2d.setColor(new Color(0,0,0,0.9f));

        graphics2d.fill(darkArea);
//        graphics2d.draw(darkArea);
        graphics2d.dispose();
    }

    public void draw(Graphics graphics) {
        graphics.drawImage(drkness, 0,0,null);
    }
}
