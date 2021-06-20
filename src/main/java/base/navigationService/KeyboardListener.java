package base.navigationService;

import base.Game;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener, FocusListener {

    public boolean[] keys = new boolean[120];

    private Game game;

    public KeyboardListener(Game game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode < keys.length) {
            keys[keyCode] = true;
        }
        if (keys[KeyEvent.VK_CONTROL]) {
            game.handleCTRL(keys);
        }
        if (keys[KeyEvent.VK_Q]) {
            game.handleQ(keys);
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode < keys.length) {
            keys[keyCode] = false;
        }
    }

    @Override
    public void focusLost(FocusEvent event) {
        for (boolean key : keys) {
            key = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
        //not gonna use it now
    }

    @Override
    public void focusGained(FocusEvent event) {
        //not gonna use it now
    }

    public boolean up() {
        return keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
    }

    public boolean down() {
        return keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
    }

    public boolean right() {
        return keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
    }

    public boolean left() {
        return keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
    }
}
