package bioobj;

import util.StringUtil;

import java.util.Vector;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 5:15:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Assembly {

    Vector contigs;

    public long globhspmap_min;
    public long globhspmap_max;
    public long globqmap_min;
    public long globqmap_max;
    public long min;
    public long max;

    String finalseq;

    /**
     * Initiates parameters for seq assembly based on a contig model.
     */
    public void initAssembly() {

        contigs = new Vector();
        finalseq = "";
        min = 0;
        max = 0;
        globhspmap_min = 0;
        globhspmap_max = 0;
    }

    /**
     * @param addpart
     * @param map_min
     * @param map_max
     * @param c_min
     * @param c_max
     */
    public void updateAssembly(String addpart, long map_min, long map_max, long c_min, long c_max) {

        Contig co = new Contig(c_min, c_max, map_min, map_max, addpart);
        contigs.addElement((Contig) co);
    }

    /**
     * processes individual contigs for the assembly.
     *
     * @param g
     */
    private void processContig(Contig g) {
        int partlen = g.seq.length();
        dtype.IntSing get = chopPart(g.seq, g.qbegin, g.qend);
        boolean cut = false;

        if (partlen > get.s.length())
            cut = true;

//System.out.println("PROCESSING "+g.qbegin+"  "+g.qend+"  "+g.begin+"  "+g.end+"\t"+g.seq);
//System.out.println(globhspmap_min+"  "+globhspmap_max+"\t : \t"+min+"  "+max);
//add with gaps to RIGHT
        if (get.x > 0 && g.qbegin > max && cut == false) {
            System.out.println("padding right " + g.qbegin + "  " + g.qend);
            finalseq = SeqOp.combineWithGaps(finalseq, (g.qbegin - max), get.s);
            max = g.qend;
            globhspmap_max = g.end;
        }
        //add with gaps to LEFT
        else if (get.x < 0 && g.qend < min && cut == false) {
            System.out.println("padding left " + g.qbegin + "  " + g.qend);
            finalseq = SeqOp.combineWithGaps(get.s, (min - g.qend), finalseq);
            min = g.qbegin;
            globhspmap_min = g.begin;
        }
        //add truncated to RIGHT
        else if (get.x > 0 && g.qbegin < max && cut == true) {
            System.out.println(" right " + g.qbegin + "  " + g.qend);
            finalseq += get.s;
            max = g.qend;
            globhspmap_max = g.end;
        }
//add truncated to LEFT
        else if (get.x < 0 && g.qbegin < max && cut == true) {
            System.out.println("left  " + g.qbegin + "  " + g.qend);
            String tmp = finalseq;
            finalseq = new String();
            finalseq = get.s + tmp;
            min = g.qbegin;
            globhspmap_min = g.begin;
        }
//REPLACE
        else if (get.x == 0 && get.s != null) {
            System.out.println("global " + globhspmap_min + "  " + globhspmap_max + ",  query: " + min + "  " + max);
            if (max != 0) {
                int atrep = (int) (max - g.qbegin);
                //System.out.println("replacing  from " + atrep + "  " + finalseq.length());
                finalseq = StringUtil.replaceAtString(finalseq, get.s, atrep);
            } else {
                finalseq = get.s;
                min = g.qbegin;
                globhspmap_min = g.begin;
                max = g.qend;
                globhspmap_max = g.end;
            }
        }
    }

    /**
     * @return
     */
    public String finishAssembly() {
        if (contigs != null)
            System.out.println("assembling " + contigs.size() + "  contigs.");
        else
            System.out.println("NO! contigs.");

        while (contigs != null && contigs.size() > 0) {
            int i = minContig();
            Contig now = (Contig) contigs.elementAt(i);
            processContig(now);
            //System.out.println(i+"   "+finalseq);
            contigs.removeElementAt(i);
        }
        return finalseq.toLowerCase();
    }

    /**
     * Finds the contig spanning the most 5' seq of the query.
     */
    private int minContig() {

        int querymin = -1;
        long truemin = -1;
        for (int i = 0; i < contigs.size(); i++) {
            Contig now = (Contig) contigs.elementAt(i);
            if (truemin == -1) {
                truemin = now.qbegin;
                querymin = i;
            } else if (now.qbegin < truemin) {
                truemin = now.qbegin;
                querymin = i;
            }
        }
        return querymin;
    }

    /**
     * @param q
     * @param newmin
     * @param newmax
     * @return
     */
    public dtype.IntSing chopPart(String q, long newmin, long newmax) {
        int len = q.length();
        dtype.IntSing ret = null;
        int dir = 0;

        System.out.println(min + "  " + max);
        if (finalseq.length() > 0) {
//left addition
            if (newmin > max && newmax > min) {
                dir = 1;
            }
//right addition
            else if (newmin < min && newmax < min) {
                dir = -1;
            }
//left join
            else if (newmin < min && newmax < max && newmax > min) {
                dir = -1;
                int cutright = (int) (newmax - min);
                q = q.substring(0, cutright - 1);
            }
//right join
            else if (newmax > max && newmin > min && newmin < max) {
                dir = 1;
                int cutleft = (int) (max - newmin);
                q = q.substring(cutleft + 1);
            }
        }
//System.out.println("  dir  "+dir);
        ret = new dtype.IntSing(dir, q);
        return ret;
    }
}

