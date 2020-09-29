package dtype;

import java.awt.*;

/**
 * Pair of floats as (x,y) coordinates.
 */
public class Salact {
    public double x;
    public double y;
    public Color c;

    public Salact(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Salact(double x, double y, Color col) {
        this.x = x;
        this.y = y;
        this.c = col;
    }

    public Salact(Salact old) {
        this.x = old.x;
        this.y = old.y;
        this.c = old.c;
    }

    public Salact() {
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
