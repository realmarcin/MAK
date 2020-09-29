package bioobj;

import java.util.Vector;

public class Unigene {

    public String id;
    public String title;
    public String gene;
    public String cytoband;
    public String locuslink;
    public String express;
    public String gnm_terminus;
    public String chromosome;
    public String sts;
    public String txmap;
    public String protsim;
    public String scount;
    public String sequence;

    public Unigene() {
        this.id = new String("");
        this.title = new String("");
        this.gene = new String("");
        this.cytoband = new String("");
        this.locuslink = new String("");
        this.express = new String("");
        this.gnm_terminus = new String("");
        this.chromosome = new String("");
        this.sts = new String("");
        this.txmap = new String("");
        this.protsim = new String("");
        this.scount = new String("");
        this.sequence = new String("");

    }

    public void parseUnigene(Vector s) {

        int i = 0;

        while (i < s.size()) {
            String now = (String) s.elementAt(i);
            //System.out.println(now.indexOf("ID")+"\t"+now);
            if (now.indexOf("ID") == 0) {
                this.id = now.substring(12);
            } else if (now.indexOf("TITLE") == 0) {
                this.title = now.substring(12);
            } else if (now.indexOf("GENE") == 0) {
                this.gene = now.substring(12);
            } else if (now.indexOf("CYTOBAND") == 0) {
                this.cytoband = now.substring(12);
            } else if (now.indexOf("LOCUSLINK") == 0) {
                this.locuslink = now.substring(12);
            } else if (now.indexOf("EXPRESS") == 0) {
                this.express += now.substring(12);
            } else if (now.indexOf("GNM_TERMINUS") == 0) {
                this.gnm_terminus = now.substring(12);
            } else if (now.indexOf("CHROMOSOME") == 0) {
                this.chromosome = now.substring(12);
            } else if (now.indexOf("STS") == 0) {
                this.sts += now.substring(12);
            } else if (now.indexOf("TXMAP") == 0) {
                this.txmap = now.substring(12);
            } else if (now.indexOf("PROTSIM") == 0) {
                this.protsim += now.substring(12) + ";";
            } else if (s.indexOf("SCOUNT") == 0) {
                this.scount += now.substring(12);
            } else if (now.indexOf("SEQUENCE") == 0) {
                this.sequence += now.substring(12) + ";";
            }
            i++;
        }

        this.sequence = util.StringUtil.replace(this.sequence, "'", " ");
        this.title = util.StringUtil.replace(this.title, "'", " ");
        this.express = util.StringUtil.replace(this.express, "'", " ");
        this.sts = util.StringUtil.replace(this.sts, "'", " ");
        this.protsim = util.StringUtil.replace(this.protsim, "'", " ");

    }
}
