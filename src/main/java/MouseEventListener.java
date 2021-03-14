import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseEventListener implements MouseListener, MouseMotionListener {

    private final Game game;

    public MouseEventListener(Game game) {
        this.game = game;
    }

    @Override       //means clicked and released
    public void mouseClicked(MouseEvent e) {

    }

    @Override       //means just pressed
    public void mousePressed(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
//            game.leftClick(event.getX(), event.getY());
        }
        if (event.getButton() == MouseEvent.BUTTON3) {
//            game.rightClick(event.getX(), event.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
