package dtype;

import java.awt.*;

/**
 * Class representing an object with (x,y) coordinates and a Color. Can cast a Salact object to salactfloat, possibly losing numerical precision.
 *
 * @Version 1.0 1/1/97
 * @Author Marcin Joachimiak
 */
public class salactdouble {
    public double x;
    public double y;
    public Color c;

    public salactdouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public salactdouble(double x, double y, Color col) {
        this.x = x;
        this.y = y;
        this.c = col;
    }

    public salactdouble(salactdouble old) {
        this.x = old.x;
        this.y = old.y;
        this.c = old.c;
    }

    public salactdouble() {
        x = 0;
        y = 0;
        c = null;
    }

    public String toString() {
        if (c != null)
            return ("x Coord  " + x + "  y Coord  " + y + "  color  " + c.toString());
        else
            return ("x Coord  " + x + "  y Coord  " + y);
    }
}
