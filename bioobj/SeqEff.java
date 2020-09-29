package bioobj;

/**
 * Created by Marcin
 * Date: Jul 14, 2005
 * Time: 6:23:34 PM
 */
public class SeqEff {

    char[] name;
    char[] seq;
    int len;

    public SeqEff() {

        seq = null;
        len = -1;
    }

    public SeqEff(char[] a) {

        seq = a;
        len = a.length;
    }

    public SeqEff(char[] a, char[] n) {

        name = n;
        seq = a;
        len = a.length;
    }

    public SeqEff(Seq s) {

        s.name.getChars(0, s.name.length(),name,0);
        s.seq.getChars(0, s.seq.length(),seq,0);
    }

}
