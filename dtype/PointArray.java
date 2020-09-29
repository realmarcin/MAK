package dtype;



public class PointArray {
    public int x;
    public int y;
    public int[] s;

    public PointArray() {
        x = -1;
        y = -1;
        s = null;
    }

    public PointArray(int a, int b, int[] t) {
        x = a;
        y = b;
        s = t;
    }

    public String toString() {
        StringBuffer prin = new StringBuffer();
        for (int i = 0; i < s.length; i++)
            prin.append(i + " " + s[i] + "   ");
        return (x + "\t" + y + "\t" + (new String(prin)));
    }
}
