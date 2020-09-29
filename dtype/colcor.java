package dtype;


import java.awt.*;

/**
 * Class of variables and methods for display of sequences.
 */
public class colcor {
    public float x;
    public float y;
    public Color col;

    public colcor(float a, float b, Color c) {
        this.x = a;
        this.y = b;
        this.col = c;
    }

    public boolean equals(Object o) {
        colcor i = (colcor) o;
        return ((x == i.x) && (y == i.y));
    }

    public String toString() {
        return (x + "\t" + y + "\t" + col);
    }
}
