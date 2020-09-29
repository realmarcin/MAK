package bioobj;

import java.util.Vector;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 6:21:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProtSeqOp {

    public Vector tms;

    /**
     * Returns the type of this amino acid.
     *
     * @param t amino acid
     * @return
     */
    private final static int getType(String t) {

        int ret = -1;
        for (int i = 0; i < SeqConst.type.length; i++) {
            if (SeqConst.type[i].equals(t)) {//System.out.println("analyzing type "+type[i]);
                ret = i;
                break;
            }
        }
        return ret;
    }

    /**
     * @param sequ
     * @param com
     * @return
     */
    public final static int[] analyzeSeq(String sequ, String com) {

        int gettype = getType(com);
        int[] ret = new int[SeqConst.typedata[gettype].length()];
        for (int i = 0; i < sequ.length(); i++) {
            char test = sequ.charAt(i);
            int index = SeqConst.typedata[gettype].indexOf(test);
            if (index != -1) {
                ret[index]++;
            }
        }
        return ret;
    }


    /**
     * Analyzes the seq 'sa' relative to the specified type of residues set 'ty'.
     *
     * @param sa
     * @param ty
     */
    public final static void analyzeType(String sa, String ty) {

        int[] get = null;
        String analyze = null;

        if (ty.equals("signal") && 20 < sa.length())
            analyze = sa.substring(0, 20);

        get = analyzeSeq(analyze, ty);

        mathy.stat.totalNormPerc(get, (double) analyze.length());
    }

    /**
     * @param sequ
     * @return
     */
    public final static int getHPH(String sequ) {

        int gettype = getType("signal");
        int[] ret = new int[SeqConst.typedata[gettype].length()];
        for (int i = 0; i < sequ.length(); i++) {
            char test = sequ.charAt(i);
            int index = SeqConst.typedata[gettype].indexOf(test);
            if (index != -1) {
                ret[index]++;
            }
        }
        int rettotal = (int) mathy.stat.totalNormPerc(ret, (double) sequ.length());
        return rettotal;
    }


    /**
     * Analyzes the seq 'sa' using a window of 'w' residues.
     *
     * @param sa
     * @param w
     */
    public void analyzeTMWindow(String sa, int w, Protein a) {

        tms = new Vector();
        int seqlen = sa.length();
        int[] allcount = new int[seqlen];

        for (int k = 0; k < seqlen; k++) {
            char now = sa.charAt(k);
            int index = SeqConst.typedata[3].indexOf(now);
            allcount[k] = index;
        }

        int start = 0;
        String process = sa;
        while (start + 1 < sa.length() - 15 && start != -1) {
            System.out.println("analyzing from region " + start);
            dtype.sensi now = analyzeTM(sa, w, start + 1, allcount, a);
            start = now.y;
            System.out.println("new start " + start + "  " + now.s);
            if (now.s.length() > 15)
                tms.addElement((dtype.sensi) now);
            if (start == -1)
                break;
        }
//System.out.println("TMs size "+tms.size());
    }

    /**
     * Analyzes the seq 'sa' relative to the specified type of residues set 'ty' using a window of 'w' residues.
     *
     * @param sa
     * @param ty
     * @param w
     */
    public final static void analyzeTypeWindow(String sa, String ty, int w, Protein a) {

        boolean go = true;
        if (sa.charAt(0) != 'M' && sa.charAt(0) != 'm')
            go = false;

        if (go) {
            if (ty.equals("signal") && 20 < sa.length())
                analyzeSeqbyWindow(sa.substring(0, 20), ty, w, a);
            else
                analyzeSeqbyWindow(sa, ty, w, a);
            if (a.leader.length() < 6) {
                a.leaderstart = -1;
                a.leaderstop = -1;
                a.leaderhph = -1;
                a.leader = "NULL";
            }
        } else {
            a.leaderstart = -1;
            a.leaderstop = -1;
            a.leaderhph = -1;
            a.leader = "NULL";
        }
    }


    /**
     * Analyzes a seq using a window -- currently calibrated to a window of 4 residues and parameters of a hydrophobic leader seq.
     *
     * @param sequ
     * @param com
     * @param window
     */
    public final static void analyzeSeqbyWindow(String sequ, String com, int window, Protein a) {

        int gettype = getType(com);
        a.leader = "";

        int seqlen = sequ.length();
        int[] allcount = new int[seqlen];
        for (int k = 0; k < seqlen; k++) {
            char now = sequ.charAt(k);
            int index = SeqConst.typedata[gettype].indexOf(now);
            allcount[k] = index;
        }
        for (int i = 0; i + 3 < seqlen;) {
            char[] now = new char[4];
            now[0] = sequ.charAt(i);
            now[1] = sequ.charAt(i + 1);
            now[2] = sequ.charAt(i + 2);
            now[3] = sequ.charAt(i + 3);

            int[] count = new int[4];
            int c = 0;
            for (int j = 0; j < 4; j++) {
                int index = SeqConst.typedata[gettype].indexOf(now[j]);
                count[j] = index;
                if (index != -1)
                    c++;
            }
//System.out.println(c+"  "+now[0]+" "+now[1]+" "+now[2]+" "+now[3]);
            if (a.leader.length() > 0) {
                if (c == 4) {
                    a.leader += sequ.substring(i, i + 4);
                    //System.out.println("adding  "+sequ.substring(i, i+4));
                } else if (c == 3 && (count[0] != -1 && allcount[i - 1] != -1)) {
                    a.leader += sequ.substring(i, i + 4);
                    //	System.out.println("adding  "+sequ.substring(i, i+4));
                } else if (c == 1 && count[0] > -1) {
                    a.leader += sequ.substring(i, i + 1);
                    //System.out.println("adding  "+sequ.substring(i, i+1));
                    break;
                } else if (c == 2 && count[2] == -1 && now[3] == -1) {
                    a.leader += sequ.substring(i, i + 2);
                    //System.out.println("adding  "+sequ.substring(i, i+2));
                    break;
                } else if (c == 2 && count[0] == -1 && count[1] == -1)
                    break;
                else if (c == 2 && (count[1] == -1 && count[3] == -1)) {
                    a.leader += sequ.substring(i, i + 4);
                    //System.out.println("adding  "+sequ.substring(i, i+4));
                } else if (c == 2 && (count[0] == -1 && count[2] == -1) && allcount[i - 1] != -1) {
                    a.leader += sequ.substring(i, i + 4);
                    //System.out.println("adding  "+sequ.substring(i, i+4));
                }
            } else {
                if (c >= 3 && count[0] > -1) {
                    a.leader += sequ.substring(i, i + 4);
                    //	System.out.println("adding FIRST "+sequ.substring(i, i+4));
                    a.leaderstart = i;
                } else if (c == 3 && count[0] == -1) {
                    a.leader += sequ.substring(i + 1, i + 4);
                    //System.out.println("adding FIRST  "+sequ.substring(i+1, i+4));
                    a.leaderstart = i;
                } else if (c == 2 && (count[0] == -1 && count[2] == -1)) {
                    a.leader += sequ.substring(i + 1, i + 4);
                    //System.out.println("adding FIRST  "+sequ.substring(i+1, i+4));
                    a.leaderstart = i;
                }
                /* don't start the leader 'signal' if 2/4 HPHP pattern
                else if(c == 2 && (count[1] == -1 && count[3] == -1))
                        {leader +=sequ.substring(i, i+4));
                        System.out.println("adding FIRST  "+sequ.substring(i, i+4));
                    leaderstart = i;}
                */
                else if (c == 2 && count[0] == -1 && count[1] == -1) {
                    a.leader += sequ.substring(i + 2, i + 4);
                    //System.out.println("adding FIRST  "+sequ.substring(i+2, i+4));
                    a.leaderstart = i + 2;
                }
            }
            i = i + 4;
        }

        if (a.leaderstart != -1) {
            a.leaderstop = a.leaderstart + a.leader.length();
            a.leaderhph = getHPH(a.leader);
        }
        //System.out.println("LEADER "+leader);
    }


    /**
     * analyzes a seq using a window -- currently calibrated to a window of 4 residues and parameters of a TM
     *
     * @param sequ
     * @param window
     * @param offset
     * @param allcount
     * @return
     */
    public final static dtype.sensi analyzeTM(String sequ, int window, int offset, int[] allcount, Protein a) {

        int tma = -1;
        int tmb = -1;
        String tm = "";

        int seqlen = sequ.length();

        for (int i = offset; i + 3 < seqlen;) {
            //System.out.println(i+"  "+tm);
            char[] now = new char[4];
            now[0] = sequ.charAt(i);
            now[1] = sequ.charAt(i + 1);
            now[2] = sequ.charAt(i + 2);
            now[3] = sequ.charAt(i + 3);

            int[] count = new int[4];
            int c = 0;
            for (int j = 0; j < 4; j++) {
                int index = SeqConst.typedata[3].indexOf(now[j]);
                count[j] = index;
                if (index != -1)
                    c++;
            }
//System.out.println(c+"  "+now[0]+" "+now[1]+" "+now[2]+" "+now[3]);
            if (tm.length() > 0) {
                if (c == 4) {
                    tm += sequ.substring(i, i + 4);
                    //System.out.println("adding  "+sequ.substring(i, i+4));
                } else if (c == 3 && (count[0] != -1 && allcount[i - 1] != -1)) {
                    tm += sequ.substring(i, i + 4);
                    //	System.out.println("adding  "+sequ.substring(i, i+4));
                } else if (c == 1 && count[0] > -1) {
                    tm += sequ.substring(i, i + 1);
                    //System.out.println("adding  "+sequ.substring(i, i+1));
                    break;
                } else if (c == 2 && count[2] == -1 && now[3] == -1) {
                    tm += sequ.substring(i, i + 2);
                    //System.out.println("adding  "+sequ.substring(i, i+2));
                    break;
                } else if (c == 2 && count[0] == -1 && count[1] == -1)
                    break;
                else if (c == 2 && (count[1] == -1 && count[3] == -1)) {
                    tm += sequ.substring(i, i + 4);
                    //System.out.println("adding  "+sequ.substring(i, i+4));
                } else if (c == 2 && (count[0] == -1 && count[2] == -1) && allcount[i - 1] != -1) {
                    tm += sequ.substring(i, i + 4);
                    //System.out.println("adding  "+sequ.substring(i, i+4));
                }
            } else {
                if (c >= 3 && count[0] > -1) {
                    tm += sequ.substring(i, i + 4);
                    //	System.out.println("adding FIRST "+sequ.substring(i, i+4));
                    a.leaderstart = i;
                } else if (c == 3 && count[0] == -1) {
                    tm += sequ.substring(i + 1, i + 4);
                    //System.out.println("adding FIRST  "+sequ.substring(i+1, i+4));
                    a.leaderstart = i;
                } else if (c == 2 && (count[0] == -1 && count[2] == -1)) {
                    tm += sequ.substring(i + 1, i + 4);
                    //System.out.println("adding FIRST  "+sequ.substring(i+1, i+4));
                    tma = i;
                }
                /* don't start the leader 'signal' if 2/4 HPHP pattern
                else if(c == 2 && (count[1] == -1 && count[3] == -1))
                        {leader +=new String(sequ.substring(i, i+4));
                        System.out.println("adding FIRST  "+sequ.substring(i, i+4));
                    leaderstart = i;}
                */
                else if (c == 2 && count[0] == -1 && count[1] == -1) {
                    tm += sequ.substring(i + 2, i + 4);
                    //System.out.println("adding FIRST  "+sequ.substring(i+2, i+4));
                    tma = i + 2;
                }
            }
            i = i + 4;
        }

        if (tma != -1) {
            tmb = tma + tm.length();
        }
        dtype.sensi tmelem = new dtype.sensi(tma, tmb, tm);
//System.out.println("Finished segment TM analysis!");
        return tmelem;
    }


    /**
     * Counts the occurence of single amino acid in seq.
     *
     * @param sequ
     * @param res
     * @return
     */
    public final static int countAA(String sequ, String res) {

        int ret = 0;
        for (int i = 0; i < sequ.length(); i++) {
            String test = "" + sequ.charAt(i);
            if (test.equalsIgnoreCase(res)) {
                ret++;
            }
        }
        return ret;
    }

}
