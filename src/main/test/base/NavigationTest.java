package base;

import org.junit.jupiter.api.Test;

import base.constants.MapConstants;

import base.navigationservice.NavigationService;

import static org.junit.jupiter.api.Assertions.*;

class NavigationTest {

    @Test
    public void TestCenter() {
        NavigationService nav = new NavigationService();
        String mapName = "BottomCenterMap";
        String expected = MapConstants.BOTTOM_CENTER_MAP;
        String result = nav.getNextPortalToGetToCenter(mapName);
        assertEquals(expected,result);
    }

    @Test
    public void TestHome() {
        NavigationService nav = new NavigationService();
        String mapName = "WaterMap";
        String expected = MapConstants.MAIN_MAP;
        String result = nav.getNextPortalToGetToHome(mapName);
        assertEquals(expected,result);
    }

    @Test
    public void TestPortalOne() {
        NavigationService nav = new NavigationService();
        String currentMap = "BottomCenterMap";
        String destination = "BottomLeftMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }

    public void TestPortalTwo(){

        NavigationService nav = new NavigationService();
        String currentMap = "TopCenterMap";
        String destination = "TopLeftMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }

    public void TestPortalThree(){
        NavigationService nav = new NavigationService();
        String currentMap = "MainMap";
        String destination = "TopCenterMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }

    @Test
    public void TestOutside(){
        //input - Top center Map
        //expected output - top left map
        NavigationService nav = new NavigationService();
        String mapName = "TopCenterMap";
        String expected = MapConstants.TOP_LEFT_MAP;
        String result = nav.getNextPortalToOutside(mapName);
        assertEquals(expected,result);
    }


}
