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

    @Test
    public void getNextPortalToGetToCenter_FromBottomLeftMap() {
        String result = NavigationService.getNextPortalToGetToCenter(BOTTOM_LEFT_MAP);
        assertEquals(BOTTOM_CENTER_MAP, result);
    }

    @Test
    public void getNextPortalToGetToHome_FromWaterMap() {
        String result = NavigationService.getNextPortalToGetToHome(WATER_MAP);
        assertEquals(MAIN_MAP, result);
    }

    @Test
    public void getNextPortalTo_FromBottomCenter() {
        String result = NavigationService.getNextPortalTo(BOTTOM_CENTER_MAP, BOTTOM_LEFT_MAP);
        assertEquals(BOTTOM_LEFT_MAP, result);
    }

    @Test
    public void getNextPortalTo_FromTopCenter() {
        String result = NavigationService.getNextPortalTo(TOP_CENTER_MAP, TOP_LEFT_MAP);
        assertEquals(TOP_LEFT_MAP, result);
    }

    @Test
    public void getNextPortalTo_FromMainMap() {
        String result = NavigationService.getNextPortalTo(MAIN_MAP, TOP_CENTER_MAP);
        assertEquals(TOP_CENTER_MAP, result);
    }

    @Test
    public void getNextPortalToOutside_FromTopCenterMap() {
        String result = NavigationService.getNextPortalToOutside(TOP_CENTER_MAP);
        assertEquals(TOP_LEFT_MAP, result);
    }

}
