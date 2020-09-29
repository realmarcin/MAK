package dtype;

/**
 * object for storing and sorting tree/contact correlations.
 */
public class NodeComp {

    public int n1;
    public int n2;
    public int pn1;
    public int pn2;
    public double nodediff;
    public double partdiff;
    public double iddiff;

    public NodeComp(int a, int b, int e, int f, double y) {
        n1 = a;
        n2 = b;
        pn1 = e;
        pn2 = f;
        nodediff =
                Math.abs(a - b);
        partdiff = Math.abs(e - f);
        iddiff = y;
    }

    public boolean equals(Object o) {
        NodeComp i = (NodeComp) o;
        return (((n1 == i.n1) || (n1 == i.n2))
                && ((n2 == i.n2) || (n2 == i.n1)));
    }


    public String toString() {
        return ("(n1,n2): (" + n1 + "," + n2 + ") (pn1,pn2): (" + pn1 + "," + pn2 + ") \n");
    }
}
