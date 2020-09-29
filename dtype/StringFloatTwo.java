package dtype;

public class StringFloatTwo {
    public String a = null;
    public Float x = null;
    public Float y = null;

    public StringFloatTwo() {
        this.a = null;
        this.x = null;
        this.y = null;
    }

    public StringFloatTwo(String one, Float two, Float three) {
        this.a = new String(one);
        this.x = two;
        this.y = three;
    }

    public String toString() {
        String retone = new String("string :  " + this.a + "\n");
        String rettwo = new String("Float 1:  " + this.x + "\n");
        String retthree = new String("Float 2:  " + this.y + "\n");
        return (retone + "\t" + rettwo + "\t" + retthree);
    }

}
