package base.gameobjects.animalstates;

import base.gameobjects.animals.Mouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static base.gameobjects.AgeStage.ADULT;
import static base.navigationservice.Direction.*;
import static base.navigationservice.Direction.RIGHT;
import static org.junit.jupiter.api.Assertions.*;

class WalkingStateTest {

    WalkingState testSubject;
    Mouse mouse;

    @BeforeEach
    public void before() {
        testSubject = new WalkingState();
        mouse = new Mouse(0, 0, 3, 100, 100, 100, ADULT, "");
    }

    @Test
    public void getMovingTickToAdjustPosition() {
        mouse.teleportAnimalTo(66,66);
        assertEquals(20, testSubject.getMovingTickToAdjustPosition(DOWN, mouse));
        assertEquals(0, testSubject.getMovingTickToAdjustPosition(UP, mouse));
        assertEquals(0, testSubject.getMovingTickToAdjustPosition(LEFT, mouse));
        assertEquals(20, testSubject.getMovingTickToAdjustPosition(RIGHT, mouse));

        mouse.setSpeed(2);
        assertEquals(31, testSubject.getMovingTickToAdjustPosition(DOWN, mouse));
        assertEquals(1, testSubject.getMovingTickToAdjustPosition(UP, mouse));
        assertEquals(1, testSubject.getMovingTickToAdjustPosition(LEFT, mouse));
        assertEquals(31, testSubject.getMovingTickToAdjustPosition(RIGHT, mouse));

        mouse.setSpeed(1);
        assertEquals(62, testSubject.getMovingTickToAdjustPosition(DOWN, mouse));
        assertEquals(2, testSubject.getMovingTickToAdjustPosition(UP, mouse));
        assertEquals(2, testSubject.getMovingTickToAdjustPosition(LEFT, mouse));
        assertEquals(62, testSubject.getMovingTickToAdjustPosition(RIGHT, mouse));

        mouse.teleportAnimalTo(128,128);
        assertEquals(64, testSubject.getMovingTickToAdjustPosition(DOWN, mouse));
        assertEquals(64, testSubject.getMovingTickToAdjustPosition(UP, mouse));
        assertEquals(64, testSubject.getMovingTickToAdjustPosition(LEFT, mouse));
        assertEquals(64, testSubject.getMovingTickToAdjustPosition(RIGHT, mouse));
    }

}