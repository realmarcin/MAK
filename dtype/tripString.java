package dtype;

public class tripString {

    public String x;
    public String y;
    public String z;

    public tripString(String a, String b, String c) {
        this.x = a;
        this.y = b;
        this.z = c;
    }

    public tripString(tripString trips) {
        this.x = trips.x;
        this.y = trips.y;
        this.z = trips.z;
    }

    public String toString() {
        return (this.x + "     " + this.y + "     " + this.z);
    }

}
