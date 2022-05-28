package base.gui;

import base.Game;
import base.gameobjects.Animal;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static base.constants.VisibleText.favorite;
import static base.constants.VisibleText.newName;

public class ChangeAnimalNameWindow {

    int width;
    int height;

    JFrame frame;
    JPanel panel;
    JButton button;
    JTextField field;
    JCheckBox checkBox;

    String buttonText = "Ok";

    public ChangeAnimalNameWindow(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void editAnimalName(Game game, Animal animal) {
        frame = new JFrame(newName);
        panel = new JPanel();
        field = new JTextField(animal.getName(), 10);
        button = new JButton(buttonText);
        checkBox = new JCheckBox(favorite);

        if (animal.isFavorite()) {
            checkBox.setSelected(true);
        }

        panel.add(field);
        panel.add(button);
        panel.add(checkBox);

        frame.setLocation(width, height);
        frame.add(panel);
        frame.setSize(300, 90);
        frame.setVisible(true);
        frame.getRootPane().setDefaultButton(button);

        game.pause();

        button.addActionListener(e -> {
            String s = e.getActionCommand();
            if (s.equals(buttonText)) {
                animal.setName(field.getText());
                frame.setVisible(false);
                game.unpause();
            }
        });

        checkBox.addActionListener(e -> animal.setFavorite(checkBox.isSelected()));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                game.unpause();
            }
        });
    }
}
