package dtype;

import java.awt.*;

/**
 * Vector object for storing and sorting tree/contact correlations.
 */
public class Item {
    public int seq;
    public int pos;
    public Color col;
    public String stri;

    public Item(int s, int p, Color c) {
        seq = s;
        pos = p;
        col = c;
    }

    public Item(int s, int p, Color c, String pass) {
        seq = s;
        pos = p;
        col = c;
        stri = pass;
    }

    /**
     * currently checks only seq and pos variables
     */
    public boolean equals(Object o) {
        Item i = (Item) o;
        return ((seq == i.seq) && (pos == i.pos));
    }

    public String toString() {
        return ("seq: " + seq + "\t" + "  pos:  " + pos + "\t" + " color:  " + col + " String: " + stri);
    }
}
