package dtype;

import java.awt.*;

/**
 * Class representing an object with (x,y) coordinates and a Color. Can cast a Salact object to salactfloat, possibly losing numerical precision.
 *
 * @Version 1.0 1/1/97
 * @Author Marcin Joachimiak
 */
public class salactfloat {
    public float x;
    public float y;
    public Color c;

    public salactfloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public salactfloat(float x, float y, Color col) {
        this.x = x;
        this.y = y;
        this.c = col;
    }

    public salactfloat(Salact old) {
        this.x = (float) old.x;
        this.y = (float) old.y;
        this.c = old.c;
    }

    public salactfloat() {
        x = 0;
        y = 0;
        c = null;
    }

    public String toString() {
        if (c != null)
            return ("x Coord  " + x + "  y Coord  " + y + "  color " + c.toString());
        else
            return ("x Coord  " + x + "  y Coord  " + y);
    }
}
