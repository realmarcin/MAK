package util;

import java.awt.*;

/**
 * Color scales graphics.
 * <pre>
 * Version 1.0, 8/97 - original version.
 * </pre>
 *
 * @author Marcin
 * @version 1.0, 8/97
 */
public interface ColorScaleInterface {
    /**
     * Color scales return a color corresponding to a double between
     * 0.0 and 1.0; if out of bounds, the color of the nearest endpoint in the scale if returned.
     */
    public Color colorOf(double x);

    /**
     * A scale going from black to red.
     */
    public class RedScale implements ColorScaleInterface {
        public Color colorOf(double x) {

            if (x <= 0.0) return Color.black;
            if (x >= 1.0) return Color.red;
            return new Color((int) (x * 255.0), 0, 0);
        }
    }

    /**
     * A scale going from black to green.
     */
    public class GreenScale implements ColorScaleInterface {
        public Color colorOf(double x) {

            if (x <= 0.0) return Color.black;
            if (x >= 1.0) return Color.green;
            return new Color(0, (int) (x * 255.0), 0);
        }
    }

    /**
     * A scale going from black to blue.
     */
    public class BlueScale implements ColorScaleInterface {
        public Color colorOf(double x) {

            if (x <= 0.0) return Color.black;
            if (x >= 1.0) return Color.blue;
            return new Color(0, 0, (int) (x * 255.0));
        }
    }

    /**
     * A scale going from black to white.
     */
    public class WhiteScale implements ColorScaleInterface {
        public Color colorOf(double x) {

            if (x <= 0.0) return Color.black;
            if (x >= 1.0) return Color.white;
            int c = (int) (x * 255.0);
            return new Color(c, c, c);
        }
    }

    /**
     * A scale going from white to black.
     */
    public class BlackScale implements ColorScaleInterface {
        public Color colorOf(double x) {

            if (x <= 0.0) return Color.white;
            if (x >= 1.0) return Color.black;
            int c = 255 - (int) (x * 255.0);
            return new Color(c, c, c);
        }
    }

    /**
     * A scale blending one color into another.
     */
    public class BlendScale implements ColorScaleInterface {
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

}
