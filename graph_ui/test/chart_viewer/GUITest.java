package chart_viewer;

import org.junit.Test;

public class GUITest {
    @Test
    public void testGUI() throws Exception {
        GUI gui = new GUI();
        gui.launchTest("D:\\stocks\\stocks");
    }
}