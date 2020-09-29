package dtype;

import java.awt.*;

/**
 * Class of variables and methods for display of sequences.
 */
public class itemcol {
    public double x;
    public double y;
    public Color col;

    public itemcol(double a, double b, Color c) {
        this.x = a;
        this.y = b;
        this.col = c;
    }

    public boolean equals(Object o) {
        itemcol i = (itemcol) o;
        return ((x == i.x) && (y == i.y));
    }

    public String toString() {
        return (x + "\t" + y + "\t" + col);
    }
}
