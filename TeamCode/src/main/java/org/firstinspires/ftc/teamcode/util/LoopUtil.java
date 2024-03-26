package org.firstinspires.ftc.teamcode.util;

public class LoopUtil {
    private static long lastTime = 0;

    /**
     * @return The nano time between each function call
     */
    public static long getLoopTime() {
        long currentTime = System.nanoTime();
        long delta = lastTime - currentTime;
        lastTime = currentTime;
        return delta;
    }

    public static double getLoopTimeInSeconds() {
        return getLoopTime() / 1000000000.0;
    }

    public static double getLoopTimeInHertz() {
        return 1 / getLoopTimeInSeconds();
    }
}
