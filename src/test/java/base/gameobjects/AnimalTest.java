package base.gameobjects;

import base.gameobjects.animals.Mouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static base.gameobjects.AgeStage.ADULT;
import static base.navigationservice.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnimalTest {

    Mouse testSubject;

    @BeforeEach
    public void before() {
        testSubject = new Mouse(0, 0, 3, 100, 100, 100, ADULT, "");
    }

    @Test
    public void getStartingSprite_DOWN() {
        assertEquals(0, testSubject.getStartingSprite(DOWN));
    }

    @Test
    public void getEndSprite_DOWN() {
        assertEquals(2, testSubject.getEndSprite(DOWN));
    }

    @Test
    public void getStartingSprite_LEFT() {
        assertEquals(11, testSubject.getStartingSprite(LEFT));
    }

    @Test
    public void getEndSprite_LEFT() {
        assertEquals(13, testSubject.getEndSprite(LEFT));
    }

    @Test
    public void getStartingSprite_UP() {
        assertEquals(22, testSubject.getStartingSprite(UP));
    }

    @Test
    public void getEndSprite_UP() {
        assertEquals(24, testSubject.getEndSprite(UP));
    }

    @Test
    public void getStartingSprite_RIGHT() {
        assertEquals(33, testSubject.getStartingSprite(RIGHT));
    }

    @Test
    public void getEndSprite_RIGHT() {
        assertEquals(35, testSubject.getEndSprite(RIGHT));
    }

    @Test
    public void getStartingSprite_EAT_DOWN() {
        assertEquals(3, testSubject.getStartingSprite(EAT_DOWN));
    }

    @Test
    public void getEndSprite_EAT_DOWN() {
        assertEquals(6, testSubject.getEndSprite(EAT_DOWN));
    }

    @Test
    public void getStartingSprite_EAT_LEFT() {
        assertEquals(14, testSubject.getStartingSprite(EAT_LEFT));
    }

    @Test
    public void getEndSprite_EAT_LEFT() {
        assertEquals(17, testSubject.getEndSprite(EAT_LEFT));
    }

    @Test
    public void getStartingSprite_EAT_UP() {
        assertEquals(25, testSubject.getStartingSprite(EAT_UP));
    }

    @Test
    public void getEndSprite_EAT_UP() {
        assertEquals(28, testSubject.getEndSprite(EAT_UP));
    }

    @Test
    public void getStartingSprite_EAT_RIGHT() {
        assertEquals(36, testSubject.getStartingSprite(EAT_RIGHT));
    }

    @Test
    public void getEndSprite_EAT_RIGHT() {
        assertEquals(39, testSubject.getEndSprite(EAT_RIGHT));
    }

    @Test
    public void getStartingSprite_SLEEP_LEFT() {
        assertEquals(7, testSubject.getStartingSprite(SLEEP_LEFT));
    }

    @Test
    public void getEndSprite_SLEEP_LEFT() {
        assertEquals(10, testSubject.getEndSprite(SLEEP_LEFT));
    }

    @Test
    public void getStartingSprite_WAKEUP_LEFT() {
        assertEquals(18, testSubject.getStartingSprite(WAKEUP_LEFT));
    }

    @Test
    public void getEndSprite_WAKEUP_LEFT() {
        assertEquals(21, testSubject.getEndSprite(WAKEUP_LEFT));
    }

    @Test
    public void getStartingSprite_SLEEP_RIGHT() {
        assertEquals(29, testSubject.getStartingSprite(SLEEP_RIGHT));
    }

    @Test
    public void getEndSprite_SLEEP_RIGHT() {
        assertEquals(32, testSubject.getEndSprite(SLEEP_RIGHT));
    }

    @Test
    public void getStartingSprite_WAKEUP_RIGHT() {
        assertEquals(40, testSubject.getStartingSprite(WAKEUP_RIGHT));
    }

    @Test
    public void getEndSprite_WAKEUP_RIGHT() {
        assertEquals(43, testSubject.getEndSprite(WAKEUP_RIGHT));
    }
}