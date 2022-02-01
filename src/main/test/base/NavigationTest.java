package base;

import org.junit.jupiter.api.Test;

import base.constants.MapConstants;

import base.navigationservice.NavigationService;

import static org.junit.jupiter.api.Assertions.*;

class NavigationTest {

    @Test
    public void TestCenter() {
        //tests the getNextPortalToGetToCenter method
        //method input - BottomLeftMap 
        //expected output - BottomCenterMap
        NavigationService nav = new NavigationService();
        String mapName = "BottomCenterMap";
        String expected = MapConstants.BOTTOM_CENTER_MAP;
        String result = nav.getNextPortalToGetToCenter(mapName);
        assertEquals(expected,result);
    }

    @Test
    public void TestHome() {
        //tests the getNextPortalToGetHome method
        //method input - WaterMap
        //expected output - MainMap
        NavigationService nav = new NavigationService();
        String mapName = "WaterMap";
        String expected = MapConstants.MAIN_MAP;
        String result = nav.getNextPortalToGetToHome(mapName);
        assertEquals(expected,result);
    }

    @Test
    public void TestPortalOne() {
        //tests the getNextPortalToGetToCenter method
        //method inputs - BottomCenterMap, BottomLeftMap
        //expected output - BottomLeftMap
        NavigationService nav = new NavigationService();
        String currentMap = "BottomCenterMap";
        String destination = "BottomLeftMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }

    public void TestPortalTwo(){
        //tests the getNextPortalToGetToCenter method
        //method input - TopCenterMap, TopLeftMap
        //expected output - TopLeftMap
        NavigationService nav = new NavigationService();
        String currentMap = "TopCenterMap";
        String destination = "TopLeftMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }

    public void TestPortalThree(){
        //tests the getNextPortalToGetToCenter method
        //method input - MainMap, TopCenterMap
        //expected output - TopCenterMap
        NavigationService nav = new NavigationService();
        String currentMap = "MainMap";
        String destination = "TopCenterMap";
        String expected = destination;
        String result = nav.getNextPortalTo(currentMap, destination);
        assertEquals(expected,result);
    }

    @Test
    public void TestOutside(){
        //tests the getNextPortalToGetToOutside method
        //method input - TopCenterMap
        //expected output - TopLeftMap
        NavigationService nav = new NavigationService();
        String mapName = "TopCenterMap";
        String expected = MapConstants.TOP_LEFT_MAP;
        String result = nav.getNextPortalToOutside(mapName);
        assertEquals(expected,result);
    }


}
