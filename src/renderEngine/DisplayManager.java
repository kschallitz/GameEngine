package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

/**
 * Created by Kurt on 6/25/2017.
 */
public class DisplayManager {
    //private static final int WIDTH = 1280;
    private static final int WIDTH = 1920;
    //private static final int HEIGHT = 720;
    private static final int HEIGHT = 1080;
    private static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static float delta;

    public static void createDisplay(){

        ContextAttribs attribs = new ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true);

        try {
            //Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            //Display.setFullscreen(true);
            setDisplayMode(WIDTH, HEIGHT, true);
            Display.create(new PixelFormat(), attribs);
            Display.setTitle("Kurt's Awesome Game");
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        // Use the whole display to render the game
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay(){
        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay(){}

    /**
     *
     * @return Current time in milliSeconds
     */
    private static long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    /**
     * Set the display mode to be used
     *
     * @param width The width of the display required
     * @param height The height of the display required
     * @param fullscreen True if we want fullscreen mode
     */
    public static void setDisplayMode(int width, int height, boolean fullscreen) {

        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width) &&
                (Display.getDisplayMode().getHeight() == height) &&
                (Display.isFullscreen() == fullscreen)) {
            return;
        }

        try {
            DisplayMode targetDisplayMode = null;

            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                //for (int i=0;i<modes.length;i++) {
                //DisplayMode current = modes[i];
                for (DisplayMode current : modes) {
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        // if we've found a match for bpp and frequence against the
                        // original display mode then it's probably best to go for this one
                        // since it's most likely compatible with the monitor
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                                (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width,height);
            }

            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
                return;
            }

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);

        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
        }
    }
}
