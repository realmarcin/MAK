package bioobj;

/**
 * object for storing and obtaining seq annotations over the web
 */
public class GenomAnn {
    public String date;
    public double eval;
    public double logeval;
    public double seqid;
    public String db;

    public String swid;
    public String swacc;
    public String swkw;
    public String entrezacc;
    public String pdbacc;

    public String swdef;
    public String entrezdef;
    public String pdbdef;

    public String swseq;
    public String entrezseq;
    public String pdbseq;

    public String hitname;
    public String queryname;
    public String ec;
    public String organism;


    public GenomAnn() {

    }

    public GenomAnn(String qnam, String hnam, double a, double b, double s) {
        queryname = new String(qnam);
        hitname = new String(hnam);
        eval = a;
        logeval = b;
        seqid = s;
    }

    public GenomAnn(String qnam, String hnam, double a, double b, double s, String wdb) {
        queryname = new String(qnam);
        hitname = new String(hnam);
        eval = a;
        logeval = b;
        seqid = s;
        db = new String(wdb);
    }

   /* public int annotNCBI() {
        int ret = -1;
        web.GetEntrez ge = new web.GetEntrez();
        ge.print = false;
        entrezseq = new String(ge.submit(hitname));
        String fullname = null;
        if (ge.name != null) {
            entrezdef = new String(ge.name);
            ret = 1;
        }

        return ret;
    }

    public int annotSW() {
        int ret = -1;
        web.GetSW gsw = new web.GetSW();
        gsw.print = false;

        gsw.submit(hitname);

        if (gsw.swde != null && gsw.swec != null) {
            swdef = new String(gsw.swde);
            ec = new String(gsw.swec);
            organism = new String(gsw.swos);
            swseq = new String(gsw.swfasta);
            ret = 1;
        }
        return ret;
    }

    public int annotSWLocal(string swdat) {
        int ret = -1;
        web.GetSW gsw = new web.GetSW();
        gsw.print = false;
//System.out.println("data    "+swdat);
        gsw.parseSW(swdat);

        if (gsw.swde != null && gsw.swec != null && gsw.swos != null && gsw.swfasta != null) {
            swdef = new String(gsw.swde);
            ec = new String(gsw.swec);
            organism = new String(gsw.swos);
            swseq = new String(gsw.swfasta);
            ret = 1;
        }
        return ret;
    }

    public void writeAnnot(string file) {
        util.GiveDate gd = new util.GiveDate();
        String dat = gd.giveShortDate();
        try {
            //cohen.io.PrintfStream pif = new cohen.io.PrintfStream(file+dat+".annot");
            cohen.io.PrintfStream pif = new cohen.io.PrintfStream(file + ".annot");
            pif.printf("date:  " + dat + "\n");
            pif.printf("query:  " + queryname + "\n");
            pif.printf("hit:  " + hitname + "\n");
            pif.printf("eval:  " + eval + "\n");
            pif.printf("logeval:  " + logeval + "\n");
            pif.printf("seqid:  " + seqid + "\n");
            pif.printf("swacc:  " + swacc + "\n");
            pif.printf("entrezacc:  " + entrezacc + "\n");
            pif.printf("pdbacc:  " + pdbacc + "\n");
            pif.printf("swdef:  " + swdef + "\n");
            pif.printf("entrezdef:  " + entrezdef + "\n");
            pif.printf("pdbdef:  " + pdbdef + "\n");
            pif.printf("ec:  " + ec + "\n");
            pif.printf("organism: " + organism + "\n");
            pif.printf("swseq:  " + swseq + "** \n");
            pif.printf("entrezseq:  " + entrezseq + "** \n");
            pif.printf("pdbseq:  " + pdbseq + "** \n");


            pif.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file");
        }
    }

    public void readAnnot(string file) {
        try {
            BufferedReader in = cohen.IO.openReader(file);
            String data = null;
            StringTokenizer tokens = null;

            int tokType;

            boolean done = false;
            int linecount = 0;
            while (done == false) {
                data = in.readLine();
                linecount++;
                if (data == null) {
                    done = false;
                    break;
                }

                tokens = new StringTokenizer(data);
                String t = tokens.nextToken();
                if (t.indexOf("date:") != -1) {
                    date = tokens.nextToken();
                }
                if (t.indexOf("queryname:") != -1) {
                    queryname = tokens.nextToken();
                }
                if (t.indexOf("hitname:") != -1) {
                    hitname = tokens.nextToken();
                }
                if (t.indexOf("eval:") != -1) {
                    Double hold = new Double(t);
                    eval = hold.doubleValue();
                }
                if (t.indexOf("logeval:") != -1) {
                    Double hold = new Double(t);
                    logeval = hold.doubleValue();

                }
                if (t.indexOf("seqid:") != -1) {
                    Double hold = new Double(t);
                    seqid = hold.doubleValue();

                }
                if (t.indexOf("swacc:") != -1) {
                    swacc = tokens.nextToken();
                }
                if (t.indexOf("entrezacc:") != -1) {
                    entrezacc = tokens.nextToken();
                }
                if (t.indexOf("pdbacc:") != -1) {
                    pdbacc = tokens.nextToken();
                }
                if (t.indexOf("swdef:") != -1) {
                    swdef = tokens.nextToken();
                }
                if (t.indexOf("entrezdef:") != -1) {
                    entrezdef = tokens.nextToken();
                }
                if (t.indexOf("pdbdef:") != -1) {
                    pdbdef = tokens.nextToken();
                }
                if (t.indexOf("ec:") != -1) {
                    ec = tokens.nextToken();
                }
                if (t.indexOf("organism:") != -1) {
                    organism = tokens.nextToken();
                }
                if (t.indexOf("swseq:") != -1) {
                    swseq = tokens.nextToken();
                    data = in.readLine();
                    tokens = new StringTokenizer(data);
                    t = tokens.nextToken();
                    while (t.indexOf("**") != -1) {
                        swseq = swseq + t;
                        t = tokens.nextToken();
                    }
                }
                if (t.indexOf("entrezseq:") != -1) {
                    entrezseq = tokens.nextToken();
                    data = in.readLine();
                    tokens = new StringTokenizer(data);
                    t = tokens.nextToken();
                    while (t.indexOf("**") != -1) {
                        entrezseq = entrezseq + t;
                        t = tokens.nextToken();
                    }
                }
                if (t.indexOf("pdbseq:") != -1) {
                    pdbseq = tokens.nextToken();
                    data = in.readLine();
                    tokens = new StringTokenizer(data);
                    t = tokens.nextToken();
                    while (t.indexOf("**") != -1) {
                        pdbseq = pdbseq + t;
                        t = tokens.nextToken();
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Error with annotation file.");
        }
    }*/

}
