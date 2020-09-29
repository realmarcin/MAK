package dtype;

/**
 * User: marcin
 * Date: Nov 6, 2007
 * Time: 6:01:48 PM
 */
public class StringTriple {

    public String a = null;
    public String b = null;
    public String c = null;


    /**
     *
     */
    public StringTriple() {
    }

    /**
     * @param one
     */
    public StringTriple(String one) {
        this.a = one;
    }

    /**
     * @param one
     * @param two
     * @param three
     */
    public StringTriple(String one, String two, String three) {
        this.a = one;
        this.b = two;
        this.c = three;
    }

    /**
     * @return
     */
    public String toString() {
        String retone = "String a:  " + a + "\n";
        String rettwo = "String b:  " + b + "\n";
        String retthree = "String c:  " + c + "\n";
        return (retone + rettwo + retthree);
    }

}
