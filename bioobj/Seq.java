package bioobj;

import util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 *
 */
public class Seq {

    public String seq;
    public String name;
    public int len;

    /**
     *
     */

    public Seq() {

        seq = new String();
        len = 0;
        name = new String();
    }

    /**
     * @param s
     */
    public Seq(String s) {

        seq = s;
        len = seq.length();
        name = new String();
    }

    /**
     * @param s
     */
    public Seq(String s, String n) {

        seq = s;
        len = seq.length();
        name = n;
    }

    /**
     * @return
     */
    public final String sequence() {
        return seq;
    }


    /**
     * @return
     */
    public final int length() {

        return seq.length();
    }


    /**
     * @return
     */
    public final String name() {

        return name;
    }

    /**
     * @param c
     * @return
     */
    public final static Seq parseSeq(char[] c) {

        /*
        System.out.print("parseSeq ");
        for(int i=0;i<c.length;i++)
            System.out.print(c[i]);
        System.out.print("\nparseSeq\n");
         */

        Seq s = new Seq();

        int i = 0;
        if (c[i] == '>') {

            i = 1;

            String name = "";

            while (c[i] != '\n') {

                name += c[i];
                i++;
            }

            s.name = name;
            //System.out.println("parseSeq "+name);

            i += 1;
            String seq = "";
            while (i > c.length) {

                //if(c[i]!='\n')
                seq += c[i];

                i++;
            }
            //System.out.println("parseSeq "+seq);

            s.seq = seq;
        }

        return s;
    }

    /**
     * @param c
     * @return
     */
    public final static SeqEff parseSeqEff(char[] c) {

        /*
        System.out.print("parseSeq ");
        for(int i=0;i<c.length;i++)
            System.out.print(c[i]);
        System.out.print("\nparseSeq\n");
         */

        SeqEff s = new SeqEff();

        int i = 0;
        if (c[i] == '>') {

            i = 1;

            String name = "";

            while (c[i] != '\n') {

                name += c[i];
                i++;
            }

            name.getChars(0, name.length(), s.name, 0);
            //System.out.println("parseSeq "+name);

            i += 1;
            String seq = "";
            while (i > c.length) {

                //if(c[i]!='\n')
                seq += c[i];

                i++;
            }
            //System.out.println("parseSeq "+seq);

            seq.getChars(0, seq.length(), s.seq, 0);
        }

        return s;
    }

    /**
     * Parses the name of the seq (from Fasta format).
     *
     * @param d
     * @return
     */
    public final static String parseName(String d) {

        String ret = null;
        int i = d.indexOf("|");

        if (i > -1)
            ret = d.substring(0, i);
        else {
            i = d.indexOf(" ");
            if (i > -1)
                ret = d.substring(0, i);
            else
                ret = d;
        }
        return ret;
    }


    /**
     * Parses the id given an id label.
     *
     * @param go seq id label
     * @param d
     * @return
     */
    public final static String parseID(String go, String d) {

        //System.out.println("user firstid parsing  "+d+"  WITH   "+go);
        String ret = null;
        int ia = d.indexOf("|" + go);
        int ib = d.indexOf(go + "|");
        int ea = -1;
        int eb = -1;

//System.out.println("index of go  "+ia+"  WITH   "+go);
//System.out.println("index of go  "+ib+"  WITH   "+go);

        if (ia != -1) {
            ea = d.indexOf("|", ia + 1 + go.length() + 1);
            if (ea == -1)
                ea = d.indexOf(" ", ia + 1 + go.length() + 1);

            if (ia > -1 && ea > -1)
                ret = d.substring(ia + 2 + go.length(), ea);
        } else if (ib != -1) {
            eb = d.indexOf("|", ib + 1 + go.length() + 1);
            if (eb == -1)
                eb = d.indexOf(" ", ib + 1 + go.length() + 1);

            if (ib > -1 && eb > -1)
                ret = d.substring(ib + 1 + go.length(), eb);
        }


        if (ret != null) {
            int ind = ret.indexOf(" ");
            if (ind != -1)
                ret = ret.substring(ind);
        }

        System.out.println("returning PARSEID " + ret);
        return ret;
    }

    /**
     * @param d
     * @return
     */
    public final static String parseFirstID(String d) {

        String ret = null;
        int i = d.indexOf("|");
        int e = d.indexOf("|", i + 4);
        int ie = d.indexOf(".");
        int ei = d.indexOf(":");
        if (ie == -1)
            ie = 10000;
        if (ei == -1)
            ei = 10000;
        int mini = Math.min(ie, ei);

        if (i > -1 && e > -1)
            ret = d.substring(i, e);
        else if (i > -1)
            ret = d.substring(i, d.indexOf(" ", i + 4));
        else if (mini > -1 && mini != 10000) {
            int spa = d.indexOf(" ", mini + 1);
            if (spa == -1)
                spa = d.length();
            int fmin = mini - 2;
            if (mini - 2 < 0)
                fmin = 0;
            ret = d.substring(fmin, spa);
        }
        if (ret == null) {
            int ind = d.indexOf(" *");
            if (ind != -1) {
                ret = d.substring(0, ind);
                ret = StringUtil.replace(ret, " ", "");
            }
        } else
            ret = util.StringUtil.replace(ret, " ", "");

//System.out.println("parsing generic FIRST "+ret);
        return ret;
    }


    /**
     * Parses GenBank style seq definition format.
     *
     * @param d
     * @return
     */
    public final static String parseGB(String d) {

        String ret = null;
        int i = d.indexOf("gb|");
        int e = d.indexOf("|", i + 4);

        if (i > -1 && e > -1)
            ret = d.substring(i + 3, e);
        else if (i > -1) {
            int spa = d.indexOf(" ", i + 4);
            if (spa == -1)
                spa = d.length();
            ret = d.substring(i + 3, spa);
        }

        if (ret != null) {
            int dot = ret.indexOf(".");
            if (dot > -1)
                ret = ret.substring(0, dot);
        }

        return ret;
    }

