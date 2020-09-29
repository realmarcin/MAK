package bioobj;

import java.util.StringTokenizer;

public class GenscanStats {
    String gnex;
    String type;
    String s;
    long begin;
    long end;
    int len;
    int fr;
    int ph;
    int iac;
    int dot;
    int codrg;
    double p;
    double tscr;


    public GenscanStats(String stats) {
        this.gnex = new String();
        this.type = new String();
        this.s = new String();
        this.begin = (long) -1;
        this.end = (long) -1;
        this.len = -1;
        this.fr = -1;
        this.ph = -1;
        this.iac = -1;
        this.dot = -1;
        this.codrg = -1;
        this.p = (double) -1;
        this.tscr = (double) -1;
        parseStats(stats);
    }

    private void parseStats(String st) {
        StringTokenizer tok = new StringTokenizer(st);
        int count = 0;
        int total = tok.countTokens();
        System.out.println("total tokens " + total);
        while (tok.hasMoreTokens()) {
            String get = tok.nextToken();

            if (count == 0)
                this.gnex = new String(get);
            if (count == 1) {
                this.type = new String(get);
                if (this.type == "PlyA")
                    break;
            }
            if (count == 2)
                this.s = new String(get);
            if (count == 3)
                this.begin = Long.parseLong(get);
            if (count == 4)
                this.end = Long.parseLong(get);
            if (count == 5)
                this.fr = Integer.parseInt(get);
            if (count == 6)
                this.ph = Integer.parseInt(get);
            if (count == 7)
                this.iac = Integer.parseInt(get);
            if (count == 8)
                this.dot = Integer.parseInt(get);
            if (count == 9)
                this.codrg = Integer.parseInt(get);
            if (count == 10)
                this.p = Double.parseDouble(get);
            if (count == 11)
                this.tscr = Double.parseDouble(get);
        }


    }


}
