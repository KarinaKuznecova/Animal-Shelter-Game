package base.navigationservice;

import org.junit.jupiter.api.Test;

import static base.constants.MapConstants.*;
import static base.navigationservice.Direction.*;
import static base.navigationservice.Direction.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NavigationServiceClassTest {

    @Test
    public void getPixelsToAdjustPosition() {
        assertEquals(56, NavigationService.getPixelsToAdjustPosition(DOWN, 200, 200));
        assertEquals(8, NavigationService.getPixelsToAdjustPosition(UP, 200, 200));
        assertEquals(8, NavigationService.getPixelsToAdjustPosition(LEFT, 200, 200));
        assertEquals(56, NavigationService.getPixelsToAdjustPosition(RIGHT, 200, 200));

        assertEquals(64, NavigationService.getPixelsToAdjustPosition(DOWN, 128, 128));
        assertEquals(64, NavigationService.getPixelsToAdjustPosition(UP, 128, 128));
        assertEquals(64, NavigationService.getPixelsToAdjustPosition(LEFT, 128, 128));
        assertEquals(64, NavigationService.getPixelsToAdjustPosition(RIGHT, 128, 128));
    }

}
