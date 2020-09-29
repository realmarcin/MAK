package dtype;



public class abarray {
    public int x;
    public int y;
    public int[] s;

    public abarray() {
        x = -1;
        y = -1;
        s = null;
    }

    public abarray(int a, int b, int[] t) {
        this.x = a;
        this.y = b;
        this.s = t;
    }

    public String toString() {
        StringBuffer prin = new StringBuffer();
        for (int i = 0; i < s.length; i++)
            prin.append(i + " " + s[i] + "   ");
        return (x + "\t" + y + "\t" + (new String(prin)));
    }
}
