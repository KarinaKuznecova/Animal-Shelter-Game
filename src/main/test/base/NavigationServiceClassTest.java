package base;

import org.junit.jupiter.api.Test;

import base.constants.MapConstants;

import base.navigationservice.NavigationService;

import static org.junit.jupiter.api.Assertions.*;

class NavigationServiceClassTest {

    NavigationService nav = new NavigationService();

    @Test
    public void getNextPortalToGetToCenter_FromBottomLeftMap() {
        String mapName = "BottomLeftMap";
        String expected = MapConstants.BOTTOM_CENTER_MAP;
        String result = nav.getNextPortalToGetToCenter(mapName);
        assertEquals(expected,result);
    }

    @Test
    public void getNextPortalToGetToHome_FromWaterMap() {
        String mapName = "WaterMap";
        String expected = MapConstants.MAIN_MAP;
        String result = nav.getNextPortalToGetToHome(mapName);
        assertEquals(expected,result);
    }

    @Test
    public void getNextPortalTo_FromBottomCenter() {
        String currentMap = "BottomCenterMap";
        String destination = "BottomLeftMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }
    @Test
    public void getNextPortalTo_FromTopCenter(){
        String currentMap = "TopCenterMap";
        String destination = "TopLeftMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }
    @Test
    public void getNextPortalTo_FromMainMap(){
        String currentMap = "MainMap";
        String destination = "TopCenterMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }

    @Test
    public void getNextPortalToOutside_FromTopCenterMap(){
        String mapName = "TopCenterMap";
        String expected = MapConstants.TOP_LEFT_MAP;
        String result = nav.getNextPortalToOutside(mapName);
        assertEquals(expected,result);
    }


}
