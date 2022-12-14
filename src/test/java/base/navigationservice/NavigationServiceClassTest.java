package base.navigationservice;

import org.junit.jupiter.api.Test;

import static base.constants.MapConstants.*;
import static base.navigationservice.Direction.*;
import static base.navigationservice.Direction.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NavigationServiceClassTest {

    @Test
    public void getPixelsToAdjustPosition() {
        assertEquals(24, NavigationService.getPixelsToAdjustPosition(DOWN, 200, 200));
        assertEquals(8, NavigationService.getPixelsToAdjustPosition(UP, 200, 200));
        assertEquals(8, NavigationService.getPixelsToAdjustPosition(LEFT, 200, 200));
        assertEquals(24, NavigationService.getPixelsToAdjustPosition(RIGHT, 200, 200));

        assertEquals(32, NavigationService.getPixelsToAdjustPosition(DOWN, 128, 128));
        assertEquals(32, NavigationService.getPixelsToAdjustPosition(UP, 128, 128));
        assertEquals(32, NavigationService.getPixelsToAdjustPosition(LEFT, 128, 128));
        assertEquals(32, NavigationService.getPixelsToAdjustPosition(RIGHT, 128, 128));

        assertEquals(29, NavigationService.getPixelsToAdjustPosition(DOWN, 55, 99));
        assertEquals(3, NavigationService.getPixelsToAdjustPosition(UP, 55, 99));
        assertEquals(23, NavigationService.getPixelsToAdjustPosition(LEFT, 55, 99));
        assertEquals(9, NavigationService.getPixelsToAdjustPosition(RIGHT, 55, 99));
    }

}
