package bioobj;

import dtype.Branch;

public class ClustalNode {
    public String a = null;
    public Double x = null;
    public Double x2 = null;
//public int branchlevel = -1;
    public Branch br;
    public int open = -1;
    public int close = -1;
    public int branchlevel = -1;

    public ClustalNode() {
        a = null;
        br = new Branch();
        open = -1;
        close = -1;
        branchlevel = -1;
    }

    public ClustalNode(String one, Double two, int o, int c) {
        if (one != null)
            a = one;
        else
            a = null;
        x = two;
        open = o;
        open = c;
        branchlevel = open - close;
    }

    public ClustalNode(String one, Double two) {
        if (one != null)
            a = one;
        else
            a = null;
        x = two;
        open = -1;
        close = -1;
        branchlevel = -1;

    }

    public ClustalNode(String one, Double two, int o, int c, Branch b) {
        if (one != null)
            a = one;
        else
            a = null;
        x = two;

        if (b != null)
            br = new Branch(b);
        open = o;
        close = c;
        branchlevel = open - close;
    }

    public ClustalNode(String one, Double two, Double three, int o, int c) {
        if (one != null)
            a = one;
        else
            a = null;
        x = two;
        x2 = three;

        open = o;
        close = c;
        branchlevel = open - close;
    }

    public ClustalNode(String one, Double two, Double three, int o, int c, Branch b) {
        if (one != null)
            a = one;
        else
            a = null;
        x = two;
        x2 = three;

        if (b != null)
            br = new Branch(b);

        open = o;
        close = c;
        branchlevel = o - c;
    }

    /**
     *
     * @param ca
     */
    public ClustalNode(ClustalNode ca) {
        if (ca.a != null)
            a = ca.a;
        else
            a = null;

        x = ca.x;
        x2 = ca.x2;
        open = ca.open;
        close = ca.close;
        branchlevel = open - close;

        if (ca.br != null)
            br = new Branch(ca.br);
    }

    public String toString() {
        String retone = "string :  " + a;
        String rettwo = " float1:" + x;
        String retthree = "";
        if (x2 != null)
            retthree = " float2:" + x2;
        String b = "none";
        if (br != null)
            b = br.toString();
        return (retone + rettwo + retthree + " open: " + open + "\tclose: " + close);
    }
}
