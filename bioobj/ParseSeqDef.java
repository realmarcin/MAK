package bioobj;

import util.StringUtil;

public class ParseSeqDef {

    public String def = null;
    public String gi = null;
    public String embl = null;
    public String dbj = null;
    public String pir = null;
    public String gb = null;

    public double eval = -1;
    public int bits = -1;


    public ParseSeqDef(String d) {

        def = d;
        def = StringUtil.replace(def, "'", " ");
        def = StringUtil.replace(def, "~", " ");

        gi = parseGI();
        embl = parseEMBL();
        dbj = parseDBJ();
        pir = parsePIR();
        gb = parseGB();
        // = chopDef();
        setEvalandBits();
    }

    public ParseSeqDef(Protein now) {
        def = now.name;
        def = StringUtil.replace(def, "'", " ");
        def = StringUtil.replace(def, "~", " ");

        gi = parseGI();
        embl = parseEMBL();
        dbj = parseDBJ();
        pir = parsePIR();
        gb = parseGB();
        //def = chopDef();
        setEvalandBits();
    }

    public void setEvalandBits() {
        String test1 = ";*";
        String test2 = "bits";
        String test3 = "*;";

        int s1 = def.indexOf(test1);
        int s2 = def.lastIndexOf(test2);
        int s3 = def.lastIndexOf(test1);
        int s4 = def.lastIndexOf(test3);

        if (s1 > 0 && s2 > 0)
            bits = Integer.parseInt(def.substring(s1 + 3, s2 - 1));
        if (s3 > 0 && s4 > 0)
            eval = Double.parseDouble(def.substring(s3 + 3, s4 - 1));
    }

    public String parseName() {
        String ret = null;
        int i = def.indexOf(" ");

        if (i > -1)
            ret = def.substring(0, i);
        return ret;
    }

    private String parseGI() {
        String ret = null;
        int i = def.indexOf("gi|");
        int e = def.indexOf("|", i + 4);

        if (i > -1 && e > -1)
            ret = def.substring(i + 3, e);
        else if (i > 0)
            ret = def.substring(i + 3, def.indexOf(" ", i + 3));
        else {
            i = def.indexOf("gi ");
            e = def.indexOf(" ", i + 4);

            if (i > -1 && e > -1)
                ret = def.substring(i + 3, e);
            else if (i > 0)
                ret = def.substring(i + 3, def.indexOf(" ", i + 3));
        }
        return ret;
    }

    private String parseGB() {
        String ret = null;
        int i = def.indexOf("gb|");
        int e = def.indexOf("|", i + 4);

        if (i > -1 && e > -1)
            ret = def.substring(i + 3, e);
        else if (i > -1)
            ret = def.substring(i + 3, def.indexOf(" ", i + 4));

        ret = removeVersion(ret);

        return ret;
    }

    private String parsePIR() {
        String ret = null;
        int i = def.indexOf("pir||");
        if (i > -1) {
            ret = def.substring(i + 5, i + 11);
        }
        return ret;
    }

    private String parseEMBL() {
        String ret = null;
        int i = def.indexOf("emb|");
        int e = def.indexOf("|", i + 5);
        if (i > -1 && e > -1)
            ret = def.substring(i + 4, e);
        else if (i > -1) {
            int spa = def.indexOf(" ", i + 4);
            int tilda = def.indexOf("~", i + 4);
            System.out.println("  tilda ind  " + tilda + "    " + (i + 14) + "    " + (i + 12));
            if (tilda == (i + 14) || tilda == (i + 12))
                ret = def.substring(i + 4, tilda);
            else if (spa > 0)
                ret = def.substring(i + 4, spa);
        }
        if (ret == null) {
            i = def.indexOf("sp|");
            e = def.indexOf("|", i + 4);
            if (i > -1 && e > -1)
                ret = def.substring(i + 3, e);
            else if (i > -1) {
                int spa = def.indexOf(" ", i + 4);
                int tilda = def.indexOf("~", i + 4);
                System.out.println("  tilda ind  " + tilda + "    " + (i + 14) + "    " + (i + 12));
                if (tilda == (i + 14) || tilda == (i + 12))
                    ret = def.substring(i + 4, tilda);
                else if (spa > 0)
                    ret = def.substring(i + 4, spa);
            }
        }
/*
if(ret == null)
{

	int k = def.indexOf("(");
	int ka = def.indexOf(")");
	if(k > 0 && ka > 0 && ka > k)
	ret = def.substring(k+1, ka));
	}
*/
        ret = removeVersion(ret);
        return ret;
    }

    private String parseDBJ() {
        String ret = null;
        int i = def.indexOf("dbj|");
        int e = def.indexOf("|", i + 4);
        if (i > -1 && e > -1)
            ret = def.substring(i + 4, e);
        else if (i > -1)
            ret = def.substring(i + 4, def.indexOf(" "));

        ret = removeVersion(ret);

        return ret;
    }

    /**
     *
     * @return
     */
    public String chopDef() {
        String ret = "";
        int max = def.lastIndexOf("|");
        ret = def.substring(max + 1);

        return ret;
    }

    /**
     * 
     * @param r
     * @return
     */
    private String removeVersion(String r) {
        String ret = null;
        if (r != null) {
            int dot = r.indexOf(".");
            if (dot > -1)
                ret = r.substring(0, dot);
        }
        return ret;
    }
}