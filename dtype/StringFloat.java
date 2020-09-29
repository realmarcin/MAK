package dtype;

public class StringFloat {
    public String a = null;
    public Float x = null;
    public Float x2 = null;

    public StringFloat() {
        this.a = null;
    }

    public StringFloat(String one, Float two) {
        if (one != null)
            this.a = new String(one);
        else
            this.a = null;
        this.x = two;
    }

    public StringFloat(String one, Float two, Float three) {
        if (one != null)
            this.a = new String(one);
        else
            this.a = null;
        this.x = two;
        this.x2 = three;
    }

    public StringFloat(StringFloat a) {
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
