package dtype;


public class Sens {
    
    public double x;
    public double y;
    public String s;
    public String so;
    public int index;

    public Sens(double a, double b, String t) {
        x = a;
        y = b;
        s = t;
    }

    public Sens(double a, double b, String t, String ti) {
        x = a;
        y = b;
        s = t;
        so = ti;
    }

    public Sens(double a, double b, String t, String ti, int i) {
        x = a;
        y = b;
        s = t;
        so = ti;
        index = i;
    }

    public String toString() {
        if (so != null)
            return (x + "\t" + y + "\t" + s + "\t" + so + "\t" + index);
        else
            return (x + "\t" + y + "\t" + s + "\t" + index);
    }
}
