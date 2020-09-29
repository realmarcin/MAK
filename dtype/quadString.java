package dtype;

public class quadString {
    public String w;
    public String x;
    public String y;
    public String z;

    public quadString(String e, String a, String b, String c) {
        this.w = new String(e);
        this.x = new String(a);
        this.y = new String(b);
        this.z = new String(c);
    }

    public quadString(quadString trips) {
        this.w = new String(trips.w);
        this.x = new String(trips.x);
        this.y = new String(trips.y);
        this.z = new String(trips.z);
    }

    public String toString() {
        return (this.w + "     " + this.x + "     " + this.y + "     " + this.z);
    }

}
