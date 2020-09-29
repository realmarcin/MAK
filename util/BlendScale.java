package util;

import java.awt.*;

/**
 * A scale blending one color into another.
 */
public class BlendScale {
    private int rMin, rMax;
    private int gMin, gMax;
    private int bMin, bMax;
    private Color colorLow;
    private Color colorHigh;

    public BlendScale(Color lo, Color hi) {
        colorLow = lo;
        colorHigh = hi;
        rMin = lo.getRed();
        gMin = lo.getGreen();
        bMin = lo.getBlue();
        rMax = hi.getRed();
        gMax = hi.getGreen();
        bMax = hi.getBlue();
    }

    public Color colorOf(double x) {
        if (x <= 0.0) return colorLow;
        if (x >= 1.0) return colorHigh;

        int r, g, b;

        if (rMin == rMax)
            r = rMin;
        else
            r = (int) (x * (double) (rMax - rMin)) + rMin;
        if (gMin == gMax)
            g = gMin;
        else
            g = (int) (x * (double) (gMax - gMin)) + gMin;
        if (bMin == bMax)
            b = bMin;
        else
            b = (int) (x * (double) (bMax - bMin)) + bMin;

        return new Color(r, g, b);
    }
}
