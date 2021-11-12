package base.navigationservice;

import base.Game;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener, FocusListener {

    public boolean[] keys = new boolean[128];
    private boolean saving = false;
    private Game game;

    public KeyboardListener(Game game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        saving = false;
        int keyCode = event.getKeyCode();
        if (keyCode < keys.length) {
            keys[keyCode] = true;
        }
        if (keys[KeyEvent.VK_CONTROL] && keys[KeyEvent.VK_S]) {
            saving = true;
            game.handleCTRLandS();
        }
        if (keys[KeyEvent.VK_DELETE]) {
            game.deleteAnimal();
        }
        if (keys[KeyEvent.VK_B]) {
            game.showBackpack();
        }
        if (keys[KeyEvent.VK_H]) {
            game.replaceMapWithDefault();
        }
        if (keys[KeyEvent.VK_P]) {
            game.switchTopPanel(10);
        }
        if (keys[KeyEvent.VK_T]) {
            game.openTerrainMenu();
        }
        if (keys[KeyEvent.VK_Q]) {
            game.handleQ();
        }
        if (keys[KeyEvent.VK_1]) {
            game.switchTopPanel(1);
        }
        if (keys[KeyEvent.VK_2]) {
            game.switchTopPanel(2);
        }
        if (keys[KeyEvent.VK_3]) {
            game.switchTopPanel(3);
        }
        if (keys[KeyEvent.VK_4]) {
            game.switchTopPanel(4);
        }
        if (keys[KeyEvent.VK_5]) {
            game.switchTopPanel(5);
        }
        if (keys[KeyEvent.VK_6]) {
            game.switchTopPanel(6);
        }
        if (keys[KeyEvent.VK_7]) {
            game.switchTopPanel(7);
        }
        if (keys[KeyEvent.VK_8]) {
            game.switchTopPanel(8);
        }
        if (keys[KeyEvent.VK_9]) {
            game.switchTopPanel(9);
        }
        if (keys[KeyEvent.VK_0]) {
            game.switchTopPanel(0);
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
        //not going to use it now
    }

    @Override
    public void focusGained(FocusEvent event) {
        //not going to use it now
    }

    public boolean up() {
        return keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
    }

    public boolean down() {

        if (!saving) {
            return keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
        } else {
            return false; //Don't move down when saving
        }
    }

    public boolean right() {
        return keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
    }

    public boolean left() {
        return keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
    }
}
