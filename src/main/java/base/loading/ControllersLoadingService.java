package base.loading;

import base.Game;
import base.graphicsservice.RenderHandler;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ControllersLoadingService {

    public void loadControllers(Game game, Canvas canvas, RenderHandler renderer) {

        addListeners(game, canvas);

        canvas.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = canvas.getWidth();
                int newHeight = canvas.getHeight();

                if (newWidth > renderer.getMaxWidth())
                    newWidth = renderer.getMaxWidth();

                if (newHeight > renderer.getMaxHeight())
                    newHeight = renderer.getMaxHeight();

                renderer.getCamera().setWidth(newWidth);
                renderer.getCamera().setHeight(newHeight);
                canvas.setSize(newWidth, newHeight);
                game.pack();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //not going to use it now
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //not going to use it now
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //not going to use it now
            }
        });
    }

    private void addListeners(Game game, Canvas canvas) {
        game.addKeyListener(game.getKeyboardListener());
        game.addFocusListener(game.getKeyboardListener());
        game.addMouseListener(game.getMouseEventListener());
        game.addMouseMotionListener(game.getMouseEventListener());

        canvas.addKeyListener(game.getKeyboardListener());
        canvas.addFocusListener(game.getKeyboardListener());
        canvas.addMouseListener(game.getMouseEventListener());
        canvas.addMouseMotionListener(game.getMouseEventListener());
    }
}
