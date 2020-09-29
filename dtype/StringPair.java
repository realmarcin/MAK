package dtype;

/**
 *
 */
public class StringPair {
    public String a = null;
    public String b = null;
    public int x;
    public int y;
    public int z;

    public StringPair(String one, String two) {
        this.a = one;
        this.b = two;
    }

    public StringPair(String one, String two, int three, int four) {
        this.a = one;
        this.b = two;
        x = three;
        y = four;
    }

    public StringPair(String one, String two, int three, int four, int five) {
        this.a = one;
        this.b = two;
        x = three;
        y = four;
        z = five;
    }

    public StringPair(String one, String two, int three, int four, int five, int testdiff) {
        this.a = one;
        this.b = two;
        x = three;
        y = four;
        z = five;

        int i = Math.abs(y - x);
        if (i != testdiff) {
            System.out.println("StringPair  failed " + testdiff + "\tvs.\t" + i + "\t" + z + "\t-\t" + y);
        }
    }

    public String toString() {
        String retone = "string one:  " + a + "\n";
        String rettwo = "string two:  " + b + "\n";
        System.out.println(retone);
        System.out.println(rettwo);
        return (retone + rettwo);
    }

    public String toStringFull() {
        return (a + "\t" + b + "\t" + x + "\t" + y + "\t" + z);
    }
}