    /**
     * Parses NCBI style seq definition format.
     *
     * @param d
     * @return
     */
    public final static String parseGI(String d) {

        String ret = null;
        int ia = d.indexOf("gi|");
//int ib = d.indexOf("gi.");
//int ic = d.indexOf("SI:");
        int minend = 0;
        if (ia > -1) {
            minend = Math.min(d.indexOf(" ", ia + 4), d.indexOf("|", ia + 4));
            ret = d.substring(ia + 3, minend);
        }

/*
else if(ib > -1)
{
minend = Math.min(d.indexOf(" ", ib+4), d.indexOf("|", ib+4));
ret = new String(d.substring(ib+3,minend));
}
else if(ic > -1)
{
minend = Math.min(d.indexOf(" ", ic+4), d.indexOf("|", ic+4));
ret = new String(d.substring(ic+3,minend));
}
*/

        if (ret != null) {
            int dot = ret.indexOf(".");
            if (dot > -1)
                ret = ret.substring(0, dot);
        }

        return ret;
    }

    /**
     * Parses PIR seq definition format.
     *
     * @param d
     * @return
     */
    public final static String parsePIR(String d) {

        String ret = null;
        int i = d.indexOf("pir||");
        if (i > -1) {
            ret = d.substring(i + 5, i + 11);
        }
        return ret;
    }


    /**
     * Parses EMBL seq definition format.
     *
     * @param d
     * @return
     */
    public final static String parseEMBL(String d) {

        String ret = null;
        int i = d.indexOf("emb|");
        int e = d.indexOf("|", i + 5);
        if (i > -1 && e > -1)
            ret = d.substring(i + 4, e);
        else if (i > -1) {
            int spa = d.indexOf(" ", i + 4);
            int tilda = d.indexOf("~", i + 4);
//System.out.println("  tilda ind  "+tilda+"    "+(i+14)+"    "+(i+12));
            if (tilda == (i + 14) || tilda == (i + 12))
                ret = d.substring(i + 4, tilda);
            else if (spa > 0)
                ret = d.substring(i + 4, spa);
        }
        if (ret == null) {
            i = d.indexOf("sp|");
            //int d.indexOf("sp.");
            //int d.indexOf("sp:");
            int sla = d.indexOf("|", i + 4);
            if (sla == -1)
                sla = 100000;
            int spa = d.indexOf(" ", i + 4);
            if (spa == -1)
                spa = 100000;
            int minend = Math.min(spa, sla);
            if (i > -1 && minend != 100000)
                ret = d.substring(i + 3, minend);
            /*
            else if(i > -1)
            {
            int spa = d.indexOf(" ", i+4);
            int tilda  = d.indexOf("~",i+4);
            //System.out.println("  tilda ind  "+tilda+"    "+(i+14)+"    "+(i+12));
            if(tilda == (i +14) || tilda == (i +12))
            ret = new String(d.substring(i+4,tilda));
            else if(spa > 0)
            ret = new String(d.substring(i+4,spa));
            }
            */
            /*
            else if( id > -1)
            {
                int minend = Math.min(d.indexOf(" ", id+4), d.indexOf("|", id+4));
                if(minend != -1)
                ret = new String(d.substring(id+3,minend));
                }
            else if( ie > -1)
                {
                    int spa = d.indexOf(" ", ie+3);
                    ret = new String(d.substring(ie+3,spa));
                }
            */
        }
/*
if(ret == null)
{

	int k = d.indexOf("(");
	int ka = d.indexOf(")");
	if(k > 0 && ka > 0 && ka > k)
	ret = new String(d.substring(k+1, ka));
	}
*/
        if (ret != null) {
            int dot = ret.indexOf(".");
            if (dot > -1)
                ret = ret.substring(0, dot);
        }
        return ret;
    }


    /**
     * Parses DBJ seq definition format.
     *
     * @param d
     * @return
     */
    public final static String parseDBJ(String d) {

        String ret = null;
        int i = d.indexOf("dbj|");
        int e = d.indexOf("|", i + 4);

        if (i > -1 && e > -1)
            ret = d.substring(i + 4, e);
        else if (i > -1 && i + 4 < d.length() && d.indexOf(" ") > -1)
            ret = d.substring(i + 4, d.indexOf(" "));
        return ret;
    }

    /**
     * Chops the definition to the first '|' char.
     *
     * @param d
     * @return
     */
    public final static String chopDef(String d) {

        String ret = new String();
        int i = d.lastIndexOf("|");

        int max = i;
        ret = d.substring(max + 1);

        return ret;
    }

    /**
     * @return
     */
    public final String toString() {

        return (">" + name + "\n" + seq);
    }

    /**
     *
     */
    public final static StringBuffer[] readSequence(String file) {
        System.out.println("Assuming only one sequence in file.");
        StringBuffer[] ret = new StringBuffer[2];
        ret[0] = new StringBuffer("");
        ret[1] = new StringBuffer("");
        File test = new File(file);
        if (test.exists())
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String data = in.readLine();
                while (data != null) {
                    int greatind = data.indexOf(">");
                    if (greatind == 0) {
                        ret[1] = new StringBuffer(data.substring(1));
                        data = in.readLine();
                    }
                    if (data == null)
                        break;
                    ret[0].append(new StringBuffer(data));
                    data = in.readLine();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        System.out.println("Finished reading sequence file.");
        return ret;
    }

}
