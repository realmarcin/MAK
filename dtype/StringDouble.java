package dtype;

public class StringDouble {
    public String a = null;
    public Double x = null;
    public Double x2 = null;

    public StringDouble() {
        this.a = null;
    }

    public StringDouble(String one, Double two) {
        if (one != null)
            this.a = new String(one);
        else
            this.a = null;
        this.x = two;
    }

    public StringDouble(String one, Double two, Double three) {
        if (one != null)
            this.a = new String(one);
        else
            this.a = null;
        this.x = two;
        this.x2 = three;
    }

    public StringDouble(StringDouble a) {
        if (a.a != null)
            this.a = new String(a.a);
        else
            this.a = null;
        this.x = a.x;
        this.x2 = a.x2;
    }

    public String toString() {
        String retone = new String("string :  " + this.a);
        String rettwo = new String("X :  " + this.x);
        String retthree = new String("X2 :  " + this.x2);
        return (retone + "\t" + rettwo + "\t" + retthree);
    }

}
