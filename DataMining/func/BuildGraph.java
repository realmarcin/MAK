package DataMining.func;

import DataMining.*;
import dtype.IntArrayWrapper;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.*;

import java.io.File;
import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Sep 12, 2011
 * Time: 11:09:38 PM
 */
public class BuildGraph {

    boolean debug = false;
    boolean do_all_pvals = false;

    boolean doTFEXP = false, doTFREF = false, doGO = false, doPATH = false, doCOG = false, doTIGR = false, doGMT = false;

    String[] valid_args = {
            "-bic", "-expr", "-mode", "-cut", "-TFEXP", "-TFREF", "-goyeast", "-PATH", "-over",
            "-pval", "-addcoreg", "-exclude", "-pairs", "-geneids", "-goref", "-tab", "-cogcode", "-sgdxref", "-goobo",
            "-debug", "-geneMapTab", "-outdir", "-allpval"
    };
    HashMap options;
    ValueBlockList BIC;
    String prefix;
    SimpleMatrix expr_data;
    int datagenemax, dataexpmax;
    String[][] tab_data;
    String[][] GMT_data;
    String[][] TIGR_data;
    String[] genenames;
    String[] geneids;
    String[] genenames_gmt;
    String[] geneids_gmt;
    String[] datageneids;
    String[][] GO_data;
    String[][] geneMapTab_data;
    String[][] GO_yeast_data;
    String[][] sgdxref_data;
    SimpleMatrix TFEXP_data;
    SimpleMatrix TFREF_data;
    SimpleMatrix TFREF_data_pair;

    String outdir = "";

    ArrayList GO_C = new ArrayList(), GO_P = new ArrayList(), GO_F = new ArrayList();
    String mode = "member";
    String[] modes = {"member"};//, "COG", "tf"};
    double block_overlap_cutoff = 0.01;//0.25;
    //double TF_proportion_cutoff = 0.25;
    double TF_pvalue_cutoff = 0.01;
    double pairTF_pvalue_cutoff = 0.01;
    //double TIGR_proportion_cutoff = 0.25;
    double TIGR_pvalue_cutoff = 0.01;
    double TIGRrole_pvalue_cutoff = 0.01;
    double GO_pvalue_cutoff = 0.01;
    double Path_pvalue_cutoff = 0.01;
    double COG_pvalue_cutoff = 0.01;
    double GMT_pvalue_cutoff = 0.01;

    int data_to_GMTmap_size = 0;
    int data_to_GOmap_size = 0;
    int data_to_COGmap_size = 0;
    int data_to_Pathmap_size = 0;
    int data_to_TIGRmap_size = 0;
    int data_to_TIGRrolemap_size = 0;
    int data_to_TFmap_size = 0;

    int TFcount = 0;
    int TIGRcount = 0;
    int GMTcount = 0;
    int TIGRrolecount = 0;
    int GOcount = 0;
    int Pathcount = 0;
    int COGcount = 0;

    String GMT_header;

    HashMap yeast_gene_names = new HashMap();

    InitRandVar irv;
    long seed = MINER_STATIC.RANDOM_SEEDS[0];//759820;

    /*controls which overlap measure is used (1,2,3): gene_exp, gene, exp */
    int overlap_mode = 1;


    ArrayList summary = new ArrayList();

    public String[] summary_header = {
            "name",
            "block_id",
            "genes",
            "exps",
            "area",
            "full_crit ",
            "exp_mean",
            "TIGRpval",
            //"TIGRftest",
            "TIGR",
            //"noTIGRpval",
            //"ftestnoTIGR",
            "TIGRrolepval",
            //"TIGRroleftest",
            "TIGRrole",
            //"noTIGRrolepval",
            //"ftestnoTIGR",
            "GMTpval",
            "GMT",
            "GOpval",
            //"GOftest",
            "GO",
            //"noGOpval",
            //"noGOftest",
            "Pathpval",
            "Path",
            "TFpval",
            "TF",
            "pairTFpval",
            "pairTF",
            "exp_mean_crit",
            "exp_mse_crit",
            "exp_kendall_crit",
            "exp_reg_crit",
            "inter_crit",
            "feat_crit",
            "TF_crit",
            "COGpval",
            "COG"
    };


    double GOcover, TFcover, pairTFcover, Pathcover, TIGRrolecover, COGcover, GMTcover;
    HashMap TIGRmap, TIGRrolemap, COGmap, PATHmap, GOmap, GOrefMAP, COGcodemap, GMTmap, TFRrefmap;

    HashMap totalGO = new HashMap(), totalPath = new HashMap(), totalTIGRrole = new HashMap(), totalCOG = new HashMap(), totalGMT = new HashMap();
    HashMap foundGO = new HashMap(), foundTF = new HashMap(), foundPairTF = new HashMap(), foundPath = new HashMap(), foundTIGRrole = new HashMap(), foundCOG = new HashMap(), foundGMT = new HashMap();
    boolean addCoRegCases = false;

    String[] exclude_genes;

    boolean pairs = false;

    /**
     * @param args
     */
    public BuildGraph(String[] args) {

        try {
            init(args);

            irv = new InitRandVar(seed, debug);
            //System.out.println("memory.limit(2000)");
            //System.out.println(irv.Rengine.eval("memory.limit(2000)"));

            System.out.println("mode " + mode);
            if (mode.equals("member"))
                doMembers();
            /*else if (mode.equals("COG"))
                doCOG();
            else if (mode.equals("tf"))
                doTFREF();*/

            try {
                Set TFf1 = foundTF.keySet();
                System.out.println("TFs");
                System.out.println("TFs ref " + TFREF_data.data.length);
                MoreArray.printArray((String[]) TFf1.toArray(new String[0]));
            } catch (Exception e) {
                //e.printStackTrace();
            }

            double total = expr_data.data.length * expr_data.data[0].length;

            double great1 = 0;
            double great2 = 0;
            //HashMap allpairs = new HashMap();
            double count = 0;
            for (int i = 0; i < expr_data.data.length; i++) {
                for (int j = 0; j < expr_data.data[0].length; j++) {
                    if (!Double.isNaN(expr_data.data[i][j])) {
                        count++;
                        if (Math.abs(expr_data.data[i][j]) > 1) {
                            great1++;
                        }
                        if (Math.abs(expr_data.data[i][j]) > 2) {
                            great2++;
                        }
                    }
                    //IntPair ip = new IntPair(i, j);
                    //allpairs.put(ip, 1);
                }
            }
            //Set allkeys = allpairs.keySet();

            System.out.println("Data set > 1 fold changers " + great1 + "\tout of " + count + " = " + (great1 / count));
            System.out.println("Data set > 2 fold changers " + great2 + "\tout of " + count + " = " + (great2 / count));

            HashMap genes_exps = new HashMap();
            for (int i = 0; i < BIC.size(); i++) {
                ValueBlock v = (ValueBlock) BIC.get(i);
                genes_exps = LoadHash.addPairAndIncrInt(v.genes, v.exps, genes_exps);
            }
            Set keys = genes_exps.keySet();
            int k = keys.size();

            double bgreat1 = 0;
            double bgreat2 = 0;
            Iterator it = keys.iterator();
            int bcount = 0;
            while (it.hasNext()) {
                /*String cur = (String) it.next();
                //System.out.println(cur);
                int mid = cur.indexOf("_");
                int g = Integer.parseInt(cur.substring(0, mid));//cur.x;//
                int e = Integer.parseInt(cur.substring(mid + 1));//cur.y;//*/
                IntArrayWrapper c = (IntArrayWrapper) it.next();
                int g = c.data[0];
                if (c.data.length > 1) {
                    int e = c.data[1];

                    try {
                        if (!Double.isNaN(expr_data.data[g - 1][e - 1])) {
                            bcount++;
                            if (Math.abs(expr_data.data[g - 1][e - 1]) > 1) {
                                bgreat1++;
                            }
                            if (Math.abs(expr_data.data[g - 1][e - 1]) > 2) {
                                bgreat2++;
                            }
                        }
                    } catch (Exception e1) {
                        System.out.println("out of bounds indices " + g + "\t" + e + "\tdata dim " + expr_data.data.length + "\t" + expr_data.data[0].length);
                        e1.printStackTrace();
                        System.exit(0);
                    }
                }
                //System.out.println("IntPair\t" + g + "\t" + e + "\t" + expr_data.data[g - 1][e - 1] + "\t" + bgreat1 + "\t" + bgreat2);

            }

            String outsum = outdir + prefix + "summary.txt";// "_"+CRIT+"_"+
            summary.add(0, MoreArray.toString(summary_header, "\t"));
            TextFile.write(summary, outsum);
            System.out.println("wrote " + outsum);

            System.out.println("Bicluster set BIC.size() " + BIC.size());// + "\tallkeys " + allkeys.size());
            System.out.println("Bicluster set covers " + bcount + "\tout of " + total + "= " + (bcount / total));
            System.out.println("Bicluster set > 1 fold changers " + bgreat1 + "\tout of " + great1 + " = " + (bgreat1 / great1));
            System.out.println("Bicluster set > 2 fold changers " + bgreat2 + "\tout of " + great2 + " = " + (bgreat2 / great2));

            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        Set GOf = foundGO.keySet();
        Set TFf = foundTF.keySet();
        Set Pathf = foundPath.keySet();
        Set TIGRrolef = foundTIGRrole.keySet();
        Set COGf = foundCOG.keySet();
        Set GMTf = foundGMT.keySet();

        System.out.println("GO biclusters " + GOcount + "\tunique GOs " + GOf.size() + "\tcover " + GOcover);
        System.out.println("TF biclusters " + TFcount + "\tunique TFs " + TFf.size() + "\tcover " + TFcover);
        System.out.println("Path biclusters " + Pathcount + "\tunique Paths " + Pathf.size() + "\tcover " + Pathcover);
        System.out.println("TIGRrole biclusters " + TIGRrolecount + "\tunique TIGRroles " + TIGRrolef.size() + "\tcover " + TIGRrolecover);
        System.out.println("COG biclusters " + COGcount + "\tunique COGs " + COGf.size() + "\tcover " + COGcover);
        System.out.println("GMT biclusters " + GMTcount + "\tunique GMTs " + GMTf.size() + "\tcover " + GMTcover);

        int maxg = 0, maxe = 0;
        for (int i = 0; i < BIC.size(); i++) {
            ValueBlock v = (ValueBlock) BIC.get(i);
            if (v.genes.length > maxg)
                maxg = v.genes.length;
            if (v.exps != null && v.exps.length > maxe)
                maxe = v.exps.length;
        }
        System.out.println("\nmax genes " + maxg + "\texps " + maxe);

        System.exit(0);
    }

    /**
     */
    private void doMembers() {

        String[] TFlabel = null;
        String[] TFlabelpvallist = null;
        double[] TFlabelpval = null;
        String[] pairTFlabel = null;
        String[] pairTFlabelpvallist = null;
        double[] pairTFlabelpval = null;

        String[] TFexplabel = null;
        double[] TFexplabelfreq = null;
        String[] TIGRlabel = null;
        String[] TIGRrolelabel = null;
        double[] TIGRlabelfreq = null;
        double[] TIGRrolelabelpval = null;
        String[] GOlabel = null;
        String[] GOlabelpvallist = null;
        double[] GOlabelpval = null;
        String[] COGlabel = null;
        double[] COGlabelfreq = null;
        String[] Pathlabel = null;
        String[] Pathlabelpvallist = null;
        double[] Pathlabelpval = null;
        String[] GMTlabel = null;
        String[] GMTlabelpvallist = null;
        double[] GMTlabelpval = null;

        /*if (motif_data != null) {
            ArrayList resultsAr = assignTF(BIC.size());
            TFlabel = (String[]) resultsAr.get(0);
            TFlabelpval = (double[]) resultsAr.get(1);
        }*/

        if (doTFREF) {

            ArrayList resultsAr1 = assignTF(BIC, true, true);//, true);
            TFlabel = (String[]) resultsAr1.get(0);
            TFlabelpval = (double[]) resultsAr1.get(1);
            TFlabelpvallist = (String[]) resultsAr1.get(2);
            System.out.println("TF done");

            if (pairs) {
                doPairTF();
                ArrayList resultsAr1a = assignPairTF(BIC, true, true);//, true);
                pairTFlabel = (String[]) resultsAr1a.get(0);
                pairTFlabelpval = (double[]) resultsAr1a.get(1);
                pairTFlabelpvallist = (String[]) resultsAr1a.get(2);
            }

        }

        if (doPATH) {
            ArrayList resultsAr7 = assignPath(true);
            if (resultsAr7 != null && resultsAr7.size() != 0) {
                Pathlabel = (String[]) resultsAr7.get(0);
                Pathlabelpval = (double[]) resultsAr7.get(1);
                Pathlabelpvallist = (String[]) resultsAr7.get(2);
            }
            System.out.println("Path done");
        }

        if (doTFEXP) {
            ArrayList resultsAr2 = assignTFEXP(true);
            TFexplabel = (String[]) resultsAr2.get(0);
            TFexplabelfreq = (double[]) resultsAr2.get(1);
            System.out.println("TFEXP done");
        }

        if (doTIGR) {
            System.out.println("TIGR " + BIC.size());
            ArrayList resultsAr3 = assignTIGRrole(true);
            TIGRrolelabel = (String[]) resultsAr3.get(0);
            TIGRrolelabelpval = (double[]) resultsAr3.get(1);

            ArrayList resultsAr4 = assignTIGR(true);
            TIGRlabel = (String[]) resultsAr4.get(0);
            TIGRlabelfreq = (double[]) resultsAr4.get(1);
            System.out.println("TIGR done");
        }

        if (doGO) {
            ArrayList resultsAr5 = assignGO(true);
            GOlabel = (String[]) resultsAr5.get(0);
            GOlabelpval = (double[]) resultsAr5.get(1);
            GOlabelpvallist = (String[]) resultsAr5.get(2);
            System.out.println("GO done");
        }


        if (doCOG) {
            ArrayList resultsAr6 = assignCOG(true);
            COGlabel = (String[]) resultsAr6.get(0);
            COGlabelfreq = (double[]) resultsAr6.get(1);
        }


        if (doGMT) {
            ArrayList resultsAr8 = assignGMT(true);
            GMTlabel = (String[]) resultsAr8.get(0);
            GMTlabelpval = (double[]) resultsAr8.get(1);
            System.out.println("GMT done");
        }

        HashMap results = new HashMap();
        ArrayList hashes = new ArrayList();

        //build gene_exp hash
        for (int i = 0; i < BIC.size(); i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + BIC.size() + " ");
            ValueBlock vi = (ValueBlock) BIC.get(i);
            HashMap hvi = new HashMap();
            for (int g = 0; g < vi.genes.length; g++) {
                if (vi.exps != null) {
                    for (int e = 0; e < vi.exps.length; e++) {
                        hvi.put("" + vi.genes[g] + "_" + vi.exps[e], 1);
                    }
                } else {
                    hvi.put("" + vi.genes[g], 1);
                }
            }
            if (hvi.size() > 0)
                hashes.add(hvi);


            if (doTFREF)
                System.out.println("doMembers TFlabel[i]_" + i + "\t" + TFlabel[i]);

            /*public String[] summary_header = {
                    "index",
                    "block_id",
                    "genes",
                    "exps",
                    "area",
                    "full_crit ",
                    "exp_mean",
                    "TIGRprop",
                    "TIGRftest",
                    "TIGR",
                    "noTIGRprop",
                    "ftestnoTIGR",
                    "TIGRroleprop",
                    "TIGRroleftest",
                    "TIGRrole",
                    "noTIGRroleprop",
                    "ftestnoTIGR",
                    "GOprop",
                    "GOftest",
                    "GO",
                    "noGOprop",
                    "noGOftest",
                    "TF",
                    "TFfreq",
                    "exp_mean_crit",
                    "exp_mse_crit",
                    "exp_kendall_crit",
                    "exp_reg_crit",
                    "inter_crit",
                    "feat_crit",
                    "TF_crit",
            };*/


            if (TIGRlabelfreq == null) {
                System.out.println("doMembers TIGRlabelfreq is null ");
            }
            if (TIGRlabel == null) {
                System.out.println("doMembers TIGRlabel is null ");
            }
            if (TIGRrolelabelpval == null) {
                System.out.println("doMembers TIGRrolelabelpval is null ");
            }
            if (TIGRrolelabel == null) {
                System.out.println("doMembers TIGRrolelabel is null ");
            }
            if (GMTlabelpval == null) {
                System.out.println("doMembers GMTlabelpval is null");
            }
            if (GMTlabel == null) {
                System.out.println("doMembers GMTlabel is null");
            }
            if (GOlabelpval == null) {
                System.out.println("doMembers GOlabelpval is null ");
            }
            if (GOlabel == null) {
                System.out.println("doMembers GOlabel is null ");
            }
            if (Pathlabelpval == null) {
                System.out.println("doMembers Pathlabelpval is null ");
            }
            if (Pathlabel == null) {
                System.out.println("doMembers Pathlabel is null ");
            }
            if (COGlabel == null) {
                System.out.println("doMembers COGlabel is null ");
            }
            if (TFlabelpval == null) {
                System.out.println("doMembers TFlabelpval is null ");
            }
            if (TFlabel == null) {
                System.out.println("doMembers TFlabel is null ");
            }
            if (pairTFlabel == null) {
                System.out.println("doMembers pairTFlabel is null ");
            }


            String bdata = null;
            if (doTFREF) {
                String pairTFlabelhold = (pairTFlabel != null ? pairTFlabel[i] : "null");
                double pairTFpvalhold = (pairTFlabelpval != null ? (pairTFlabelpval[i] != Double.NaN ? pairTFlabelpval[i] : Double.NaN) : Double.NaN);
                //System.out.println("pairTFlabelhold " + pairTFlabelhold + "\t" + pairTFpvalhold);
                try {

                    String pairData = "0\tnull\t";
                    if (pairTFlabelhold != null)
                        pairData = pairTFpvalhold + "\t" + pairTFlabelhold + "\t";
                    String COGData = "0\tnull\t";
                    if (COGlabel != null && COGlabel[i] != null)
                        COGData = COGlabelfreq[i] + "\t" + COGlabel[i];

                    bdata = "" + (i + 1) + "\t" + vi.blockId() + "\t" + vi.genes.length + "\t" + (vi.exps != null ? vi.exps.length : 0) + "\t" +
                            (vi.genes.length * (vi.exps != null ? vi.exps.length : 1)) + "\t" + vi.full_criterion + "\t" +
                            vi.exp_mean + "\t" + TIGRlabelfreq[i] + "\t" + TIGRlabel[i] + "\t" +
                            TIGRrolelabelpval[i] + "\t" + TIGRrolelabel[i] + "\t" +
                            //GMTlabelpval[i] + "\t" + GMTlabel[i] + "\t" +
                            (GMTlabelpval != null ? GMTlabelpval[i] : 0) + "\t" + (GMTlabel != null ? GMTlabel[i] : 0) + "\t" +
                            GOlabelpval[i] + "\t" + GOlabel[i] + "\t" +
                            Pathlabelpval[i] + "\t" + Pathlabel[i] + "\t" +
                            TFlabelpval[i] + "\t" + TFlabel[i] + "\t" +
                            pairData +
                            vi.all_criteria[0] + "\t" + vi.all_criteria[1] + "\t" + vi.all_criteria[2] + "\t" + vi.all_criteria[3] + "\t" +
                            vi.all_criteria[6] + "\t" + vi.all_criteria[7] + "\t" + vi.all_criteria[8] + "\t" +
                            COGData;
                    summary.add(bdata);
                } catch (Exception e) {

                    if (vi.genes == null)
                        System.out.println("vi.genes is null");
                    if (vi.exps == null)
                        System.out.println("vi.exps is null");
                    if (TIGRlabel == null)
                        System.out.println("TIGRlabel is null");
                    if (TIGRrolelabel == null)
                        System.out.println("TIGRrolelabel is null");
                    if (GMTlabel == null)
                        System.out.println("GMTlabel is null");
                    if (GOlabel == null)
                        System.out.println("GOlabel is null");
                    if (Pathlabel == null)
                        System.out.println("Pathlabel is null");
                    if (TFlabel == null)
                        System.out.println("TFlabel is null");

                    if (vi.all_criteria == null)
                        System.out.println("vi.all_criteria is null");
                    e.printStackTrace();
                }

                System.out.println("attempted summary add 1 " + summary.size());
            } else {
                try {
                    bdata = "" + (i + 1) + "\t" + vi.blockId() + "\t" + vi.genes.length + "\t" + (vi.exps != null ? vi.exps.length : 0) + "\t" +
                            (vi.genes.length * (vi.exps != null ? vi.exps.length : 1)) + "\t" + vi.full_criterion + "\t" +
                            vi.exp_mean + "\t" + (TIGRlabelfreq != null ? TIGRlabelfreq[i] : 0) + "\t" + (TIGRlabel != null ? TIGRlabel[i] : 0) + "\t" +
                            (TIGRrolelabelpval != null ? TIGRrolelabelpval[i] : 0) + "\t" + (TIGRrolelabel != null ? TIGRrolelabel[i] : 0) + "\t" +
                            (GMTlabelpval != null ? GMTlabelpval[i] : 0) + "\t" + (GMTlabel != null ? GMTlabel[i] : 0) + "\t" +
                            (GOlabelpval != null ? GOlabelpval[i] : 0) + "\t" + (GOlabel != null ? GOlabel[i] : 0) + "\t" +
                            "NULL\tNULL\t" +
                            "NULL\tNULL\t" +
                            "NULL\tNULL\t" +
                            vi.all_criteria[0] + "\t" + vi.all_criteria[1] + "\t" + vi.all_criteria[2] + "\t" + vi.all_criteria[3] + "\t" +
                            vi.all_criteria[6] + "\t" + vi.all_criteria[7] + "\t" + vi.all_criteria[8] + "\t" +
                            (COGlabelfreq != null ? COGlabelfreq[i] : 0) + "\t" + (COGlabel != null ? COGlabel[i] : 0);
                    summary.add(bdata);
                } catch (Exception e) {

                    if (vi.genes == null)
                        System.out.println("vi.genes is null");
                    if (vi.exps == null)
                        System.out.println("vi.exps is null");
                    if (TIGRlabel == null)
                        System.out.println("TIGRlabel is null");
                    if (TIGRrolelabel == null)
                        System.out.println("TIGRrolelabel is null");
                    if (GMTlabel == null)
                        System.out.println("GMTlabel is null");
                    if (GOlabel == null)
                        System.out.println("GOlabel is null");
                    if (Pathlabel == null)
                        System.out.println("Pathlabel is null");
                    if (TFlabel == null)
                        System.out.println("TFlabel is null");

                    if (vi.all_criteria == null)
                        System.out.println("vi.all_criteria is null");
                    e.printStackTrace();
                }

                System.out.println("attempted summary add 2 " + summary.size());
            }
        }

        ArrayList done = new ArrayList();
        //pairwise bicluster overlap
        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            HashMap hi = (HashMap) hashes.get(i);
            ValueBlock vi = (ValueBlock) BIC.get(i);
            for (int j = i + 1; j < BIC.size(); j++) {
                ValueBlock vj = (ValueBlock) BIC.get(j);
                HashMap hj = (HashMap) hashes.get(j);
                double overlap = Double.NaN;

                if (vj.exps != null) {
                    if (overlap_mode == 1)
                        overlap = BlockMethods.computeBlockOverlapGeneExpRootProduct(hi, hj,
                                vi.genes.length, vi.exps.length, vj.genes.length, vj.exps.length);
                    else if (overlap_mode == 2)
                        overlap = BlockMethods.computeBlockOverlapGeneRootProduct(vi, vj);
                    else if (overlap_mode == 3)
                        overlap = BlockMethods.computeBlockOverlapExpRootProduct(vi, vj);
                } else {
                    overlap = BlockMethods.computeBlockOverlapGeneRootProduct(vi, vj);
                }

                //System.out.println("overlap " + overlap_mode + "\t" + i + "\t" + j + "\t" + overlap);
                if (overlap > block_overlap_cutoff) {
                    //if (overlap < 0.01)
                    //   overlap = 0;
                    if (debug)
                        System.out.println("lengths " + vi.genes.length + "\t" + vi.exps.length + "\t" +
                                vj.genes.length + "\t" + vj.exps.length +
                                "\toverlap " + overlap);
                    String blockkey = "" + (vi.index != -1 ? vi.index : i) + "_" + (vj.index != -1 ? vj.index : j);
                    results.put(blockkey, overlap);
                    if (MoreArray.getArrayIndUnique(done, vi.index) == -1)
                        done.add(vi.index);
                    if (MoreArray.getArrayIndUnique(done, vj.index) == -1)
                        done.add(vj.index);
                }
            }
        }
        ArrayList outar = new ArrayList();
        ArrayList single = new ArrayList();
        for (int i = 0; i < BIC.size(); i++) {
            ValueBlock vi = (ValueBlock) BIC.get(i);
            if (MoreArray.getArrayIndUnique(done, vi.index) == -1) {
                single.add("" + vi.index);
                outar.add("" + vi.index);
            }
        }

        Set s = results.entrySet();
        Iterator it = s.iterator();


        ArrayList outedge = new ArrayList();
        outedge.add("Overlap");
        ArrayList keyar = new ArrayList();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String member_member_pair = (String) cur.getKey();
            double val = (Double) cur.getValue();
            int ind = member_member_pair.indexOf("_");
            String a = member_member_pair.substring(0, ind);
            String b = member_member_pair.substring(ind + 1);

            outar.add(a + " " + " (pp) " + b);
            if (val > block_overlap_cutoff)
                outedge.add(a + " ((pp)) " + b + " = " + val);

            if (keyar.indexOf(a) == -1)
                keyar.add(a);
            if (keyar.indexOf(b) == -1)
                keyar.add(b);
        }
        String outpath1 = outdir + prefix + "_member_graph_TOTAL.sif";
        if (overlap_mode == 2)
            outpath1 = prefix + "_member_graph_GENE.sif";
        else if (overlap_mode == 3)
            outpath1 = prefix + "_member_graph_EXP.sif";
        TextFile.write(outar, outpath1);

        String outpath2 = outdir + prefix + "member_graph_overlap_TOTAL.eda";
        if (overlap_mode == 2)
            outpath2 = prefix + "member_graph_overlap_GENE.eda";
        if (overlap_mode == 3)
            outpath2 = prefix + "member_graph_overlap_EXP.eda";

        TextFile.write(outedge, outpath2);

        keyar = MoreArray.addArrayList(keyar, single);


        /*for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) BIC.get(i);
        }*/


        ArrayList outarnode = new ArrayList();
        outarnode.add("Criterion");
        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) BIC.get(i);
            outarnode.add((vi.index != -1 ? vi.index : i) + " = " + vi.full_criterion);
        }
        TextFile.write(outarnode, outdir + prefix + "member_graph_nodecriterion.noa");

        ArrayList outarmeanexpr = new ArrayList();
        outarmeanexpr.add("MeanExpr");
        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) BIC.get(i);
            outarmeanexpr.add((vi.index != -1 ? vi.index : i) + " = " + vi.exp_mean / (1.0 / (Math.log(vi.block_area) / Math.log(10))));
        }
        TextFile.write(outarmeanexpr, outdir + prefix + "member_graph_nodemeanexpr.noa");

        ArrayList outararea = new ArrayList();
        outararea.add("BlockArea");
        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) BIC.get(i);

            outararea.add((vi.index != -1 ? vi.index : i) + " = " + Math.log(vi.block_area) / Math.log(10));
        }
        TextFile.write(outararea, outdir + prefix + "member_graph_nodearea.noa");

        if (doTIGR) {
            ArrayList outarTIGRrole = new ArrayList();
            outarTIGRrole.add("TIGRrole");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarTIGRrole.add((vi.index != -1 ? vi.index : i) + " = " + TIGRrolelabel[i]);
            }
            TextFile.write(outarTIGRrole, outdir + prefix + "member_graph_nodeTIGRrole.noa");
        }

        if (doGMT) {
            ArrayList outarGMT = new ArrayList();
            outarGMT.add("GMT");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarGMT.add((vi.index != -1 ? vi.index : i) + " = " + GMTlabel[i]);
            }
            TextFile.write(outarGMT, outdir + prefix + "member_graph_nodeGMT.noa");
        }

        if (doTIGR) {
            ArrayList outarTIGR = new ArrayList();
            outarTIGR.add("TIGR");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarTIGR.add((vi.index != -1 ? vi.index : i) + " = " + TIGRlabel[i]);
            }
            TextFile.write(outarTIGR, outdir + prefix + "member_graph_nodeTIGR.noa");
        }

        if (doGO) {
            ArrayList outarGO = new ArrayList();
            outarGO.add("GO");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarGO.add((vi.index != -1 ? vi.index : i) + " = " + GOlabel[i]);
            }
            TextFile.write(outarGO, outdir + prefix + "member_graph_nodeGO.noa");
        }

        if (doCOG) {
            ArrayList outarCOG = new ArrayList();
            outarCOG.add("COG");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarCOG.add((vi.index != -1 ? vi.index : i) + " = " + COGlabel[i]);
            }
            TextFile.write(outarCOG, outdir + prefix + "member_graph_nodeCOG.noa");
        }

        if (doPATH) {
            ArrayList outarPath = new ArrayList();
            outarPath.add("Path");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarPath.add((vi.index != -1 ? vi.index : i) + " = " + Pathlabel[i]);
            }
            TextFile.write(outarPath, outdir + prefix + "member_graph_nodePath.noa");
        }

        if (doCOG) {
            ArrayList outarCOG = new ArrayList();
            outarCOG.add("COG");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarCOG.add((vi.index != -1 ? vi.index : i) + " = " + COGlabel[i]);
            }
            TextFile.write(outarCOG, outdir + prefix + "member_graph_nodeCOG.noa");
        }

        if (doTIGR && doGO) {
            ArrayList outarTIGRroleGO = new ArrayList();
            outarTIGRroleGO.add("TIGRroleGO");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                String label = "";
                if (TIGRrolelabel[i] != null && !TIGRrolelabel[i].equals("none") && GOlabel[i] != null && !GOlabel[i].equals("none"))
                    label = TIGRrolelabel[i] + " x " + GOlabel[i];
                else if (TIGRrolelabel[i] != null && !TIGRrolelabel[i].equals("none"))
                    label = TIGRrolelabel[i];
                else if (GOlabel[i] != null && !GOlabel[i].equals("none"))
                    label = GOlabel[i];

                outarTIGRroleGO.add((vi.index != -1 ? vi.index : i) + " = " + label);
            }
            TextFile.write(outarTIGRroleGO, outdir + prefix + "member_graph_nodeTIGRroleGO.noa");
        }

        if (doTFREF && doGO && doTIGR && doPATH) {
            ArrayList outarTIGRroleGOPath = new ArrayList();
            outarTIGRroleGOPath.add("TIGRroleGOPath");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                String label = "";
                if (TIGRrolelabel[i] != null && !TIGRrolelabel[i].equals("none")
                        && GOlabel[i] != null && !GOlabel[i].equals("none")
                        && Pathlabel[i] != null && !Pathlabel[i].equals("none"))
                    label = TIGRrolelabel[i] + " x " + GOlabel[i] + " x " + Pathlabel[i];
                else if (TIGRrolelabel[i] != null && !TIGRrolelabel[i].equals("none")
                        && GOlabel[i] != null && !GOlabel[i].equals("none"))
                    label = TIGRrolelabel[i] + " x " + GOlabel[i];
                else if (TIGRrolelabel[i] != null && !TIGRrolelabel[i].equals("none")
                        && Pathlabel[i] != null && !Pathlabel[i].equals("none"))
                    label = TIGRrolelabel[i] + " x " + Pathlabel[i];
                else if (GOlabel[i] != null && !GOlabel[i].equals("none")
                        && Pathlabel[i] != null && !Pathlabel[i].equals("none"))
                    label = GOlabel[i] + " x " + Pathlabel[i];
                else if (TIGRrolelabel[i] != null && !TIGRrolelabel[i].equals("none"))
                    label = TIGRrolelabel[i];
                else if (GOlabel[i] != null && !GOlabel[i].equals("none"))
                    label = GOlabel[i];
                else if (Pathlabel[i] != null && !Pathlabel[i].equals("none"))
                    label = Pathlabel[i];

                outarTIGRroleGOPath.add((vi.index != -1 ? vi.index : i) + " = " + label);
            }
            TextFile.write(outarTIGRroleGOPath, outdir + prefix + "member_graph_nodeTIGRroleGOPath.noa");
        }

        //Localization
        if (doGO) {
            ArrayList localize = new ArrayList();
            localize.add("Localization");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                String label = "";
                if (GOlabel[i] != null && !GOlabel[i].equals("none")) {
                    label = Localize.makeGroupLabel(GOlabel[i]);
                } else {
                    label = "none";
                }

                localize.add((vi.index != -1 ? vi.index : i) + " = " + label);
            }
            TextFile.write(localize, outdir + prefix + "member_graph_localize.noa");
        }

        //Functional classes
        if (doGO && doTIGR && doPATH) {
            ArrayList funclass = new ArrayList();
            funclass.add("FunctionalClass");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                String label = "";
                //if (GOlabel[i] != null && !GOlabel[i].equals("none")) {

                label = AssignFunClass.makeGroupLabel(GOlabel[i], Pathlabel[i], TIGRrolelabel[i]);
                /*} else {
                    label = "none";
                }*/

                funclass.add((vi.index != -1 ? vi.index : i) + " = " + label);
            }
            TextFile.write(funclass, outdir + prefix + "member_graph_funclass.noa");
        } else if (doGO && doTIGR && doCOG) {
            ArrayList funclass = new ArrayList();
            funclass.add("FunctionalClass");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                String label = "";
                //if (GOlabel[i] != null && !GOlabel[i].equals("none")) {
                System.out.println("funclass " + GOlabel[i] + "\t" + COGlabel[i] + "\t" + TIGRrolelabel[i]);
                label = AssignFunClass.makeGroupLabel(GOlabel[i], null, TIGRrolelabel[i]);
                            /*} else {
                                label = "none";
                            }*/

                funclass.add((vi.index != -1 ? vi.index : i) + " = " + label);
            }
            TextFile.write(funclass, outdir + prefix + "member_graph_funclass.noa");
        }

        if (doTIGR) {
            ArrayList outarTIGRval = new ArrayList();
            outarTIGRval.add("TIGRval");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarTIGRval.add((vi.index != -1 ? vi.index : i) + " = " + TIGRlabelfreq[i]);
            }
            TextFile.write(outarTIGRval, outdir + prefix + "member_graph_nodeTIGRval.noa");
        }

        if (doGO) {
            ArrayList outarGOval = new ArrayList();
            outarGOval.add("GOval");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarGOval.add((vi.index != -1 ? vi.index : i) + " = " + GOlabelpval[i]);
            }
            TextFile.write(outarGOval, outdir + prefix + "member_graph_nodeGOval.noa");
        }

        if (doCOG) {
            ArrayList outarCOGval = new ArrayList();
            outarCOGval.add("COGval");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarCOGval.add((vi.index != -1 ? vi.index : i) + " = " + COGlabelfreq[i]);
            }
            TextFile.write(outarCOGval, outdir + prefix + "member_graph_nodeCOGval.noa");
        }

        if (doPATH) {
            ArrayList outarPathval = new ArrayList();
            outarPathval.add("Pathval");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarPathval.add((vi.index != -1 ? vi.index : i) + " = " + Pathlabelpval[i]);
            }
            TextFile.write(outarPathval, outdir + prefix + "member_graph_nodePathval.noa");
        }

        if (doTIGR) {
            ArrayList outarTIGRroleval = new ArrayList();
            outarTIGRroleval.add("TIGRroleval");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarTIGRroleval.add((vi.index != -1 ? vi.index : i) + " = " + TIGRrolelabelpval[i]);
            }
            TextFile.write(outarTIGRroleval, outdir + prefix + "member_graph_nodeTIGRroleval.noa");
        }

/*
        if (doGMT) {
            ArrayList outarGMTval = new ArrayList();
            outarGMTval.add("GMT");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarGMTval.add((vi.index != -1 ? vi.index : i) + " = " + GMTlabel[i]);
            }
            TextFile.write(outarGMT, prefix + "member_graph_nodeGMT.noa");
        }
*/
        if (doTFREF) {
            ArrayList outarTF = new ArrayList();
            outarTF.add("TF");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                try {
                    outarTF.add((vi.index != -1 ? vi.index : i) + " = " + TFlabel[i]);
                } catch (Exception e) {
                    if (vi == null)
                        System.out.print("vi == null");
                    if (TFlabel == null)
                        System.out.print("TFlabel == null");
                    if (TFlabel[i] == null)
                        System.out.print("TFlabel[i] == null");
                    e.printStackTrace();
                }
            }
            TextFile.write(outarTF, outdir + prefix + "member_graph_nodeTF.noa");

            if (pairs) {
                ArrayList outarpairTF = new ArrayList();
                outarpairTF.add("pairTF");
                for (int i = 0; i < BIC.size(); i++) {
                    System.out.print(".");
                    ValueBlock vi = (ValueBlock) BIC.get(i);
                    try {
                        outarpairTF.add((vi.index != -1 ? vi.index : i) + " = " + pairTFlabel[i]);
                    } catch (Exception e) {
                        if (vi == null)
                            System.out.print("vi == null");
                        if (pairTFlabel == null)
                            System.out.print("pairTFlabel == null");
                        if (pairTFlabel[i] == null)
                            System.out.print("pairTFlabel[i] == null");
                        e.printStackTrace();
                    }
                }
                TextFile.write(outarpairTF, outdir + prefix + "member_graph_nodepairTF.noa");
            }

            ArrayList outarTFexp = new ArrayList();
            outarTFexp.add("TFexp");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarTFexp.add((vi.index != -1 ? vi.index : i) + " = " + TFexplabel[i]);
            }
            TextFile.write(outarTFexp, outdir + prefix + "member_graph_nodeTFexp.noa");

            ArrayList outarTFval = new ArrayList();
            outarTFval.add("TFval");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarTFval.add((vi.index != -1 ? vi.index : i) + " = " + TFlabelpval[i]);
            }
            TextFile.write(outarTFval, outdir + prefix + "member_graph_nodeTFval.noa");

            ArrayList outarTFexpval = new ArrayList();
            outarTFexpval.add("TFexpval");
            for (int i = 0; i < BIC.size(); i++) {
                System.out.print(".");
                ValueBlock vi = (ValueBlock) BIC.get(i);
                outarTFexpval.add((vi.index != -1 ? vi.index : i) + " = " + TFexplabelfreq[i]);
            }
            TextFile.write(outarTFexpval, outdir + prefix + "member_graph_nodeTFexpval.noa");
        }

        ArrayList outdot = new ArrayList();
        outdot.add("digraph membergraph {");
        String add = "node [shape=ellipse fontsize=20 fontname=\"Helvetica\"];";
        for (int i = 0; i < keyar.size(); i++) {
            String k = (String) keyar.get(i);
            add += " " + k;
        }
        add += ";";
        outdot.add(add);
        outdot.add("");

        for (int i = 0; i < keyar.size(); i++) {
            String k = (String) keyar.get(i);
            String addlabel = null;
            addlabel = "\"" + k + "\"" + "[label=\"" + k + "\"];";
            outdot.add(addlabel);
        }
        outdot.add("");


        if (block_overlap_cutoff > 0)
            System.out.println("applying filter: discard if x < " + block_overlap_cutoff);
        it = s.iterator();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String member_member_pair = (String) cur.getKey();

            double val = (Double) cur.getValue();
            if (val > block_overlap_cutoff) {
                int ind = member_member_pair.indexOf("_");
                String a = member_member_pair.substring(0, ind);
                String b = member_member_pair.substring(ind + 1);

                String vs = "" + val;
                if (vs.indexOf("E") != -1)
                    vs = "" + 0;
                outdot.add("\"" + a + "\" -> \"" + b + "\"  [weight=" + vs + "];");
            }
        }
        outdot.add("}");

        String outpath = outdir + prefix + "_member_graph_TOTAL.dot";
        if (overlap_mode == 2)
            outpath = prefix + "_member_graph_GENE.dot";
        if (overlap_mode == 3)
            outpath = prefix + "_member_graph_EXP.dot";
        System.out.println("writing " + outpath);
        TextFile.write(outdot, outpath);

        //labelCooccur(TFlabel, GOlabel, Pathlabel);
        System.out.println("WARNING did not run labelCooccur");

        /* TODO debug problem with new GO data representation */
        System.out.println("WARNING GOvsPathCoreg GOvsGOCoreg PathvsPathCoreg disabled");
        /*if (doTFREF) {
            GOvsPathCoreg(GOlabel, GOlabelpvallist, Pathlabel, Pathlabelpvallist);
            GOvsGOCoreg(GOlabel, GOlabelpvallist);
            PathvsPathCoreg(Pathlabel, Pathlabelpvallist);

            //doPairTF();
        }*/
    }

    /**
     *
     */

    public void doPairTF() {

        //xlabels are gene targets
        //ylabels are TFs
        ArrayList pairTFdatatmp = new ArrayList();
        ArrayList pairTFdataLabels = new ArrayList();
        for (int a = 0; a < TFREF_data.ylabels.length; a++) {
            for (int b = a + 1; b < TFREF_data.ylabels.length; b++) {
                int[] data = new int[TFREF_data.xlabels.length];
                for (int i = 0; i < TFREF_data.xlabels.length; i++) {
                    if (TFREF_data.data[a][i] == 1 && TFREF_data.data[b][i] == 1) {
                        data[i] = 1;
                    }
                }

                double sum = stat.sumEntries(data);
                //reject singletons and 2s to reduce size of dataset, also not amenable to statistics
                if (sum > 2) {
                    pairTFdatatmp.add(data);
                    String s = TFREF_data.ylabels[a] + "__" + TFREF_data.ylabels[b];
                    System.out.println(a + "\t" + b + "\t" + s + "\t" + sum);
                    pairTFdataLabels.add(s);
                }
            }
        }
        System.out.println("doPairTF " + pairTFdataLabels.size() + "\t" + pairTFdatatmp.size());
        String[] ylabels = MoreArray.ArrayListtoString(pairTFdataLabels);
        pairTFdataLabels = null;
        int[][] pairTFdata = MoreArray.convtoIntNoRotate(pairTFdatatmp);

        System.out.println("TFREF_data " + TFREF_data.data.length + "\t" + TFREF_data.data[0].length);
        System.out.println("TFREF_data labels " + TFREF_data.ylabels.length + "\t" + TFREF_data.xlabels.length);
        System.out.println("pairTFdata " + pairTFdata.length + "\t" + pairTFdata[0].length);
        System.out.println("ylabels " + ylabels.length + "\txlabels " + TFREF_data.xlabels.length);

        TFREF_data_pair = new SimpleMatrix(pairTFdata, TFREF_data.xlabels, ylabels);

        System.out.println("writing pairTF.txt");//pairTF_great2.txt");
        System.out.println("TFREF_data_pair.data " + TFREF_data_pair.data.length + "\t" + TFREF_data_pair.data[0].length);
        System.out.println("TFREF_data_pair.ylabels " + TFREF_data_pair.ylabels.length + "\tTFREF_data_pair.xlabels " + TFREF_data_pair.xlabels.length);

        TabFile.write(TFREF_data_pair.data, outdir + "pairTF.txt", TFREF_data_pair.xlabels, TFREF_data_pair.ylabels, "\t", "", "");
    }


    /**
     * Analyzes coocurrence of validation labels for all pairs of biclusters.
     *
     * @param TFlabel
     * @param GOlabel
     * @param pathlabel
     */
    private void labelCooccur(String[] TFlabel, String[] GOlabel, String[] pathlabel) {

        System.out.println("labelCooccur length " + TFlabel.length + "\t" + GOlabel.length + "\t" + pathlabel.length);
        //code to count cooccurence of labels
        HashMap GOs = new HashMap();
        for (int a = 0; a < GOlabel.length; a++) {
            if (GOlabel[a] != null && GOlabel[a].length() > 0) {//&& !GOlabel[a].equals("none")
                GOlabel[a] = StringUtil.replace(GOlabel[a], "GO: ", "");
                if (GOlabel[a].indexOf("_") == -1) {
                    //System.out.println("labelCooccur GO " + a + "\t" + GOlabel[a]);
                    GOs.put(GOlabel[a], 1);
                } else {
                    String[] split1 = GOlabel[a].split("_");
                    for (int b = 0; b < split1.length; b++) {
                        //split1[b] = StringUtil.replace(split1[b], "GO: ", "");
                        //System.out.println("labelCooccur split GO " + a + "\t" + b + "\t" + split1[b]);
                        GOs.put(split1[b], 1);
                    }
                }
            }
        }
        HashMap Paths = new HashMap();
        for (int a = 0; a < pathlabel.length; a++) {
            if (pathlabel[a] != null && pathlabel[a].length() > 0) {//&& !pathlabel[a].equals("none")
                pathlabel[a] = StringUtil.replace(pathlabel[a], "Path: ", "");
                if (pathlabel[a].indexOf("_") == -1) {
                    //System.out.println("labelCooccur path " + a + "\t" + pathlabel[a]);
                    Paths.put(pathlabel[a], 1);
                } else {
                    String[] split1 = pathlabel[a].split("_");
                    //System.out.println("labelCooccur" + split1.length + "\t\t" + pathlabel[a]);
                    for (int b = 0; b < split1.length; b++) {
                        //split1[b] = StringUtil.replace(split1[b], "Path: ", "");
                        //System.out.println("labelCooccur split path " + a + "\t" + b + "\t" + split1[b]);
                        Paths.put(split1[b], 1);
                    }
                }
            }
        }
        HashMap TFs = new HashMap();
        for (int a = 0; a < TFlabel.length; a++) {
            if (TFlabel[a] != null && TFlabel[a].length() > 0) {//&& !TFlabel[a].equals("none")
                TFlabel[a] = StringUtil.replace(TFlabel[a], "TF: ", "");
                if (TFlabel[a].indexOf("_") == -1) {
                    //System.out.println("labelCooccur TF " + a + "\t" + TFlabel[a]);
                    TFs.put(TFlabel[a], 1);
                } else {
                    String[] split1 = TFlabel[a].split("_");
                    for (int b = 0; b < split1.length; b++) {
                        //split1[b] = StringUtil.replace(split1[b], "TF: ", "");
                        //System.out.println("labelCooccur split TF " + a + "\t" + b + "\t" + split1[b]);
                        TFs.put(split1[b], 1);
                    }
                }
            }
        }

        Set GOskey = GOs.keySet();
        int glen = GOskey.size();
        Iterator iter = GOskey.iterator();
        String[] GOsStr = new String[glen];
        int count = 0;
        while (iter.hasNext()) {
            String key = (String) iter.next();
            GOsStr[count] = key;
            count++;
        }
        System.out.println("GOs " + MoreArray.toString(GOsStr, ","));

        Set Pathskey = Paths.keySet();
        int plen = Pathskey.size();
        Iterator iter2 = Pathskey.iterator();
        String[] PathsStr = new String[plen];
        int count2 = 0;
        while (iter2.hasNext()) {
            String key = (String) iter2.next();
            PathsStr[count2] = key;
            count2++;
        }
        System.out.println("Paths " + MoreArray.toString(PathsStr, ","));

        Set TFskey = TFs.keySet();
        int tlen = TFskey.size();
        Iterator iter3 = TFskey.iterator();
        String[] TFsStr = new String[tlen];
        int count3 = 0;
        while (iter3.hasNext()) {
            String key = (String) iter3.next();
            TFsStr[count3] = key;
            count3++;
        }
        System.out.println("TFs " + MoreArray.toString(TFsStr, ","));

        //GO vs Path
        HashMap GO_Path = new HashMap();
        for (int a = 0; a < GOlabel.length; a++) {
            if (GOlabel[a] != null && GOlabel[a].length() > 0) {
                GOlabel[a] = StringUtil.replace(GOlabel[a], "GO: ", "");
                String[] split1 = GOlabel[a].split("_");
                for (int b = 0; b < split1.length; b++) {
                    for (int i = 0; i < pathlabel.length; i++) {
                        if (pathlabel[i] != null && pathlabel[i].length() > 0) {
                            pathlabel[i] = StringUtil.replace(pathlabel[i], "Path: ", "");
                            String[] split2 = pathlabel[i].split("_");
                            for (int j = 0; j < split2.length; j++) {
                                //combined key
                                String key = split1[b] + "_" + split2[j];
                                if (GO_Path.get(key) == null) {
                                    //System.out.println("GO_Path " + key + "\t" + 1);
                                    GO_Path.put(key, 1);
                                } else {
                                    int val = (Integer) GO_Path.get(key);
                                    int newval = val + 1;
                                    //System.out.println("GO_Path " + key + "\t" + newval);
                                    GO_Path.put(key, newval);
                                }

                            }
                        }
                    }
                }
            }
        }
        //GO vs TF
        HashMap GO_TF = new HashMap();
        for (int a = 0; a < GOlabel.length; a++) {
            if (GOlabel[a] != null && GOlabel[a].length() > 0) {
                GOlabel[a] = StringUtil.replace(GOlabel[a], "GO: ", "");
                String[] split1 = GOlabel[a].split("_");
                for (int b = 0; b < split1.length; b++) {
                    for (int i = 0; i < TFlabel.length; i++) {
                        if (TFlabel[i] != null && TFlabel[i].length() > 0) {
                            TFlabel[i] = StringUtil.replace(TFlabel[i], "TF: ", "");
                            String[] split2 = TFlabel[i].split("_");
                            for (int j = 0; j < split2.length; j++) {
                                //combined key
                                String key = split1[b] + "_" + split2[j];
                                if (GO_TF.get(key) == null) {
                                    //System.out.println("GO_TF " + key + "\t" + 1);
                                    GO_TF.put(key, 1);
                                } else {
                                    int val = (Integer) GO_TF.get(key);
                                    int newval = val + 1;
                                    //System.out.println("GO_TF " + key + "\t" + newval);
                                    GO_TF.put(key, newval);
                                }

                            }
                        }
                    }
                }
            }
        }
        //Path vs TF
        HashMap Path_TF = new HashMap();
        for (int a = 0; a < pathlabel.length; a++) {
            if (pathlabel[a] != null && pathlabel[a].length() > 0) {
                pathlabel[a] = StringUtil.replace(pathlabel[a], "Path: ", "");
                String[] split1 = pathlabel[a].split("_");
                for (int b = 0; b < split1.length; b++) {
                    for (int i = 0; i < TFlabel.length; i++) {
                        if (TFlabel[i] != null && TFlabel[i].length() > 0) {
                            TFlabel[i] = StringUtil.replace(TFlabel[i], "TF: ", "");
                            String[] split2 = TFlabel[i].split("_");
                            for (int j = 0; j < split2.length; j++) {
                                //combined key
                                String key = split1[b] + "_" + split2[j];
                                if (Path_TF.get(key) == null) {
                                    //System.out.println("Path_TF " + key + "\t" + 1);
                                    Path_TF.put(key, 1);
                                } else {
                                    int val = (Integer) Path_TF.get(key);
                                    int newval = val + 1;
                                    //System.out.println("Path_TF " + key + "\t" + newval);
                                    Path_TF.put(key, newval);
                                }

                            }
                        }
                    }
                }
            }
        }

        //GO vs Path
        int[][] gp = new int[glen][plen];
        Set GO_Paths_keys = GO_Path.keySet();
        Iterator itgp = GO_Paths_keys.iterator();
        while (itgp.hasNext()) {
            String c = (String) itgp.next();
            int ind = c.indexOf("_");
            String cg = c.substring(0, ind);
            String cp = c.substring(ind + 1);
            int val = (Integer) GO_Path.get(c);
            int gind = MoreArray.getArrayInd(GOsStr, cg);
            int pind = MoreArray.getArrayInd(PathsStr, cp);
            if (val > 0) {
                System.out.println("GO_Path > 0 " + c + "\t" + cg + "\t" + cp + "\t" + val + "\t" + gind + "\t" + pind);
            }
            gp[gind][pind] = val;
        }
        String out = prefix + "_GO_vs_Path.txt";
        TabFile.write(gp, outdir + out, PathsStr, GOsStr);

        //GO vs TF
        //GOlabel
        //TFlabel
        int[][] gt = new int[glen][tlen];
        Set GO_TF_keys = GO_TF.keySet();
        Iterator itgt = GO_TF_keys.iterator();
        while (itgt.hasNext()) {
            String c = (String) itgt.next();
            int ind = c.indexOf("_");
            String cg = c.substring(0, ind);
            String ct = c.substring(ind + 1);
            int val = (Integer) GO_TF.get(c);
            int gind = MoreArray.getArrayInd(GOsStr, cg);
            int tind = MoreArray.getArrayInd(TFsStr, ct);
            if (val > 0) {
                System.out.println("GO_TF > 0 " + c + "\t" + cg + "\t" + ct + "\t" + val + "\t" + gind + "\t" + tind);
            }
            gt[gind][tind] = val;
        }
        String out2 = prefix + "_GO_vs_TF.txt";
        TabFile.write(gt, outdir + out2, TFsStr, GOsStr);

        //Path vs TF
        //Pathlabel
        //TFlabel
        int[][] pt = new int[plen][tlen];
        Set Path_TF_keys = Path_TF.keySet();
        Iterator itpt = Path_TF_keys.iterator();
        while (itpt.hasNext()) {
            String c = (String) itpt.next();
            //System.out.println("Path_TF c " + c);
            int ind = c.indexOf("_");
            String cp = c.substring(0, ind);
            String ct = c.substring(ind + 1);
            int val = (Integer) Path_TF.get(c);
            int pind = MoreArray.getArrayInd(PathsStr, cp);
            int tind = MoreArray.getArrayInd(TFsStr, ct);
            if (val > 0) {
                System.out.println("Path_TF > 0 " + c + "\t:" + cp + ":\t:" + ct + ":\t" + val + "\t" + pind + "\t" + tind);
            }
            pt[pind][tind] = val;
        }
        String out3 = prefix + "_Path_vs_TF.txt";
        TabFile.write(pt, outdir + out3, TFsStr, PathsStr);
    }

    /**
     * Analyzes coocurrence of validation labels for all pairs of biclusters.
     *
     * @param GOlabel
     * @param pathlabel
     */
    private void GOvsPathCoreg(String[] GOlabel, String[] GOlabelpval, String[] pathlabel, String[] pathlabelpval) {

        ArrayList outpart = new ArrayList();
        ValueBlockList partBlocks = new ValueBlockList();

        //GO vs Path
        for (int i = 0; i < BIC.size(); i++) {
            if (GOlabel[i] != null && !GOlabel[i].equals("none") && GOlabel[i].length() > 0) {

                GOlabel[i] = StringUtil.replace(GOlabel[i], "GO: ", "");

                String[] split1 = null;
                String[] orig = null;

                System.out.println("GOvsPathCoreg GOlabelpval[i]_" + i + "\t" + GOlabelpval[i]);

                String[] split1pvalOrig = {GOlabelpval[i]};
                String[] split1pval = null;
                if (GOlabelpval[i].indexOf("__") != -1) {
                    split1pvalOrig = GOlabelpval[i].split("__");
                    split1 = GOlabel[i].split("_");
                    orig = MoreArray.copy(split1);
                    Arrays.sort(split1);

                    split1pval = new String[split1pvalOrig.length];
                    for (int u = 0; u < split1pvalOrig.length; u++) {
                        int index = StringUtil.getFirstEqualsIndex(split1, orig[u]);
                        split1pval[index] = split1pvalOrig[u];

                        if (split1pval[index] == null) {
                            System.out.println("GOvsPathCoreg split1pval[index] is null" + i + "\t" + index + "\t" + GOlabelpval[i]);
                        }
                    }
                } else {
                    split1 = new String[1];
                    split1[0] = GOlabel[i];
                    orig = split1;
                    split1pval = new String[1];
                    split1pval[0] = GOlabelpval[i];
                }


                for (int j = 0; j < split1.length; j++) {
                    if (pathlabel[i] != null && !pathlabel[i].equals("none") && pathlabel[i].length() > 0) {

                        pathlabel[i] = StringUtil.replace(pathlabel[i], "Path: ", "");
                        String[] split2 = null;
                        String[] orig2 = null;
                        String[] split2pvalOrig = {pathlabelpval[i]};
                        String[] split2pval = null;

                        if (pathlabelpval[i].indexOf("__") != -1) {
                            split2pvalOrig = pathlabelpval[i].split("__");
                            split2 = pathlabel[i].split("_");
                            Arrays.sort(split2);
                            orig2 = MoreArray.copy(split2);
                            split2pval = new String[split2pvalOrig.length];
                            for (int u = 0; u < split2pvalOrig.length; u++) {
                                int index = StringUtil.getFirstEqualsIndex(split2, orig2[u]);
                                split2pval[index] = split2pvalOrig[u];
                            }
                        } else {
                            split2 = new String[1];
                            split2[0] = pathlabel[i];
                            orig2 = split2;
                            split2pval = new String[1];
                            split2pval[0] = pathlabelpval[i];
                        }


                        for (int k = 0; k < split2.length; k++) {
                            ValueBlock v = (ValueBlock) BIC.get(i);

                            String[] geneids = new String[v.genes.length];
                            for (int g = 0; g < geneids.length; g++) {
                                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                                geneids[g] = datageneids[v.genes[g] - 1];
                            }

                            ArrayList ggenes = new ArrayList();
                            for (int g = 0; g < geneids.length; g++) {
                                ArrayList GOar = (ArrayList) GOmap.get(geneids[g]);
                                if (GOar != null) {
                                    for (int h = 0; h < GOar.size(); h++) {
                                        String[] GO = (String[]) GOar.get(h);
                                        //System.out.println("GOvsPathCoreg " + h + "\t" + MoreArray.toString(GO, ","));
                                        if (GO[4].equals(split1[j]))
                                            ggenes.add(geneids[g]);
                                    }
                                }
                            }

                            ArrayList pgenes = new ArrayList();
                            for (int g = 0; g < geneids.length; g++) {
                                ArrayList Pathar = (ArrayList) PATHmap.get(geneids[g]);
                                if (Pathar != null) {
                                    for (int h = 0; h < Pathar.size(); h++) {
                                        String Path = (String) Pathar.get(h);
                                        if (Path.equals(split2[k]))
                                            pgenes.add(geneids[g]);
                                    }
                                }
                            }

                            ArrayList pggenes = new ArrayList();
                            //find common p and g
                            for (int u = 0; u < pgenes.size(); u++) {
                                String p = (String) pgenes.get(u);
                                for (int t = 0; t < ggenes.size(); t++) {
                                    String g = (String) ggenes.get(t);
                                    if (p.equals(g))
                                        pggenes.add(p);
                                }
                            }

                            ArrayList genesum = new ArrayList();
                            for (int u = 0; u < ggenes.size(); u++) {
                                String p = (String) ggenes.get(u);
                                genesum.add(p);
                            }
                            for (int u = 0; u < pgenes.size(); u++) {
                                String p = (String) pgenes.get(u);
                                if (!genesum.contains(p))
                                    genesum.add(p);
                            }

                            ArrayList genesumInt = new ArrayList();
                            for (int u = 0; u < genesum.size(); u++) {
                                String curs = (String) genesum.get(u);
                                int index = StringUtil.getFirstEqualsIndex(geneids, curs);
                                genesumInt.add(v.genes[index]);
                            }


                            ValueBlock vnew = new ValueBlock(MoreArray.ArrayListtoInt(genesumInt), v.exps);
                            vnew.setDataAndMean(expr_data.data);

                            partBlocks.add(vnew);
                            outpart.add("" + i + "\t" + i + "_" + split1[j] + "\t" + split1[j] + "\t" + split1pval[j] + "\t" +
                                    i + "_" + split2[k] + "\t" + split2[k] + "\t" + split2pval[k] + "\t" +
                                    ggenes.size() + "\t" + pgenes.size() + "\t" +
                                    pggenes.size() + "\t" +
                                    ((double) pggenes.size() / (double) v.genes.length) + "\t" +
                                    //TF + "\t" + TFpval + "\t" + (1 - TFpval) + "\t" +
                                    //((double) (pgenes.size() + ggenes.size()) / (double) v.genes.length) + "\t" +
                                    vnew.exp_mean);
                        }
                    }
                }
            }
        }


        ArrayList result = assignTF(partBlocks, false, addCoRegCases ? true : false);

        String[] TFlabel = (String[]) result.get(0);
        double[] TFlabelfreq = (double[]) result.get(1);
        String[] TFlabelpval = (String[]) result.get(2);

        ArrayList out = new ArrayList();
        for (int i = 0; i < partBlocks.size(); i++) {

            //int index = Integer.parseInt(str.split("\t")[0]);
            //double minp = Double.NaN;
            //String minTF = null;
            if (TFlabelpval[i] != null) {

                double meanp = Double.NaN;
                if (TFlabelpval[i].indexOf("__") != -1) {
                    String[] ps = (TFlabelpval[i].split("__"));
                    double[] meanpAr = new double[ps.length];
                    for (int j = 0; j < ps.length; j++) {
                        meanpAr[j] = Double.parseDouble(ps[j]);
                    }
                    meanp = stat.avg(meanpAr);
                } else {
                    meanp = Double.parseDouble(TFlabelpval[i]);
                }
                String str = (String) outpart.get(i) + "\t" + TFlabel[i] + "\t" + meanp + "\t" + (1 - meanp);

                out.add(str);

                /*if (TFlabelpval[i].indexOf("_") != -1) {
                    minp = Double.parseDouble((TFlabelpval[i].split("_"))[0]);
                    minTF = (TFlabel[i].split("_"))[0];
                    System.out.println("GOvsPathCoreg multiple TFs significant " + TFlabel[i] + "\t" + TFlabelpval[i]);
                } else {
                    minp = Double.parseDouble(TFlabelpval[i]);
                    minTF = TFlabel[i];
                }

                if (minTF != null && minp < TF_pvalue_cutoff) {
                    String str = (String) outpart.get(i);
                    System.out.println("GOvsPathCoreg Bicluster cooccur " + i + "\t" + str +
                            "\tTF " + minTF + "\t" + TFlabelpval[i]);

                    str += "\t" + minTF + "\t" + minp + "\t" + (1 - minp);

                    out.add(str);
                }*/
            }
        }

        String out3 = prefix + "_GO_vs_Path_commonTF.txt";
        TextFile.write(out, outdir + out3);
    }


    /**
     * Analyzes coocurrence of validation labels for all pairs of biclusters.
     *
     * @param GOlabel
     */
    private void GOvsGOCoreg(String[] GOlabel, String[] GOlabelpval) {

        ArrayList outpart = new ArrayList();
        ValueBlockList partBlocks = new ValueBlockList();

        //GO vs Path
        for (int i = 0; i < BIC.size(); i++) {
            if (GOlabel[i] != null && !GOlabel[i].equals("none") && GOlabel[i].length() > 0) {
                GOlabel[i] = StringUtil.replace(GOlabel[i], "GO: ", "");

                String[] split1 = null;
                String[] orig = null;

                System.out.println("GOvsGOCoreg GOlabelpval[i]_" + i + "\t" + GOlabelpval[i] + "\t" + GOlabel[i]);

                String[] split1pvalOrig = {GOlabelpval[i]};
                String[] split1pval = null;
                if (GOlabelpval[i].indexOf("__") != -1) {
                    split1pvalOrig = GOlabelpval[i].split("__");
                    split1 = GOlabel[i].split("_");
                    orig = MoreArray.copy(split1);
                    Arrays.sort(split1);

                    split1pval = new String[split1pvalOrig.length];
                    for (int u = 0; u < split1pvalOrig.length; u++) {
                        int index = StringUtil.getFirstEqualsIndex(split1, orig[u]);
                        split1pval[index] = split1pvalOrig[u];

                        if (split1pval[index] == null) {
                            System.out.println("GOvsGOCoreg split1pval[index] is null" + i + "\t" + index + "\t" + GOlabelpval[i]);
                        }
                    }
                } else {
                    split1 = new String[1];
                    split1[0] = GOlabel[i];
                    orig = split1;
                    split1pval = new String[1];
                    split1pval[0] = GOlabelpval[i];
                }

                for (int j = 0; j < split1.length; j++) {
                    for (int k = j + 1; k < split1.length; k++) {

                        ValueBlock v = (ValueBlock) BIC.get(i);

                        String[] geneids = new String[v.genes.length];
                        for (int g = 0; g < geneids.length; g++) {
                            //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                            geneids[g] = datageneids[v.genes[g] - 1];
                        }

                        ArrayList ggenes = new ArrayList();
                        for (int g = 0; g < geneids.length; g++) {
                            ArrayList GOar = (ArrayList) GOmap.get(geneids[g]);
                            if (GOar != null) {
                                for (int h = 0; h < GOar.size(); h++) {
                                    String[] GO = (String[]) GOar.get(h);
                                    //System.out.println("GOvsPathCoreg " + h + "\t" + MoreArray.toString(GO, ","));
                                    if (GO[4].equals(split1[j]))
                                        ggenes.add(geneids[g]);
                                }
                            }
                        }

                        ArrayList pgenes = new ArrayList();
                        for (int g = 0; g < geneids.length; g++) {
                            ArrayList Pathar = (ArrayList) GOmap.get(geneids[g]);
                            if (Pathar != null) {
                                for (int h = 0; h < Pathar.size(); h++) {
                                    String[] GO = (String[]) Pathar.get(h);
                                    if (GO[4].equals(split1[k]))
                                        pgenes.add(geneids[g]);
                                }
                            }
                        }

                        ArrayList pggenes = new ArrayList();
                        //find common p and g
                        for (int u = 0; u < pgenes.size(); u++) {
                            String p = (String) pgenes.get(u);
                            for (int t = 0; t < ggenes.size(); t++) {
                                String g = (String) ggenes.get(t);
                                if (p.equals(g))
                                    pggenes.add(p);
                            }
                        }

                        ArrayList genesum = new ArrayList();
                        for (int u = 0; u < ggenes.size(); u++) {
                            String p = (String) ggenes.get(u);
                            genesum.add(p);
                        }
                        for (int u = 0; u < pgenes.size(); u++) {
                            String p = (String) pgenes.get(u);
                            if (!genesum.contains(p))
                                genesum.add(p);
                        }

                        ArrayList genesumInt = new ArrayList();
                        for (int u = 0; u < genesum.size(); u++) {
                            String curs = (String) genesum.get(u);
                            int index = StringUtil.getFirstEqualsIndex(geneids, curs);
                            genesumInt.add(v.genes[index]);
                        }


                        ValueBlock vnew = new ValueBlock(MoreArray.ArrayListtoInt(genesumInt), v.exps);
                        vnew.setDataAndMean(expr_data.data);

                        partBlocks.add(vnew);

                        String s = i + "_" + split1[k];
                        String s1 = "" + i + "\t" + i + "_" + split1[j] + "\t" + split1[j] + "\t" + split1pval[j] + "\t" +
                                s + "\t" + split1[k] + "\t" + split1pval[k] + "\t" +
                                ggenes.size() + "\t" + pgenes.size() + "\t" +
                                pggenes.size() + "\t" +
                                ((double) pggenes.size() / (double) v.genes.length) + "\t" +
                                vnew.exp_mean;

                        //if (s.equals("9_translation"))
                        //    System.out.println("9_translation " + s1);
                        outpart.add(s1);
                    }
                }
            }
        }

        ArrayList result = assignTF(partBlocks, false, addCoRegCases ? true : false);//) true);

        String[] TFlabel = (String[]) result.get(0);
        double[] TFlabelfreq = (double[]) result.get(1);
        String[] TFlabelpval = (String[]) result.get(2);

        ArrayList out = new ArrayList();
        for (int i = 0; i < partBlocks.size(); i++) {

            //double minp = Double.NaN;
            //String minTF = null;

            if (TFlabelpval[i] != null) {
                double meanp = Double.NaN;
                if (TFlabelpval[i].indexOf("__") != -1) {
                    String[] ps = (TFlabelpval[i].split("__"));
                    double[] meanpAr = new double[ps.length];
                    for (int j = 0; j < ps.length; j++) {
                        meanpAr[j] = Double.parseDouble(ps[j]);
                    }
                    meanp = stat.avg(meanpAr);
                } else {
                    meanp = Double.parseDouble(TFlabelpval[i]);
                }
                String str = (String) outpart.get(i) + "\t" + TFlabel[i] + "\t" + meanp + "\t" + (1 - meanp);

                out.add(str);

                /*if (TFlabelpval[i].indexOf("_") != -1) {
                    minp = Double.parseDouble((TFlabelpval[i].split("_"))[0]);
                    minTF = (TFlabel[i].split("_"))[0];
                    System.out.println("GOvsGOCoreg multiple TFs significant " + TFlabel[i] + "\t" + TFlabelpval[i]);
                } else {
                    minp = Double.parseDouble(TFlabelpval[i]);
                    minTF = TFlabel[i];
                }

                if (minTF != null && minp < TF_pvalue_cutoff) {
                    String str = (String) outpart.get(i);

                    System.out.println("GOvsGOCoreg Bicluster cooccur " + i + "\t" + str +
                            "\tTF " + minTF + "\t" + TFlabelpval[i]);

                    str += "\t" + minTF + "\t" + minp + "\t" + (1 - minp);

                    out.add(str);
                }*/
            }
        }

        String out3 = prefix + "_GO_vs_GO_commonTF.txt";
        TextFile.write(out, outdir + out3);
    }

    /**
     * Analyzes coocurrence of validation labels for all pairs of biclusters.
     *
     * @param pathlabel
     */
    private void PathvsPathCoreg(String[] pathlabel, String[] pathlabelpval) {

        ArrayList outpart = new ArrayList();

        ValueBlockList partBlocks = new ValueBlockList();

        //GO vs Path
        for (int i = 0; i < BIC.size(); i++) {
            if (pathlabel[i] != null && !pathlabel[i].equals("none") && pathlabel[i].length() > 0) {
                pathlabel[i] = StringUtil.replace(pathlabel[i], "Path: ", "");

                String[] split1 = null;
                String[] orig = null;
                String[] split1pvalOrig = {pathlabelpval[i]};
                String[] split1pval = null;

                if (pathlabelpval[i].indexOf("__") != -1) {
                    split1pvalOrig = pathlabelpval[i].split("__");
                    split1 = pathlabel[i].split("_");
                    Arrays.sort(split1);
                    orig = MoreArray.copy(split1);
                    split1pval = new String[split1pvalOrig.length];
                    for (int u = 0; u < split1pvalOrig.length; u++) {
                        int index = StringUtil.getFirstEqualsIndex(split1, orig[u]);
                        split1pval[index] = split1pvalOrig[u];
                    }
                } else {
                    split1 = new String[1];
                    split1[0] = pathlabel[i];
                    orig = split1;
                    split1pval = new String[1];
                    split1pval[0] = pathlabelpval[i];
                }

                System.out.println("PathvsPathCoreg lengths " + split1.length + "\t" + split1pval.length);
                System.out.println("PathvsPathCoreg l " + MoreArray.toString(split1, ","));
                System.out.println("PathvsPathCoreg p " + MoreArray.toString(split1pval, "\t"));

                for (int j = 0; j < split1.length; j++) {
                    for (int k = j + 1; k < split1.length; k++) {
                        ValueBlock v = (ValueBlock) BIC.get(i);

                        String[] geneids = new String[v.genes.length];
                        for (int g = 0; g < geneids.length; g++) {
                            //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                            geneids[g] = datageneids[v.genes[g] - 1];
                        }

                        ArrayList ggenes = new ArrayList();
                        for (int g = 0; g < geneids.length; g++) {
                            ArrayList Pathar = (ArrayList) PATHmap.get(geneids[g]);
                            if (Pathar != null) {
                                for (int h = 0; h < Pathar.size(); h++) {
                                    String Path = (String) Pathar.get(h);
                                    if (Path.equals(split1[j]))
                                        ggenes.add(geneids[g]);
                                }
                            }
                        }

                        ArrayList pgenes = new ArrayList();
                        for (int g = 0; g < geneids.length; g++) {
                            ArrayList Pathar = (ArrayList) PATHmap.get(geneids[g]);
                            if (Pathar != null) {
                                for (int h = 0; h < Pathar.size(); h++) {
                                    String Path = (String) Pathar.get(h);
                                    if (Path.equals(split1[k]))
                                        pgenes.add(geneids[g]);
                                }
                            }
                        }

                        ArrayList pggenes = new ArrayList();
                        //find common p and g
                        for (int u = 0; u < pgenes.size(); u++) {
                            String p = (String) pgenes.get(u);
                            for (int t = 0; t < ggenes.size(); t++) {
                                String g = (String) ggenes.get(t);
                                if (p.equals(g))
                                    pggenes.add(p);
                            }
                        }

                        //System.out.println("Bicluster " + i + " has GO: " + GOlabel[i] +
                        //        " and Path: " + pathlabel[i] + "\tshared genes " + pggenes.size());

                        //ArrayList apTF = getMaxTF(MoreArray.ArrayListtoString(pgenes), 0.01);
                        //ArrayList agTF = getMaxTF(MoreArray.ArrayListtoString(ggenes), 0.01);

                        ArrayList genesum = new ArrayList();
                        for (int u = 0; u < ggenes.size(); u++) {
                            String p = (String) ggenes.get(u);
                            genesum.add(p);
                        }
                        for (int u = 0; u < pgenes.size(); u++) {
                            String p = (String) pgenes.get(u);
                            if (!genesum.contains(p))
                                genesum.add(p);
                        }

                        ArrayList genesumInt = new ArrayList();
                        for (int u = 0; u < genesum.size(); u++) {
                            String curs = (String) genesum.get(u);
                            int index = StringUtil.getFirstEqualsIndex(geneids, curs);
                            genesumInt.add(v.genes[index]);
                        }


                        ValueBlock vnew = new ValueBlock(MoreArray.ArrayListtoInt(genesumInt), v.exps);
                        vnew.setDataAndMean(expr_data.data);

                        partBlocks.add(vnew);
                        outpart.add("" + i + "\t" + i + "_" + split1[j] + "\t" + split1[j] + "\t" + split1pval[j] + "\t" +
                                i + "_" + split1[k] + "\t" + split1[k] + "\t" + split1pval[k] + "\t" +
                                ggenes.size() + "\t" + pgenes.size() + "\t" +
                                pggenes.size() + "\t" +
                                ((double) pggenes.size() / (double) v.genes.length) + "\t" +
                                //TF + "\t" + TFpval + "\t" + (1 - TFpval) + "\t" +
                                //((double) (pgenes.size() + ggenes.size()) / (double) v.genes.length) + "\t" +
                                vnew.exp_mean);
                    }
                }
            }
        }


        ArrayList result = assignTF(partBlocks, false, addCoRegCases ? true : false);//, true);

        String[] TFlabel = (String[]) result.get(0);
        double[] TFlabelfreq = (double[]) result.get(1);
        String[] TFlabelpval = (String[]) result.get(2);

        ArrayList out = new ArrayList();
        for (int i = 0; i < partBlocks.size(); i++) {

            //double minp = Double.NaN;
            //String minTF = null;
            if (TFlabelpval[i] != null) {

                double meanp = Double.NaN;
                if (TFlabelpval[i].indexOf("__") != -1) {
                    String[] ps = TFlabelpval[i].split("__");

                    double[] meanpAr = new double[ps.length];
                    for (int j = 0; j < ps.length; j++) {
                        try {
                            meanpAr[j] = Double.parseDouble(ps[j]);
                        } catch (NumberFormatException e) {
                            System.out.println("PathvsPathCoreg TFlabelpval[i] problem " + TFlabel[i] + "\t" + TFlabelpval[i]);
                            e.printStackTrace();
                        }
                    }
                    meanp = stat.avg(meanpAr);
                } else {
                    meanp = Double.parseDouble(TFlabelpval[i]);
                }
                String str = (String) outpart.get(i) + "\t" + TFlabel[i] + "\t" + meanp + "\t" + (1 - meanp);

                out.add(str);

                /*if (TFlabelpval[i].indexOf("_") != -1) {
                    minp = Double.parseDouble((TFlabelpval[i].split("_"))[0]);
                    minTF = (TFlabel[i].split("_"))[0];
                    System.out.println("PathvsPathCoreg multiple TFs significant " + TFlabel[i] + "\t" + TFlabelpval[i]);
                } else {
                    minp = Double.parseDouble(TFlabelpval[i]);
                    minTF = TFlabel[i];
                }

                if (minTF != null && minp < TF_pvalue_cutoff) {
                    String str = (String) outpart.get(i);

                    System.out.println("PathvsPathCoreg Bicluster cooccur " + i + "\t" + str +
                            "\tTF " + minTF + "\t" + TFlabelpval[i]);

                    str += "\t" + minTF + "\t" + minp + "\t" + (1 - minp);

                    out.add(str);
                }*/
            }
        }

        String out3 = prefix + "_Path_vs_Path_commonTF.txt";
        TextFile.write(out, outdir + out3);
    }

    /**
     * @param geneids
     * @return
     */
    private ArrayList getMaxTF(String[] geneids, double pvalcut) {

        ArrayList ret = null;

        ArrayList samps = new ArrayList();
        int notfound = 0;
        HashMap addedids = new HashMap();
        for (int g = 0; g < geneids.length; g++) {
            //search for direct match of Y name
            int index = MoreArray.getArrayIndUniqueIgnoreCase(TFREF_data.xlabels, geneids[g]);
            if (index == -1) {
                String s = (String) yeast_gene_names.get(geneids[g]);
                if (s != null) {
                    index = MoreArray.getArrayIndUniqueIgnoreCase(TFREF_data.xlabels, s);
                    if (index != -1) {
                        Object o = addedids.get(s);
                        if (o == null) {
                            addedids.put(s, 1);
                        } else {
                            addedids.put(s, ((Integer) o) + 1);
                        }
                        //System.out.println("getMaxTF using yeast_gene_names index " + index + "\ts " + s + "\tgeneids " + geneids[g]);
                    }
                }
            } else {
                Object o = addedids.get(geneids[g]);
                if (o == null) {
                    addedids.put(geneids[g], 1);
                } else {
                    addedids.put(geneids[g], ((Integer) o) + 1);
                }
                //System.out.println("getMaxTF using yeast id index " + index + "\tgeneids " + geneids[g]);
            }
            if (index == -1) {
                notfound++;
            }
            if (index != -1) {
                //System.out.println("getMaxTF " + g + "\t" + geneids[g] + "\t" + index);
                // + "\t" + MoreArray.toString(motif_data.xlabels, ",")
                double[] data = Matrix.extractColumn(TFREF_data.data, index);
                samps.add(data);
            }
        }
        //System.out.println("TF not found " + notfound);

        Set addedk = addedids.keySet();
        Iterator iter = addedk.iterator();

        int countbad = 0;
        while (iter.hasNext()) {
            String key = (String) iter.next();
            int v = (Integer) addedids.get(key);
            if (v > 1) {
                System.out.println("getMaxTF WARNING " + key + " mapped gene has " + v + " instances!");
                countbad++;
            }
        }
        System.out.println("getMaxTF countbad " + countbad);


        if (samps.size() > 0) {
            ret = new ArrayList();
            double[] mean = stat.arraySampAvg(samps);

            double[][] sampsmat = MoreArray.convtodouble2DArray(samps, TFREF_data.data.length);
            int[] allsums = MoreArray.doubletoInt(Matrix.columnSum(sampsmat));

            int maxind = -1;
            double minpval = 1;
            //for each TF
            for (int m = 0; m < mean.length; m++) {

                //in block w/ TF
                int a = allsums[m];
                //in block not TF
                int b = samps.size() - a;
                //out block w/ TF
                int c = (int) stat.sumEntries(TFREF_data.data[m]) - a;
                //out block not TF
                int d = TFREF_data.data[0].length - (a + b) - c;

                String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                        "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TF\",\"noTF\")))";
                irv.Rexpr = irv.Rengine.eval(rcall1);

                try {
                    String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                    irv.Rexpr = irv.Rengine.eval(rcall2);
                    double pval = irv.Rexpr.asDouble();

                    if (pval < minpval) {
                        minpval = pval;
                        maxind = m;
                    }
                } catch (Exception e) {
                    if (TFREF_data.ylabels[m].indexOf("Dig2p") != -1) {
                        System.out.println("getMaxTF samps " + samps.size());
                        System.out.println("getMaxTF mean " + mean.length);
                        System.out.println("getMaxTF sampsmat " + sampsmat.length + "\t" + sampsmat[0].length);
                        System.out.println("getMaxTF allsums " + allsums.length);
                        System.out.println("getMaxTF allsums[m] " + allsums[m]);
                        System.out.println("getMaxTF Dig2p sampsmat " + MoreArray.toString(Matrix.extractColumn(sampsmat, m + 1), "\t"));
                        System.out.println("getMaxTF Dig2p allsums " + MoreArray.toString(allsums, "\t"));
                        System.out.println("getMaxTF Dig2p mean " + mean[m]);
                    }

                    System.out.println("getMaxTF genes in block " + (a + b));
                    System.out.println("getMaxTF genes w/ TF " + (a + c));
                    System.out.println("getMaxTF TF pval " + a + "\t" + b + "\t" + c + "\t" + d);
                    System.out.println("getMaxTF TF minpval " + minpval);
                    System.out.println("getMaxTF TF maxind " + maxind + "\t" + m);
                    System.out.println("getMaxTF unadjusted TF pval < cutoff " + m + "\t" + TFREF_data.ylabels[m]);
                    System.out.println("getMaxTF Error: " + rcall1);
                    e.printStackTrace();
                }

                /*if (pval < TF_pvalue_cutoff) {
                    System.out.println("genes in block " + (a + b));
                    System.out.println("genes w/ TF " + (a + c));
                    System.out.println("TF pval " + a + "\t" + b + "\t" + c + "\t" + d);
                    System.out.println("TF pval " + pval);
                    System.out.println("unadjusted TF pval < cutoff " + m + "\t" + motif_data.ylabels[m]);
                }*/
            }

            ret.add(TFREF_data.ylabels[maxind]);
            ret.add(minpval);
            return ret;
        }
        return null;
    }

    /**
     * Create COG role network based on number of gene pairs sharing a COG role between a pair of biclusters.
     */
    private void doCOG() {
        HashMap results = new HashMap();
        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            String[] COGfun = new String[v.genes.length];
            for (int g = 0; g < geneids.length; g++) {
                try {
                    //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                    geneids[g] = datageneids[v.genes[g] - 1];

                    for (int j = 0; j < tab_data.length; j++) {
                        if (debug)
                            System.out.println("doCOG test " + tab_data[j][7] + "\t" + tab_data[j][11]);
                        if (tab_data[j][7].equals(geneids[g])) {
                            if (tab_data[j][11] != null && !tab_data[j][11].equals("")) {
                                if (debug)
                                    System.out.println("doCOG " + tab_data[j][7] + "\t" + tab_data[j][11]);
                                COGfun[g] = tab_data[j][11];
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("datageneids " + datageneids.length + "\t" + v.genes[g]);
                    e.printStackTrace();
                }
            }

            for (int g = 0; g < geneids.length; g++) {
                if (COGfun[g] != null)
                    for (int h = g + 1; h < geneids.length; h++) {
                        if (COGfun[h] != null) {
                            String key = "" + COGfun[g] + "_" + COGfun[h];
                            Object o = results.get(key);
                            if (o == null) {
                                results.put(key, 1);
                            } else {
                                int val = (Integer) o;
                                results.put(key, val + 1);
                            }
                        }
                    }
            }
        }

        Set s = results.entrySet();
        Iterator it = s.iterator();

        ArrayList outar = new ArrayList();
        ArrayList keyar = new ArrayList();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String COG_COG_pair = (String) cur.getKey();
            int val = (Integer) cur.getValue();
            int ind = COG_COG_pair.indexOf("_");
            String a = COG_COG_pair.substring(0, ind);
            String b = COG_COG_pair.substring(ind + 1);
            outar.add(a + "\t" + b + "\t" + 1 + "\t" + val);

            if (keyar.indexOf(a) == -1)
                keyar.add(a);
            if (keyar.indexOf(b) == -1)
                keyar.add(b);
        }
        TextFile.write(outar, outdir + prefix + "COG_graph.txt");


        ArrayList outdot = new ArrayList();
        outdot.add("digraph COGFunctionalRoleGraph {");

        String add = "node [shape=ellipse fontsize=20 fontname=\"Helvetica\"];";
        for (int i = 0; i < keyar.size(); i++) {
            String k = (String) keyar.get(i);
            add += " " + k;
        }
        add += ";";
        outdot.add(add);
        outdot.add("");

        for (int i = 0; i < keyar.size(); i++) {
            String k = (String) keyar.get(i);
            String addlabel = "\"" + k + "\"" + "[label=\"" + k + "\"];";
            outdot.add(addlabel);
        }
        outdot.add("");

        it = s.iterator();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String COG_COG_pair = (String) cur.getKey();

            int val = (Integer) cur.getValue();
            int ind = COG_COG_pair.indexOf("_");
            String a = COG_COG_pair.substring(0, ind);
            String b = COG_COG_pair.substring(ind + 1);
            outdot.add("\"" + a + "\" -> \"" + b + "\"  [weight=" + val + "];");
        }
        outdot.add("}");

        String outpath = outdir + prefix + "_COG_graph.dot";
        System.out.println("writing " + outpath);
        TextFile.write(outdot, outpath);
    }

    /**
     */
    /* private void doTFREF() {
int size = BIC.size();

ArrayList resultsAr = assignTF(BIC, true, true);//, true);
String[] TFlabel = (String[]) resultsAr.get(0);
double[] TFlabelfreq = (double[]) resultsAr.get(1);

HashMap weights = new HashMap();
for (int i = 0; i < size; i++) {
for (int j = i + 1; j < size; j++) {
    *//*double cor = stat.correlationCoeff((double[]) profiles.get(i), (double[]) profiles.get(j));
                if (cor > 0.3)*//*
                if (TFlabel[i] == TFlabel[j])
                    weights.put((i + 1) + "_" + (j + 1), (TFlabelfreq[i] + TFlabelfreq[j]) / 2);//cor);
            }
        }

        ArrayList outdot = new ArrayList();
        outdot.add("digraph TFGraph {");

        String add = "node [shape=ellipse fontsize=20 fontname=\"Helvetica\"];";
        for (int i = 0; i < size; i++) {
            add += " " + (i + 1);
        }
        add += ";";
        outdot.add(add);
        outdot.add("");

        for (int i = 0; i < size; i++) {
            String addlabel = "\"" + (i + 1) + "\"" + "[label=\"" + TFlabel[i] + "\"];";
            outdot.add(addlabel);
        }
        outdot.add("");

        Set s = weights.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String pair = (String) cur.getKey();

            double val = (Double) cur.getValue();
            int ind = pair.indexOf("_");
            String a = pair.substring(0, ind);
            String b = pair.substring(ind + 1);
            outdot.add("\"" + a + "\" -> \"" + b + "\"  [weight=" + val + "];");
        }
        outdot.add("}");

        String outpath = prefix + "_TF_graph.dot";
        System.out.println("writing " + outpath);
        TextFile.write(outdot, outpath);
    }*/

    /**
     * @param docount
     * @return
     */
    private ArrayList assignTFEXP(boolean docount) {
        String[] TFlabel = new String[BIC.size()];
        double[] TFlabelfreq = new double[BIC.size()];
        ArrayList profiles = new ArrayList();
        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            ArrayList data = new ArrayList();
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];
                int index = MoreArray.getArrayIndUnique(TFEXP_data.ylabels, geneids[g]);
                if (index != -1) {
                    data.add(TFEXP_data.data[index]);
                }
            }
            if (data.size() > 0) {
                double[] mean = stat.arraySampAvg(data);
                profiles.add(mean);
                double max = stat.findMax(mean);
                TFlabelfreq[i] = max;
                int maxindex = stat.findMaxIndex(mean);
                TFlabel[i] = TFEXP_data.xlabels[maxindex];
            }
        }

        ArrayList ret = new ArrayList();
        ret.add(TFlabel);
        ret.add(TFlabelfreq);
        return ret;
    }

    /**
     * @param inblocks
     * @return
     */
    private ArrayList assignTF(ValueBlockList inblocks, boolean doCount, boolean add) {

        System.out.println("assignTF");

        int size = inblocks.size();

        String[] TFlabel = new String[size];
        String[] TFlabelpvallist = new String[size];
        double[] TFlabelpval = MoreArray.initArrayD(size, Double.NaN);
        ArrayList profiles = new ArrayList();

        int notfoundtotal = 0;
        int notfound = 0;
        double maxprop = 0;
        int badprop = 0;

        ArrayList TFlabelar = new ArrayList();
        ArrayList TFfreqar = new ArrayList();
        ArrayList TFnodelabelar = new ArrayList();

        for (int i = 0; i < size; i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) inblocks.get(i);
            String[] geneids = new String[v.genes.length];
            ArrayList samps = new ArrayList();

            int count_mapped_in_block = 0;
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];

                int index = MoreArray.getArrayIndUniqueIgnoreCase(TFREF_data.xlabels, geneids[g]);
                if (index == -1) {
                    Object o = (Object) yeast_gene_names.get(geneids[g]);
                    if (o != null) {
                        String s = (String) o;
                        //System.out.println("assignTF :" + s + ":\t" + geneids[g]);
                        if (s != null) {
                            index = MoreArray.getArrayIndUniqueIgnoreCase(TFREF_data.xlabels, s);
                        } else
                            System.out.println("assignTF s is null :" + s + ":\t" + geneids[g]);
                    }
                }
                if (index == -1) {
                    System.out.println("did not find " + geneids[g]);
                    notfound++;
                }
                if (index != -1) {
                    double[] data = Matrix.extractColumn(TFREF_data.data, index);
                    samps.add(data);
                    count_mapped_in_block++;
                }
            }

            if (samps.size() > 0) {

                double[] mean = stat.arraySampAvg(samps);
                profiles.add(mean);

                //System.out.println("TF length " + mean.length + "\t" + motif_data.data.length);
                //for each TF
                for (int k = 0; k < mean.length; k++) {

                    double[][] sampsmat = MoreArray.convtodouble2DArray(samps, TFREF_data.data.length);
                    int[] allsums = MoreArray.doubletoInt(Matrix.columnSum(sampsmat));
                    int totalk = countTotalTF(k);
                    if (totalk > 0) {
                        //in block w/ TF
                        int a = allsums[k];
                        //in block not TF
                        int b = count_mapped_in_block - a;
                        //out block w/ TF
                        int c = totalk - a;
                        //out block not TF
                        int d = data_to_TFmap_size - (a + b) - c;

                        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TF\",\"noTF\")))";
                        irv.Rexpr = irv.Rengine.eval(rcall1);

                        String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                        irv.Rexpr = irv.Rengine.eval(rcall2);
                        double pval = 0;
                        try {
                            pval = irv.Rexpr.asDouble();
                        } catch (Exception e) {
                            System.out.println("totalk " + totalk + "\tdata_to_TFmap_size " + data_to_TFmap_size);
                            System.out.println("genes in block " + (a + b));
                            System.out.println("genes w/ TF " + (a + c));
                            System.out.println("TF pval " + a + "\t" + b + "\t" + c + "\t" + d);
                            System.out.println("TF pval " + pval);
                            System.out.println("unadjusted TF pval < cutoff " + k + "\t" + TFREF_data.ylabels[k]);

                            e.printStackTrace();
                        }

                        if (pval < TF_pvalue_cutoff) {
                            System.out.println("genes in block " + (a + b));
                            System.out.println("genes w/ TF " + (a + c));
                            System.out.println("F pvalT " + a + "\t" + b + "\t" + c + "\t" + d);
                            System.out.println("TF pval " + pval);
                            System.out.println("unadjusted TF pval < cutoff " + k + "\t" + TFREF_data.ylabels[k]);
                        }

                        TFlabelar.add(TFREF_data.ylabels[k]);
                        TFfreqar.add(pval);
                        TFnodelabelar.add(i);
                    }
                }
            }

            notfoundtotal += notfound;
            double prop = (double) notfound / (double) v.genes.length;
            if (prop > maxprop)
                maxprop = prop;

            if (prop > 0.25)
                badprop++;
            if (notfound > 0)
                System.out.println("notfound " + i + "\t" + notfound + " out of " + v.genes.length +
                        "\t% " + prop + "\t" + (prop > 0.25 ? "MANY" : ""));
            notfound = 0;
        }

        System.out.println("final notfoundtotal " + notfoundtotal + "\tmax " + maxprop + "\tbad > 0.25 missing " +
                badprop + "\t" + TFfreqar.size());

        //FDR correction of Fisher exact p-values
        if (TFfreqar.size() > 0) {
            double[] unadjustallpvals = MoreArray.ArrayListtoDouble(TFfreqar);

            double[] allpvals = new double[0];
            /*TODO replace back with 4M limit or so */
            if (unadjustallpvals.length < 2000000) {
                //System.out.println("unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
                boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
                //irv.Rengine.eval("data <- c(" + MoreArray.toString(TFlabelpval, ",") + ")");
                System.out.println(irv.Rengine.eval("gc()"));
                //System.out.println(irv.Rengine.eval("memory.limit()"));
                String rcall2 = "p.adjust(data,\"fdr\")";
                irv.Rexpr = irv.Rengine.eval(rcall2);

                try {
                    allpvals = irv.Rexpr.asDoubleArray();
                } catch (Exception e) {
                    e.printStackTrace();

                    System.out.println("WARNING: R out of memory, attempting shell script");

                    allpvals = adjustWithScript(unadjustallpvals);
                }
            } else {
                System.out.println("long array, attempting shell script");

                allpvals = adjustWithScript(unadjustallpvals);
            }


            boolean[] trackEnrich = new boolean[size];
            //concatenate labels for all significant cases and find top p-value
            for (int i = 0; i < allpvals.length; ) {
                int curind = (Integer) TFnodelabelar.get(i);
                double curval = allpvals[i];//(Double) TFfreqar.get(i);
                double curunval = unadjustallpvals[i];//(Double) TFfreqar.get(i);
                String curlab = (String) TFlabelar.get(i);
                int curpos = curind;

                ArrayList curindlist = new ArrayList();
                ArrayList curvallist = new ArrayList();
                ArrayList curunvallist = new ArrayList();
                ArrayList curlablist = new ArrayList();

                System.out.println("assignTF bf loop " + i);
                //populate for this block
                while (curind == curpos && i < allpvals.length) {
                    curindlist.add(curind);
                    curvallist.add(curval);
                    curunvallist.add(curunval);
                    curlablist.add(curlab);
                    i++;
                    if (i < allpvals.length) {
                        curind = (Integer) TFnodelabelar.get(i);
                        curval = allpvals[i];//(Double) TFfreqar.get(i);
                        curunval = unadjustallpvals[i];//(Double) TFfreqar.get(i);
                        curlab = (String) TFlabelar.get(i);
                    }
                }
                System.out.println("assignTF af loop " + i);

                double[] sorted = MoreArray.ArrayListtoDouble(curvallist);
                double[] unsorted = MoreArray.copy(sorted);
                Arrays.sort(sorted);

                System.out.println("af sort TF " + sorted[0] + "\t" + sorted[sorted.length - 1]);
                System.out.println("sort TF " + MoreArray.toString(sorted, ","));
                //System.out.println("assignTF unsorted " + MoreArray.toString(unsorted, ","));

                String upvallist = "";
                //for all current labels
                for (int m = 0; m < sorted.length; m++) {
                    double p = sorted[m];
                    //index in unsorted to retrieve label
                    //for all significant cases
                    if (p < TF_pvalue_cutoff || do_all_pvals) {
                        int index = MoreArray.getArrayInd(unsorted, p);

                        String s = (String) curlablist.get(index);
                        double up = (Double) curunvallist.get(index);
                        int indexinref = (Integer) curindlist.get(index);
                        System.out.println("assignTF test " + p + "\t" + TF_pvalue_cutoff + "\t" + index + "\t" +
                                indexinref + "\t" + m + "\t" + s);

                        if (!trackEnrich[indexinref] && doCount) {
                            TFcount++;
                            trackEnrich[indexinref] = true;
                        }

                        if (Double.isNaN(TFlabelpval[indexinref])) {
                            TFlabelpval[indexinref] = p;
                        } else if (p < TFlabelpval[indexinref]) {
                            TFlabelpval[indexinref] = p;
                        }

                        if (TFlabel[indexinref] == null) {
                            TFlabel[indexinref] = s;
                            TFlabelpvallist[indexinref] = "" + p;
                            upvallist = "" + up;

                            if (add) {
                                Object o = foundTF.get(s);
                                if (o == null) {
                                    foundTF.put(s, 1);
                                } else {
                                    int iv = (Integer) o;
                                    foundTF.put(s, iv + 1);
                                }
                            }
                        } else {

                            if (!TFlabel[indexinref].equals("TF: " + s) && TFlabel[indexinref].indexOf("_" + s + "_") == -1 &&
                                    !TFlabel[indexinref].startsWith("TF: " + s + "_") &&
                                    !TFlabel[indexinref].endsWith("_" + s)) {
                                TFlabel[indexinref] += "_" + s;
                                TFlabelpvallist[indexinref] += "__" + p;
                                upvallist += "__" + up;
                            }

                            if (add) {
                                Object o = foundTF.get(s);
                                //only add unique
                                if (o == null) {
                                    foundTF.put(s, 1);
                                } else {
                                    int iv = (Integer) o;
                                    foundTF.put(s, iv + 1);
                                }
                            }
                        }
                        System.out.println("assignTF bicluster TF summary " + i + "\t" + indexinref + "\t" + m + "\t" +
                                TFlabel[indexinref] + "\t" +
                                TFlabelpval[indexinref] + "\t" + TFlabelpvallist[indexinref] + "\t" + upvallist);
                    }
                }

            }//end for loop
        }

        //coverage
        Set TFf = foundTF.keySet();
        TFcover = (double) TFf.size() / (double) TFREF_data.data.length;
        System.out.println("assignTF TFcover biclusters " + TFcount + "\tunique TFs " + TFf.size() + "\t" + TFcover + "\t" + (double) TFREF_data.data.length);

        //clean up
        for (int i = 0; i < TFlabel.length; i++) {
            if (Double.isNaN(TFlabelpval[i])) {
                TFlabelpval[i] = 1;
            } /*else {
                TFlabelpval[i] = 1 - TFlabelpval[i];
            }*/
            if (TFlabel[i] == null) {
                TFlabel[i] = "none";
            }

            System.out.println("assignTF " + i + "\t" + TFlabel[i] + "\t" + TFlabelpval[i]);
        }

        //flip p-value for color scale
        //TFlabelpval = stat.subtract(1, TFlabelpval);
        ArrayList ret = new ArrayList();
        ret.add(TFlabel);
        ret.add(TFlabelpval);
        ret.add(TFlabelpvallist);
        return ret;
    }


    /**
     * @return
     */
    public int countTotalTF(int k) {

        int ret = (int) mathy.Matrix.rowSum(TFREF_data.data, k);

        return ret;
    }

    /**
     * @param unadjustallpvals
     * @return
     */
    private double[] adjustWithScript(double[] unadjustallpvals) {
        String bicfile = (String) options.get("-bic");

        String infile = bicfile + "_" + "TFrawpvals.txt";
        TabFile.write(MoreArray.toStringArray(unadjustallpvals), infile);
        System.out.println("wrote unadjusted " + infile);

        String Rprep = "data <- read.csv(\"" + infile + "\",header=F)\n";
        Rprep += "TFdata <- p.adjust(unlist(data),\"fdr\")\n";
        String outfile = bicfile + "_" + "TFadjustallpvals.txt";

        Rprep += "write.table(TFdata,file=\"" + outfile + "\",row.names=F,col.names=F)\n";

        String Rprep_script_file = bicfile + "_" + "TFadjust.R";
        System.out.println("WRITING " + Rprep_script_file);
        TextFile.write(Rprep, Rprep_script_file);
        System.out.println("wrote script " + Rprep_script_file);

        String Rout = bicfile + "_" + "TFadjust_out.txt";
        String shell = "R --vanilla < " + Rprep_script_file + " &> " + Rout;

        System.out.println("running");
        runCmd(shell, "./", "shell.sh");
        File test = new File(Rout);

        if (test.exists()) {
            System.out.println("produced " + Rout);
            System.out.println("produced " + outfile);
        } else
            System.out.println("failed: R: R --vanilla < " + Rprep_script_file + "&>" + Rout);


        final String[][] sarray = TabFile.readtoArray(outfile, false);
        String[] g = MoreArray.extractColumnStr(sarray, 1);
        double[] allpvals = MoreArray.convfromString(g);

        System.out.println("loaded adjusted " + allpvals.length);

        return allpvals;
    }


    /**
     * @param inblocks
     * @return
     */
    private ArrayList assignPairTF(ValueBlockList inblocks, boolean doCount, boolean add) {
        int size = inblocks.size();

        String[] TFlabel = new String[size];
        String[] TFlabelpvallist = new String[size];
        double[] TFlabelpval = MoreArray.initArrayD(size, Double.NaN);
        ArrayList profiles = new ArrayList();

        int notfoundtotal = 0;
        int notfound = 0;
        double maxprop = 0;
        int badprop = 0;

        ArrayList TFlabelar = new ArrayList();
        ArrayList TFfreqar = new ArrayList();
        ArrayList TFnodelabelar = new ArrayList();

        for (int i = 0; i < size; i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) inblocks.get(i);
            String[] geneids = new String[v.genes.length];
            ArrayList samps = new ArrayList();
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];

                int index = MoreArray.getArrayIndUniqueIgnoreCase(TFREF_data_pair.xlabels, geneids[g]);
                if (index == -1) {
                    String s = (String) yeast_gene_names.get(geneids[g]);
                    if (s != null) {
                        index = MoreArray.getArrayIndUniqueIgnoreCase(TFREF_data_pair.xlabels, s);
                    }
                }
                if (index == -1) {
                    if (debug)
                        System.out.println("assignPairTF did not find " + geneids[g]);
                    notfound++;
                }
                if (index != -1) {
                    try {
                        double[] data = Matrix.extractColumn(TFREF_data_pair.data, index);
                        samps.add(data);
                    } catch (Exception e) {
                        System.out.println("assignPairTF index " + index + "\tcols " + TFREF_data_pair.data[0].length + "\trows " + TFREF_data_pair.data.length);
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            }

            if (samps.size() > 0) {

                double[] mean = stat.arraySampAvg(samps);
                profiles.add(mean);

                //System.out.println("TF length " + mean.length + "\t" + TFREF_data_pair.data.length);
                //for each TF
                for (int k = 0; k < mean.length; k++) {
                    System.out.print("*");
                    double[][] sampsmat = MoreArray.convtodouble2DArray(samps, TFREF_data_pair.data.length);
                    int[] allsums = MoreArray.doubletoInt(Matrix.columnSum(sampsmat));
                    //in block w/ TF
                    int a = allsums[k];
                    //in block not TF
                    int b = samps.size() - a;
                    //out block w/ TF
                    int c = (int) stat.sumEntries(TFREF_data_pair.data[k]) - a;
                    //out block not TF
                    int d = TFREF_data_pair.data[0].length - (a + b) - c;

                    String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                            "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TF\",\"noTF\")))";
                    irv.Rexpr = irv.Rengine.eval(rcall1);

                    String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                    irv.Rexpr = irv.Rengine.eval(rcall2);
                    double pval = irv.Rexpr.asDouble();

                    if (debug && pval < pairTF_pvalue_cutoff) {
                        System.out.println("\nassignPairTF genes in block " + (a + b));
                        System.out.println("assignPairTF genes w/ TF " + (a + c));
                        System.out.println("assignPairTF F pval " + a + "\t" + b + "\t" + c + "\t" + d);
                        System.out.println("assignPairTF TF pval " + pval);
                        System.out.println("assignPairTF unadjusted TF pval < cutoff i\t" + i + "\tsamps ratio\t" +
                                ((double) samps.size() / (double) v.genes.length) + "\tnotfound\t" + notfound +
                                "\tk\t" + k + "\ta\t" + a +
                                "\tmotifpair\t" + TFREF_data_pair.ylabels[k] + "\tpval\t" + pval);
                    }

                    TFlabelar.add(TFREF_data_pair.ylabels[k]);
                    TFfreqar.add(pval);
                    TFnodelabelar.add(i);
                }
                System.out.print("\n");
            }

            notfoundtotal += notfound;
            double prop = (double) notfound / (double) v.genes.length;
            if (prop > maxprop)
                maxprop = prop;

            if (prop > 0.25)
                badprop++;
            if (notfound > 0)
                System.out.println("assignPairTF notfound " + i + "\t" + notfound + " out of " + v.genes.length +
                        "\t% " + prop + "\t" + (prop > 0.25 ? "MANY" : ""));
            notfound = 0;
        }

        System.out.println("assignPairTF final notfoundtotal " + notfoundtotal + "\tmax " + maxprop + "\tbad > 0.25 missing " +
                badprop + "\t" + TFfreqar.size());

        //FDR correction of Fisher exact p-values
        if (TFfreqar.size() > 0) {
            double[] unadjustallpvals = MoreArray.ArrayListtoDouble(TFfreqar);
            //System.out.println("assignPairTF unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
            boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
            //irv.Rengine.eval("data <- c(" + MoreArray.toString(TFlabelpval, ",") + ")");
            String rcall2 = "p.adjust(data,\"fdr\")";
            irv.Rexpr = irv.Rengine.eval(rcall2);
            double[] allpvals = irv.Rexpr.asDoubleArray();


            boolean[] trackEnrich = new boolean[size];
            //concatenate labels for all significant cases and find top p-value
            for (int i = 0; i < allpvals.length; ) {
                int curind = (Integer) TFnodelabelar.get(i);
                double curval = allpvals[i];//(Double) TFfreqar.get(i);
                double curunval = unadjustallpvals[i];//(Double) TFfreqar.get(i);
                String curlab = (String) TFlabelar.get(i);
                int curpos = curind;

                ArrayList curindlist = new ArrayList();
                ArrayList curvallist = new ArrayList();
                ArrayList curunvallist = new ArrayList();
                ArrayList curlablist = new ArrayList();

                System.out.println("assignPairTF bf loop " + i);
                //populate for this block
                while (curind == curpos && i < allpvals.length) {
                    curindlist.add(curind);
                    curvallist.add(curval);
                    curunvallist.add(curunval);
                    curlablist.add(curlab);
                    i++;
                    if (i < allpvals.length) {
                        curind = (Integer) TFnodelabelar.get(i);
                        curval = allpvals[i];//(Double) TFfreqar.get(i);
                        curunval = unadjustallpvals[i];//(Double) TFfreqar.get(i);
                        curlab = (String) TFlabelar.get(i);
                    }
                }
                System.out.println("assignPairTF af loop " + i);

                double[] sorted = MoreArray.ArrayListtoDouble(curvallist);
                double[] unsorted = MoreArray.copy(sorted);
                Arrays.sort(sorted);

                //System.out.println("assignPairTF af sort " + sorted[0] + "\t" + sorted[sorted.length - 1]);
                //System.out.println("assignPairTF sort " + MoreArray.toString(sorted, ","));
                //System.out.println("assignTF unsorted " + MoreArray.toString(unsorted, ","));

                String upvallist = "";
                //for all current labels
                for (int m = 0; m < sorted.length; m++) {
                    double p = sorted[m];
                    int index = MoreArray.getArrayInd(unsorted, p);
                    int indexinref = (Integer) curindlist.get(index);
                    //index in unsorted to retrieve label
                    //for all significant cases
                    if (p < pairTF_pvalue_cutoff || do_all_pvals) {

                        String s = (String) curlablist.get(index);
                        double up = (Double) curunvallist.get(index);

                        System.out.println("assignPairTF test " + p + "\t" + TF_pvalue_cutoff + "\t" + index + "\t" +
                                indexinref + "\t" + m + "\t" + s);

                        if (!trackEnrich[indexinref] && doCount) {
                            TFcount++;
                            trackEnrich[indexinref] = true;
                        }

                        if (Double.isNaN(TFlabelpval[indexinref])) {
                            TFlabelpval[indexinref] = p;
                        } else if (p < TFlabelpval[indexinref]) {
                            TFlabelpval[indexinref] = p;
                        }

                        if (TFlabel[indexinref] == null) {
                            TFlabel[indexinref] = s;
                            TFlabelpvallist[indexinref] = "" + p;
                            upvallist = "" + up;

                            if (add) {
                                Object o = foundPairTF.get(s);
                                if (o == null) {
                                    foundPairTF.put(s, 1);
                                } else {
                                    int iv = (Integer) o;
                                    foundPairTF.put(s, iv + 1);
                                }
                            }
                        } else {

                            if (!TFlabel[indexinref].equals("TF: " + s) && TFlabel[indexinref].indexOf("_" + s + "_") == -1 &&
                                    !TFlabel[indexinref].startsWith("TF: " + s + "_") &&
                                    !TFlabel[indexinref].endsWith("_" + s)) {
                                TFlabel[indexinref] += "_" + s;
                                TFlabelpvallist[indexinref] += "__" + p;
                                upvallist += "__" + up;
                            }

                            if (add) {
                                Object o = foundPairTF.get(s);
                                //only add unique
                                if (o == null) {
                                    foundPairTF.put(s, 1);
                                } else {
                                    int iv = (Integer) o;
                                    foundPairTF.put(s, iv + 1);
                                }
                            }
                        }
                        System.out.println("assignPairTF bicluster TF summary " + i + "\t" + indexinref + "\t" + m + "\t" +
                                TFlabel[indexinref] + "\t" +
                                TFlabelpval[indexinref] + "\t" + TFlabelpvallist[indexinref] + "\t" + upvallist);
                    /*} else if (unadjustallpvals[indexinref] < pairTF_pvalue_cutoff) {
                        System.out.println("assignPairTF test failed FDR but unadjusted OK " + index + "\t" + indexinref + "\t" +
                                unadjustallpvals[indexinref] + "\t" + p + "\t" + pairTF_pvalue_cutoff + "\t" +
                                m);
                    }*/
                    }
                }

            }//end for loop
        }

        //coverage
        Set TFf = foundPairTF.keySet();
        pairTFcover = (double) TFf.size() / (double) TFREF_data_pair.data.length;
        System.out.println("assignPairTF TFcover biclusters " + TFcount + "\tunique TFs " + TFf.size() + "\t" + pairTFcover + "\t" + (double) TFREF_data_pair.data.length);

        //clean up and switch p-values for color scale
        for (int i = 0; i < TFlabel.length; i++) {
            if (Double.isNaN(TFlabelpval[i])) {
                TFlabelpval[i] = 1;
            } /*else {
                TFlabelpval[i] = 1 - TFlabelpval[i];
            }*/
            if (TFlabel[i] == null) {
                TFlabel[i] = "none";
            }

            System.out.println("assignPairTF " + i + "\t" + TFlabel[i] + "\t" + TFlabelpval[i]);
        }

        //flip p-value for color scale
        //TFlabelpval = stat.subtract(1, TFlabelpval);
        ArrayList ret = new ArrayList();
        ret.add(TFlabel);
        ret.add(TFlabelpval);
        ret.add(TFlabelpvallist);
        return ret;
    }

    /**
     * @param doCount
     * @return
     */
    private ArrayList assignGO(boolean doCount) {
        System.out.println("assignGO");
        String[] GOlabel = new String[BIC.size()];
        double[] GOlabelpval = MoreArray.initArrayD(BIC.size(), Double.NaN);
        String[] GOlabelpvallist = new String[BIC.size()];

        ArrayList GOlabelar = new ArrayList();
        ArrayList GOfreqar = new ArrayList();
        ArrayList GOnodelabelar = new ArrayList();

        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            HashMap curmap = new HashMap();
            int countb = 0;
            int count_mapped_in_block = 0;
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];
                ArrayList GOar = (ArrayList) GOmap.get(geneids[g]);
                if (GOar != null) {
                    count_mapped_in_block++;
                    System.out.println("assignGO " + i + "\t" + g + "\t" + geneids[g] + "\t" + MoreArray.arrayListtoString(GOar, ","));

                    HashMap<String, Integer> unique = new HashMap<String, Integer>();
                    for (int h = 0; h < GOar.size(); h++) {
                        String[] GO = new String[1];
                        try {
                            GO = (String[]) GOar.get(h);
                            if (!GO[4].equals("")) {
                                try {
                                    Object test = curmap.get(GO[4]);
                                    if (test == null) {
                                        Object added = unique.get(GO[4]);
                                        if (added == null) {
                                            curmap.put(GO[4], 1.0);
                                            countb++;
                                            unique.put(GO[4], 1);

                                            //if (GO[4] == "plasma")
                                            //    System.out.println("GO[4] FOUND ROGUE plasma");
                                        }
                                    } else {
                                        Object added = unique.get(GO[4]);
                                        if (added == null) {
                                            double val = (Double) test;
                                            curmap.put(GO[4], (val + 1.0));
                                            countb++;
                                            unique.put(GO[4], 1);

                                            //if (GO[4] == "plasma")
                                            //    System.out.println("GO[4] FOUND ROGUE plasma");
                                        }
                                    }
                                } catch (Exception e) {
                                    //MoreArray.printArray(GO);
                                    //e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            try {
                                GO = new String[1];
                                GO[0] = (String) GOar.get(h);
                                // String GOtxt = (String) GOrefMAP.get(GO[0]);
                                //if (GOtxt != null) {
                                Object test = curmap.get(GO[0]);
                                if (test == null) {
                                    Object added = unique.get(GO[0]);
                                    if (added == null) {
                                        curmap.put(GO[0], 1.0);
                                        countb++;
                                        unique.put(GO[0], 1);

                                        //if (GO[0] == "plasma")
                                        //    System.out.println("GO[0] FOUND ROGUE plasma");

                                    }
                                } else {
                                    Object added = unique.get(GO[0]);
                                    if (added == null) {
                                        double val = (Double) test;
                                        curmap.put(GO[0], (val + 1.0));
                                        countb++;
                                        unique.put(GO[0], 1);

                                        //if (GO[0] == "plasma")
                                        //    System.out.println("GO[0] FOUND ROGUE plasma");
                                    }
                                }
                            } catch (Exception e1) {
                                System.out.println("h " + h);
                                System.out.println("GOar " + GOar.size());
                                System.out.println("GO " + GO.length);
                                System.out.println("GO[0] " + GO[0]);
                                e1.printStackTrace();
                            }
                        }
                        //}
                    }
                }
            }
            System.out.println("assignGO curmap.size() " + curmap.size());

            if (curmap.size() > 0) {
                Set s = curmap.entrySet();
                Iterator it = s.iterator();

                //ArrayList get = getGOcounts(it);
                //System.out.println("GO length " + mean.length + "\t" + GOREF_data.data.length);

                int countthis = 0;
                while (it.hasNext()) {
                    Map.Entry cur = (Map.Entry) it.next();
                    String st = (String) cur.getKey();
                    if (debug)
                        System.out.println("st " + st);
                    double val = (Double) cur.getValue();

                    int allGO = countGO(st);
                    if (val != 0 && allGO != 0) {
                        //in block w/ GO
                        int a = (int) val;
                        //in block not GO
                        //int b = geneids.length - (int) val;
                        int b = count_mapped_in_block - (int) val;
                        //out block w/ GO
                        int c = allGO - a;
                        //out block not GO
                        //int d = datageneids.length - (a + b) - c;
                        int d = data_to_GOmap_size - (a + b) - c;

                        System.out.println("genes in block " + (a + b));
                        System.out.println("genes w/ GO " + (a + c));

                        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"GO\",\"noGO\")))";
                        irv.Rexpr = irv.Rengine.eval(rcall1);

                        String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                        try {
                            irv.Rexpr = irv.Rengine.eval(rcall2);
                            double pval = irv.Rexpr.asDouble();
                            System.out.println("GO pval " + a + "\t" + b + "\t" + c + "\t" + d);
                            System.out.println("GO pval " + pval);

                            GOlabelar.add(st);
                            GOfreqar.add(pval);
                            GOnodelabelar.add(i);

                        } catch (Exception e) {
                            System.out.println("GO error " + a + "\t" + b + "\t" + c + "\t" + d + "\t" +
                                    geneids.length + "\t" + (int) val + "\t" + st);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (GOfreqar.size() > 0) {
            //FDR correction of Fisher exact p-values
            double[] unadjustallpvals = MoreArray.ArrayListtoDouble(GOfreqar);
            //System.out.println("unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
            boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
            //irv.Rengine.eval("data <- c(" + MoreArray.toString(GOlabelpval, ",") + ")");
            String rcall2 = "p.adjust(data,\"fdr\")";
            irv.Rexpr = irv.Rengine.eval(rcall2);
            double[] allpvals = irv.Rexpr.asDoubleArray();


            boolean[] trackEnrich = new boolean[BIC.size()];
            //concatenate labels for all significant cases and find top p-value
            for (int i = 0; i < allpvals.length; ) {
                int curind = (Integer) GOnodelabelar.get(i);
                double curval = allpvals[i];//(Double) GOfreqar.get(i);
                double curunval = unadjustallpvals[i];//(Double) GOfreqar.get(i);
                String curlab = (String) GOlabelar.get(i);
                int curpos = curind;

                ArrayList curindlist = new ArrayList();
                ArrayList curvallist = new ArrayList();
                ArrayList curunvallist = new ArrayList();
                ArrayList curlablist = new ArrayList();

                System.out.println("assignGO bf loop " + i);
                //populate for this block
                while (curind == curpos && i < allpvals.length) {
                    curindlist.add(curind);
                    curvallist.add(curval);
                    curunvallist.add(curunval);
                    if (debug)
                        System.out.println("curlab " + curlab);
                    curlablist.add(curlab);
                    i++;
                    if (i < allpvals.length) {
                        curind = (Integer) GOnodelabelar.get(i);
                        curval = allpvals[i];
                        curunval = unadjustallpvals[i];
                        curlab = (String) GOlabelar.get(i);
                    }
                }
                System.out.println("assignGO af loop " + i);

                double[] sorted = MoreArray.ArrayListtoDouble(curvallist);
                double[] unsorted = MoreArray.copy(sorted);
                Arrays.sort(sorted);

                System.out.println("af sort GO " + sorted[0] + "\t" + sorted[sorted.length - 1]);
                System.out.println("sort GO " + MoreArray.toString(sorted, ","));
                System.out.println("unsorted GO " + MoreArray.toString(unsorted, ","));

                String upvallist = "";
                //for all current labels
                for (int m = 0; m < sorted.length; m++) {
                    double p = sorted[m];
                    //index in unsorted to retrieve label
                    //for all significant cases
                    if (p < GO_pvalue_cutoff || do_all_pvals) {

                        int index = MoreArray.getArrayInd(unsorted, p);

                        String s = (String) curlablist.get(index);
                        double up = (Double) curunvallist.get(index);
                        //System.out.println("test assignGO " + p + "\t" + GO_pvalue_cutoff + "\t" + index + "\t" + m + "\t" + s);
                        int indexinref = (Integer) curindlist.get(index);

                        if (!trackEnrich[indexinref] && doCount) {
                            GOcount++;
                            trackEnrich[indexinref] = true;
                        }

                        if (Double.isNaN(GOlabelpval[indexinref])) {
                            GOlabelpval[indexinref] = p;
                        } else if (p < GOlabelpval[indexinref]) {
                            GOlabelpval[indexinref] = p;
                        }

                        if (GOlabel[indexinref] == null) {
                            GOlabel[indexinref] = "GO: " + s;
                            GOlabelpvallist[indexinref] = "" + p;
                            upvallist = "" + up;

                            Object o = foundGO.get(s);
                            if (o == null) {
                                foundGO.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundGO.put(s, iv + 1);
                            }
                        } else {
                            //only add if unique
                            if (!GOlabel[indexinref].equals("GO: " + s) && GOlabel[indexinref].indexOf("_" + s + "_") == -1 &&
                                    !GOlabel[indexinref].startsWith("GO: " + s + "_") &&
                                    !GOlabel[indexinref].endsWith("_" + s)) {
                                GOlabel[indexinref] += "_" + s;
                                GOlabelpvallist[indexinref] += "__" + p;
                                upvallist += "__" + up;
                            }

                            Object o = foundGO.get(s);
                            if (o == null) {
                                foundGO.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundGO.put(s, iv + 1);
                            }
                        }
                        System.out.println("bicluster GO summary_" + i + "\t" + m + "\t" + GOlabel[indexinref] + "\t" +
                                GOlabelpval[indexinref] + "\t" + GOlabelpvallist[indexinref] + "\t" + upvallist);
                    }
                }
            }//end for loop
        } else {
            System.out.println("GO GOfreqar has 0 entries");
        }

        //coverage
        Set GOf = foundGO.keySet();
        Iterator it = GOf.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            System.out.println("foundGO " + s);
        }
        System.out.println("foundGO " + GOf.size());

        Set GOa = totalGO.keySet();
        it = GOa.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            System.out.println("totalGO " + s);
        }

        GOcover = (double) GOf.size() / (double) GOa.size();
        System.out.println("GOcover biclusters " + GOcount + "\tunique GOs " + GOf.size() + "\t" + (double) GOa.size());

        //switch p-values for color scale
        for (int i = 0; i < GOlabel.length; i++) {
            if (Double.isNaN(GOlabelpval[i])) {
                GOlabelpval[i] = 1;
                GOlabel[i] = "none";
            } /*else {
                GOlabelpval[i] = 1 - GOlabelpval[i];
            }*/
        }

        //flip p-value for color scale
        //GOlabelpval = stat.subtract(1, GOlabelpval);
        ArrayList ret = new ArrayList();
        ret.add(GOlabel);
        ret.add(GOlabelpval);
        ret.add(GOlabelpvallist);
        return ret;
    }

    /**
     * @param doCount
     * @return
     */
    private ArrayList assignGMT(boolean doCount) {
        System.out.println("assignGMT");
        String[] GMTlabel = new String[BIC.size()];
        double[] GMTlabelfreq = MoreArray.initArrayD(BIC.size(), Double.NaN);

        ArrayList GMTlabelar = new ArrayList();
        ArrayList GMTfreqar = new ArrayList();
        ArrayList GMTnodelabelar = new ArrayList();

        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            HashMap curmap = new HashMap();
            int countb = 0;
            int count_mapped_in_block = 0;
            if (debug)
                System.out.println("assignGMT " + i + "\t" + geneids.length);
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];
                ArrayList GMTar = (ArrayList) GMTmap.get(geneids[g]);
                if (debug)
                    System.out.println("assignGMT " + v.genes[g] + "\t" + geneids[g]);
                if (GMTar != null) {
                    count_mapped_in_block++;

                    for (int h = 0; h < GMTar.size(); h++) {
                        String GMT = (String) GMTar.get(h);
                        //thisgoid.add(row[5]);
                        //thisgolab.add(row[4]);
                        if (GMT != null && !GMT.equals("")) {
                            try {
                                Object test = curmap.get(GMT);
                                if (test == null) {
                                    curmap.put(GMT, 1.0);
                                    if (debug)
                                        System.out.println("assignGMT " + GMT + "\t" + 1.0);
                                    countb++;
                                } else {
                                    double val = (Double) test;
                                    curmap.put(GMT, (val + 1.0));
                                    if (debug)
                                        System.out.println("assignGMT " + GMT + "\t" + (val + 1));
                                    countb++;
                                }
                            } catch (Exception e) {
                                //MoreArray.printArray(TIGRrole);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (debug)
                System.out.println("assignGMT curmap.size() " + curmap.size());

            if (curmap.size() > 0) {
                Set s = curmap.entrySet();
                Iterator it = s.iterator();

                //ArrayList get = getTIGRrolecounts(it);
                //System.out.println("TIGRrole length " + mean.length + "\t" + TIGRroleREF_data.data.length);

                //if (get != null && get.size() > 0) {
                /* double maxval = (Double) get.get(0);
            String max = (String) get.get(1);
            int totalassign = (Integer) get.get(2);*/

                while (it.hasNext()) {
                    Map.Entry cur = (Map.Entry) it.next();
                    String st = (String) cur.getKey();
                    double val = (Double) cur.getValue();

                    int allGMT = countGMT(st);
                    if (val != 0 && allGMT != 0) {
                        //in block w/ TIGRrole
                        int a = (int) val;
                        //in block not TIGRrole
                        //int b = geneids.length - (int) val;
                        int b = count_mapped_in_block - (int) val;
                        //out block w/ TIGRrole
                        int c = allGMT - a;
                        //out block not TIGRrole
                        //int d = TFEXP_data.data.length - (a + b) - c;
                        //int d = datageneids.length - (a + b) - c;
                        int d = this.data_to_GMTmap_size - (a + b) - c;

                        //if (c < a) {
                        //    System.out.println("WARNING overcounting of values " + a + "\tvs " + c);
                        //    c = a;
                        // }
                        //System.out.println(st);

                        //if (debug)
                        //    MoreArray.printArray(geneids);

                        if (debug) {
                            System.out.println("genes in block " + (a + b));
                            System.out.println("genes w/ GMT " + (a + c));
                        }

                        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"GMT\",\"noGMT\")))";
                        irv.Rexpr = irv.Rengine.eval(rcall1);

                        String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                        try {
                            irv.Rexpr = irv.Rengine.eval(rcall2);
                            double pval = irv.Rexpr.asDouble();
                            if (debug) {
                                System.out.println("GMT pval " + st + "\t" + a + "\t" + b + "\t" + c + "\t" + d);
                                System.out.println("GMT pval " + st + "\t" + pval);
                            }

                            GMTlabelar.add(st);
                            GMTfreqar.add(pval);
                            GMTnodelabelar.add(i);
                        } catch (Exception e) {
                            System.out.println("GMT error " + st + "\t" + a + "\t" + b + "\t" + c + "\t" + d);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //FDR correction of Fisher exact p-values
        if (GMTfreqar.size() > 0) {
            double[] unadjustallpvals = MoreArray.ArrayListtoDouble(GMTfreqar);
            //System.out.println("unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
            boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
            //irv.Rengine.eval("data <- c(" + MoreArray.toString(TIGRrolelabelfreq, ",") + ")");
            String rcall2 = "p.adjust(data,\"fdr\")";
            irv.Rexpr = irv.Rengine.eval(rcall2);
            double[] allpvals = irv.Rexpr.asDoubleArray();

            boolean[] trackEnrich = new boolean[BIC.size()];
            //concatenate labels for all significant cases and find top p-value
            for (int i = 0; i < allpvals.length; ) {
                int curind = (Integer) GMTnodelabelar.get(i);
                double curval = allpvals[i];//(Double) TIGRrolefreqar.get(i);
                double curunval = unadjustallpvals[i];//(Double) TIGRrolefreqar.get(i);
                String curlab = (String) GMTlabelar.get(i);
                int curpos = curind;

                ArrayList curindlist = new ArrayList();
                ArrayList curvallist = new ArrayList();
                ArrayList curunvallist = new ArrayList();
                ArrayList curlablist = new ArrayList();

                if (debug)
                    System.out.println("assignGMT bf loop " + i);
                //populate for this block
                int cursize = 0;
                while (curind == curpos && i < allpvals.length) {
                    curindlist.add(curind);
                    curvallist.add(curval + "_" + cursize + "_" + i + "_" + curind + "_" + curunval + "_" + curlab);
                    curunvallist.add(curunval);
                    curlablist.add(curlab);
                    i++;
                    cursize++;
                    if (i < allpvals.length) {
                        curind = (Integer) GMTnodelabelar.get(i);
                        curval = allpvals[i];//(Double) TIGRrolefreqar.get(i);
                        curunval = unadjustallpvals[i];//(Double) TIGRrolefreqar.get(i);
                        curlab = (String) GMTlabelar.get(i);
                    }
                }
                if (debug)
                    System.out.println("assignGMT af loop " + i);

                String[] sorted = MoreArray.ArrayListtoString(curvallist);
                String[] unsorted = MoreArray.copy(sorted);
                Arrays.sort(sorted);

                System.out.println("af sort GMT " + sorted[0] + "\t" + sorted[sorted.length - 1]);
                System.out.println("sort GMT " + MoreArray.toString(sorted, ","));
                System.out.println("unsorted GMT " + MoreArray.toString(unsorted, ","));

                String pvallist = "";
                String upvallist = "";
                //for all current labels
                for (int m = 0; m < sorted.length; m++) {
                    String pstr = sorted[m];
                    int undone = pstr.indexOf("_");
                    int undtwo = pstr.indexOf("_", undone + 1);
                    double p = Double.valueOf(pstr.substring(0, undone));
                    //index in unsorted to retrieve label
                    //for all significant cases
                    if (p < GMT_pvalue_cutoff || do_all_pvals) {
                        //int index = MoreArray.getArrayInd(unsorted, p);
                        int index = Integer.valueOf(pstr.substring(undone + 1, undtwo));
                        String s = ((String) curlablist.get(index)).trim();
                        double up = (Double) curunvallist.get(index);

                        if (debug)
                            System.out.println("test assignGMT " + s + "\t" + p + "\t" + GMT_pvalue_cutoff + "\t" + index + "\t" + m);
                        int indexinref = (Integer) curindlist.get(index);

                        if (debug)
                            System.out.println("assignGMT trackEnrich " + trackEnrich[indexinref] + "\t" + doCount);
                        if (!trackEnrich[indexinref] && doCount) {
                            GMTcount++;
                            trackEnrich[indexinref] = true;
                            if (debug)
                                System.out.println("assignGMT trackEnrich " + GMTcount + "\t" + GMTlabel[indexinref]);
                        }

                        if (Double.isNaN(GMTlabelfreq[indexinref])) {
                            GMTlabelfreq[indexinref] = p;
                        } else if (p < GMTlabelfreq[indexinref]) {
                            GMTlabelfreq[indexinref] = p;
                        }

                        if (GMTlabel[indexinref] == null) {
                            System.out.println("GMTlabel null " + s);
                            GMTlabel[indexinref] = s;//(GMT_header == "null" ? "" : GMT_header) + ": " +
                            pvallist = "" + p;
                            upvallist = "" + up;

                            Object o = foundGMT.get(s);
                            if (o == null) {
                                foundGMT.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundGMT.put(s, iv + 1);
                            }
                        } else {
                            System.out.println("GMTlabel not null " + s + "\t" + GMTlabel[indexinref]);
                            //check if contains that term already
                            if (!GMTlabel[indexinref].equals(s) && GMTlabel[indexinref].indexOf("_" + s + "_") == -1 &&
                                    !GMTlabel[indexinref].startsWith(s + "_") &&
                                    !GMTlabel[indexinref].endsWith("_" + s)) {

                                GMTlabel[indexinref] += "_" + s;
                                pvallist += "__" + p;
                                upvallist += "__" + up;
                            }

                            Object o = foundGMT.get(s);
                            if (o == null) {
                                foundGMT.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundGMT.put(s, iv + 1);
                            }
                        }
                        if (debug)
                            System.out.println("bicluster GMT summary " + i + "\t" + m + "\t" + GMTlabel[indexinref] + "\t" +
                                    GMTlabelfreq[indexinref] + "\t" + pvallist + "\t" + upvallist);
                    }
                }

            }//end for loop

            //switch p-values for color scale
            for (int i = 0; i < GMTlabel.length; i++) {
                if (Double.isNaN(GMTlabelfreq[i])) {
                    GMTlabelfreq[i] = 1;
                    GMTlabel[i] = "none";
                } /*else {
                TIGRrolelabelfreq[i] = 1 - TIGRrolelabelfreq[i];
            }*/
            }

            //coverage
            Set GMTf = foundGMT.keySet();
            Iterator it = GMTf.iterator();
            while (it.hasNext()) {
                String s = (String) it.next();
                System.out.println("foundGMT " + s);
            }
            System.out.println("foundGMT " + GMTf.size());

            Set GMTa = totalGMT.keySet();
            it = GMTa.iterator();
            while (it.hasNext()) {
                String s = (String) it.next();
                if (debug)
                    System.out.println("totalGMT " + s);
            }

            GMTcover = (double) GMTf.size() / (double) GMTa.size();
            System.out.println("GMTcover biclusters " + GMTcount + "\tunique GMT " + GMTf.size() + "\t" + (double) GMTa.size());
        } else {
            System.out.println("GMT GMTfreqar has 0 entries");
        }

        ArrayList ret = new ArrayList();
        ret.add(GMTlabel);
        ret.add(GMTlabelfreq);
        return ret;
    }

    /**
     * @param doCount
     * @return
     */
    private ArrayList assignTIGR(boolean doCount) {
        System.out.println("assignTIGR");
        String[] TIGRlabel = new String[BIC.size()];
        double[] TIGRlabelfreq = MoreArray.initArrayD(BIC.size(), Double.NaN);

        ArrayList TIGRlabelar = new ArrayList();
        ArrayList TIGRfreqar = new ArrayList();
        ArrayList TIGRnodelabelar = new ArrayList();

        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            HashMap curmap = new HashMap();
            int countb = 0;
            int count_mapped_in_block = 0;
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];
                ArrayList TIGRar = (ArrayList) TIGRmap.get(geneids[g]);
                if (TIGRar != null) {
                    count_mapped_in_block++;
                    for (int h = 0; h < TIGRar.size(); h++) {
                        String TIGR = (String) TIGRar.get(h);
                        //thisgoid.add(row[5]);
                        //thisgolab.add(row[4]);
                        if (TIGR != null && !TIGR.equals("")) {
                            try {
                                Object test = curmap.get(TIGR);
                                if (test == null) {
                                    System.out.println("assignTIGR " + datageneids[v.genes[g] - 1] +
                                            "\t" + TIGR + "\t" + 1);
                                    curmap.put(TIGR, 1.0);
                                    countb++;
                                } else {
                                    double val = (Double) test;
                                    System.out.println("assignTIGR " + datageneids[v.genes[g] - 1] +
                                            "\t" + TIGR + "\t" + (val + 1));
                                    curmap.put(TIGR, (val + 1.0));
                                    countb++;
                                }
                            } catch (Exception e) {
                                //MoreArray.printArray(TIGR);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            System.out.println("assignTIGR curmap.size() " + curmap.size());

            if (curmap.size() > 0) {
                Set s = curmap.entrySet();
                Iterator it = s.iterator();

                //ArrayList get = getTIGRcounts(it);
                //System.out.println("TIGR length " + mean.length + "\t" + TIGRREF_data.data.length);

                //if (get != null && get.size() > 0) {
                /* double maxval = (Double) get.get(0);
            String max = (String) get.get(1);
            int totalassign = (Integer) get.get(2);*/

                int countthis = 0;
                while (it.hasNext()) {
                    Map.Entry cur = (Map.Entry) it.next();
                    String st = (String) cur.getKey();
                    double val = (Double) cur.getValue();
                    int allTIGR = countTIGR(st);
                    if (val != 0 && allTIGR != 0) {
                        //in block w/ TIGR
                        int a = (int) val;
                        //in block not TIGR
                        //int b = geneids.length - (int) val;
                        int b = count_mapped_in_block - (int) val;
                        //out block w/ TIGR
                        int c = allTIGR - a;
                        //out block not TIGR
                        //int d = TFEXP_data.data.length - (a + b) - c;
                        //int d = datageneids.length - (a + b) - c;
                        int d = this.data_to_TIGRmap_size - (a + b) - c;

                        System.out.println("genes in block " + (a + b));
                        System.out.println("genes w/ TIGR " + (a + c));

                        //if (c < a) {
                        //   System.out.println("WARNING overcounting of values " + a + "\tvs " + c);
                        //  c = a;
                        //}

                        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TIGR\",\"noTIGR\")))";
                        irv.Rexpr = irv.Rengine.eval(rcall1);


                        System.out.println("TIGR pval " + a + "\t" + b + "\t" + c + "\t" + d + "\tallTIGR " + allTIGR
                                + "\tkey " + st);

                        String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                        try {
                            irv.Rexpr = irv.Rengine.eval(rcall2);
                            double pval = irv.Rexpr.asDouble();

                            System.out.println("TIGR pval " + pval);

                            TIGRlabelar.add(st);
                            TIGRfreqar.add(pval);
                            TIGRnodelabelar.add(i);
                        } catch (Exception e) {
                            System.out.println("TIGR error " + a + "\t" + b + "\t" + c + "\t" + d + "\tallTIGR "
                                    + allTIGR + "\tkey " + st);
                            e.printStackTrace();
                        }
                    }
                }
                if (countthis > 0 && doCount) {
                    TIGRcount++;
                }
            }
        }
        if (TIGRfreqar.size() > 0) {
            double[] unadjustallpvals = MoreArray.ArrayListtoDouble(TIGRfreqar);
            //System.out.println("unadjusted unsorted " + unadjustallpvals.length);// + "\t" + MoreArray.toString(unadjustallpvals, ","));
            //System.out.println("unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
            boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
            //irv.Rengine.eval("data <- c(" + MoreArray.toString(TIGRlabelfreq, ",") + ")");
            String rcall2 = "p.adjust(data,\"fdr\")";
            irv.Rexpr = irv.Rengine.eval(rcall2);
            double[] allpvals = irv.Rexpr.asDoubleArray();

            //concatenate labels for all significant cases and find top p-value
            for (int i = 0; i < allpvals.length; ) {
                int curind = (Integer) TIGRnodelabelar.get(i);
                double curval = allpvals[i];//(Double) TIGRfreqar.get(i);
                double curunval = unadjustallpvals[i];//(Double) TIGRfreqar.get(i);
                String curlab = (String) TIGRlabelar.get(i);
                int curpos = curind;

                ArrayList curindlist = new ArrayList();
                ArrayList curvallist = new ArrayList();
                ArrayList curunvallist = new ArrayList();
                ArrayList curlablist = new ArrayList();

                System.out.println("assignTIGR bf loop " + i);
                //populate for this block
                while (curind == curpos && i < allpvals.length) {
                    curindlist.add(curind);
                    curvallist.add(curval);
                    curunvallist.add(curunval);
                    curlablist.add(curlab);
                    i++;
                    if (i < allpvals.length) {
                        curind = (Integer) TIGRnodelabelar.get(i);
                        curval = allpvals[i];//(Double) TIGRfreqar.get(i);
                        curunval = unadjustallpvals[i];//(Double) TIGRfreqar.get(i);
                        curlab = (String) TIGRlabelar.get(i);
                    }
                }
                System.out.println("assignTIGR af loop " + i);

                double[] sorted = MoreArray.ArrayListtoDouble(curvallist);
                double[] unsorted = MoreArray.copy(sorted);
                Arrays.sort(sorted);

                System.out.println("af sort TIGR " + sorted[0] + "\t" + sorted[sorted.length - 1]);
                System.out.println("sort TIGR " + MoreArray.toString(sorted, ","));
                System.out.println("unsorted TIGR " + MoreArray.toString(unsorted, ","));

                String pvallist = "";
                String upvallist = "";
                //for all current labels
                for (int m = 0; m < sorted.length; m++) {
                    double p = sorted[m];
                    //index in unsorted to retrieve label
                    //for all significant cases
                    if (p < TIGR_pvalue_cutoff || do_all_pvals) {
                        int index = MoreArray.getArrayInd(unsorted, p);

                        String s = (String) curlablist.get(index);
                        double up = (Double) curunvallist.get(index);
                        System.out.println("test assignTIGR " + p + "\t" + TIGR_pvalue_cutoff + "\t" + index + "\t" + m + "\t" + s);
                        int indexinref = (Integer) curindlist.get(index);

                        if (Double.isNaN(TIGRlabelfreq[indexinref])) {
                            TIGRlabelfreq[indexinref] = p;
                        } else if (p < TIGRlabelfreq[indexinref]) {
                            TIGRlabelfreq[indexinref] = p;
                        }
                        if (TIGRlabel[indexinref] == null) {
                            TIGRlabel[indexinref] = "TIGR: " + s;
                            pvallist = "" + p;
                            upvallist = "" + up;
                        } else {
                            TIGRlabel[indexinref] += "_" + s;
                            pvallist += "__" + p;
                            upvallist += "__" + up;
                        }
                        System.out.println("bicluster TIGR summary " + i + "\t" + m + "\t" + TIGRlabel[indexinref] + "\t" +
                                TIGRlabelfreq[indexinref] + "\t" + pvallist + "\t" + upvallist);
                    }
                }

            }//end for loop
        } else {
            System.out.println("TIGR TIGRfreqar has 0 entries");
        }
        //switch p-values for color scale
        for (int i = 0; i < TIGRlabel.length; i++) {
            if (Double.isNaN(TIGRlabelfreq[i])) {
                TIGRlabelfreq[i] = 1;
                TIGRlabel[i] = "none";
            } /*else {
                TIGRlabelfreq[i] = 1 - TIGRlabelfreq[i];
            }*/
        }

        //flip p-value for color scale
        //TIGRlabelfreq = stat.subtract(1, TIGRlabelfreq);
        ArrayList ret = new ArrayList();
        ret.add(TIGRlabel);
        ret.add(TIGRlabelfreq);
        return ret;
    }

    /**
     * @param doCount
     * @return
     */
    private ArrayList assignTIGRrole(boolean doCount) {
        System.out.println("assignTIGRrole");
        String[] TIGRrolelabel = new String[BIC.size()];
        double[] TIGRrolelabelfreq = MoreArray.initArrayD(BIC.size(), Double.NaN);

        ArrayList TIGRrolelabelar = new ArrayList();
        ArrayList TIGRrolefreqar = new ArrayList();
        ArrayList TIGRrolenodelabelar = new ArrayList();

        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            HashMap curmap = new HashMap();
            int countb = 0;
            int count_mapped_in_block = 0;
            System.out.println("assignTIGRrole " + i + "\t" + geneids.length);
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];
                ArrayList TIGRrolear = (ArrayList) TIGRrolemap.get(geneids[g]);
                System.out.println("assignTIGRrole " + v.genes[g] + "\t" + geneids[g]);
                if (TIGRrolear != null) {
                    count_mapped_in_block++;
                    for (int h = 0; h < TIGRrolear.size(); h++) {
                        String TIGRrole = (String) TIGRrolear.get(h);
                        //thisgoid.add(row[5]);
                        //thisgolab.add(row[4]);
                        if (TIGRrole != null && !TIGRrole.equals("")) {
                            try {
                                Object test = curmap.get(TIGRrole);
                                if (test == null) {
                                    curmap.put(TIGRrole, 1.0);
                                    System.out.println("assignTIGRrole " + TIGRrole + "\t" + 1);
                                    countb++;
                                } else {
                                    double val = (Double) test;
                                    curmap.put(TIGRrole, (val + 1.0));
                                    System.out.println("assignTIGRrole " + TIGRrole + "\t" + (val + 1));
                                    countb++;
                                }
                            } catch (Exception e) {
                                //MoreArray.printArray(TIGRrole);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            System.out.println("assignTIGRrole curmap.size() " + curmap.size());

            if (curmap.size() > 0) {
                Set s = curmap.entrySet();
                Iterator it = s.iterator();

                //ArrayList get = getTIGRrolecounts(it);
                //System.out.println("TIGRrole length " + mean.length + "\t" + TIGRroleREF_data.data.length);

                //if (get != null && get.size() > 0) {
                /* double maxval = (Double) get.get(0);
            String max = (String) get.get(1);
            int totalassign = (Integer) get.get(2);*/

                while (it.hasNext()) {
                    Map.Entry cur = (Map.Entry) it.next();
                    String st = (String) cur.getKey();
                    double val = (Double) cur.getValue();

                    int allTIGRrole = countTIGRrole(st);
                    if (val != 0 && allTIGRrole != 0) {
                        //in block w/ TIGRrole
                        int a = (int) val;
                        //in block not TIGRrole
                        //int b = geneids.length - (int) val;
                        int b = count_mapped_in_block - (int) val;
                        //out block w/ TIGRrole
                        int c = allTIGRrole - a;
                        //out block not TIGRrole
                        //int d = TFEXP_data.data.length - (a + b) - c;
                        //int d = datageneids.length - (a + b) - c;
                        int d = data_to_TIGRrolemap_size - (a + b) - c;

                        System.out.println("genes in block " + (a + b));
                        System.out.println("genes w/ TIGRrole " + (a + c));

                        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TIGRrole\",\"noTIGRrole\")))";
                        irv.Rexpr = irv.Rengine.eval(rcall1);

                        String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                        try {
                            irv.Rexpr = irv.Rengine.eval(rcall2);
                            double pval = irv.Rexpr.asDouble();
                            System.out.println("TIGRrole pval " + a + "\t" + b + "\t" + c + "\t" + d);
                            System.out.println("TIGRrole pval " + pval);

                            TIGRrolelabelar.add(st);
                            TIGRrolefreqar.add(pval);
                            TIGRrolenodelabelar.add(i);
                        } catch (Exception e) {
                            System.out.println("TIGRrole error " + a + "\t" + b + "\t" + c + "\t" + d);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //FDR correction of Fisher exact p-values
        if (TIGRrolefreqar.size() > 0) {
            double[] unadjustallpvals = MoreArray.ArrayListtoDouble(TIGRrolefreqar);
            //System.out.println("unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
            boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
            //irv.Rengine.eval("data <- c(" + MoreArray.toString(TIGRrolelabelfreq, ",") + ")");
            String rcall2 = "p.adjust(data,\"fdr\")";
            irv.Rexpr = irv.Rengine.eval(rcall2);
            double[] allpvals = irv.Rexpr.asDoubleArray();

            boolean[] trackEnrich = new boolean[BIC.size()];
            //concatenate labels for all significant cases and find top p-value
            for (int i = 0; i < allpvals.length; ) {
                int curind = (Integer) TIGRrolenodelabelar.get(i);
                double curval = allpvals[i];//(Double) TIGRrolefreqar.get(i);
                double curunval = unadjustallpvals[i];//(Double) TIGRrolefreqar.get(i);
                String curlab = (String) TIGRrolelabelar.get(i);
                int curpos = curind;

                ArrayList curindlist = new ArrayList();
                ArrayList curvallist = new ArrayList();
                ArrayList curunvallist = new ArrayList();
                ArrayList curlablist = new ArrayList();

                System.out.println("assignTIGRrole bf loop " + i);
                //populate for this block
                while (curind == curpos && i < allpvals.length) {
                    curindlist.add(curind);
                    curvallist.add(curval);
                    curunvallist.add(curunval);
                    curlablist.add(curlab);
                    i++;
                    if (i < allpvals.length) {
                        curind = (Integer) TIGRrolenodelabelar.get(i);
                        curval = allpvals[i];//(Double) TIGRrolefreqar.get(i);
                        curunval = unadjustallpvals[i];//(Double) TIGRrolefreqar.get(i);
                        curlab = (String) TIGRrolelabelar.get(i);
                    }
                }
                System.out.println("assignTIGRrole af loop " + i);

                double[] sorted = MoreArray.ArrayListtoDouble(curvallist);
                double[] unsorted = MoreArray.copy(sorted);
                Arrays.sort(sorted);

                System.out.println("af sort TIGRrole " + sorted[0] + "\t" + sorted[sorted.length - 1]);
                System.out.println("sort TIGRrole " + MoreArray.toString(sorted, ","));
                System.out.println("unsorted TIGRrole " + MoreArray.toString(unsorted, ","));

                String pvallist = "";
                String upvallist = "";
                //for all current labels
                for (int m = 0; m < sorted.length; m++) {
                    double p = sorted[m];
                    //index in unsorted to retrieve label
                    //for all significant cases
                    if (p < TIGR_pvalue_cutoff || do_all_pvals) {

                        int index = MoreArray.getArrayInd(unsorted, p);

                        String s = (String) curlablist.get(index);
                        double up = (Double) curunvallist.get(index);
                        System.out.println("test assignTIGRrole " + p + "\t" + TIGR_pvalue_cutoff + "\t" + index + "\t" + m + "\t" + s);
                        int indexinref = (Integer) curindlist.get(index);

                        System.out.println("assignTIGRrole trackEnrich " + trackEnrich[indexinref] + "\t" + doCount);
                        if (!trackEnrich[indexinref] && doCount) {
                            TIGRrolecount++;
                            trackEnrich[indexinref] = true;
                            System.out.println("assignTIGRrole trackEnrich " + TIGRrolecount + "\t" + TIGRrolelabel[indexinref]);
                        }

                        if (Double.isNaN(TIGRrolelabelfreq[indexinref])) {
                            TIGRrolelabelfreq[indexinref] = p;
                        } else if (p < TIGRrolelabelfreq[indexinref]) {
                            TIGRrolelabelfreq[indexinref] = p;
                        }

                        if (TIGRrolelabel[indexinref] == null) {
                            TIGRrolelabel[indexinref] = "TIGRrole: " + s;
                            pvallist = "" + p;
                            upvallist = "" + up;

                            Object o = foundTIGRrole.get(s);
                            if (o == null) {
                                foundTIGRrole.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundTIGRrole.put(s, iv + 1);
                            }
                        } else {
                            if (!TIGRrolelabel[indexinref].equals(s) && TIGRrolelabel[indexinref].indexOf("_" + s + "_") == -1 &&
                                    !TIGRrolelabel[indexinref].startsWith(s + "_") &&
                                    !TIGRrolelabel[indexinref].endsWith("_" + s)) {

                                TIGRrolelabel[indexinref] += "_" + s;
                                pvallist += "__" + p;
                                upvallist += "__" + up;
                            }

                            Object o = foundTIGRrole.get(s);
                            if (o == null) {
                                foundTIGRrole.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundTIGRrole.put(s, iv + 1);
                            }
                        }
                        System.out.println("bicluster TIGRrole summary " + i + "\t" + m + "\t" + TIGRrolelabel[indexinref] + "\t" +
                                TIGRrolelabelfreq[indexinref] + "\t" + pvallist + "\t" + upvallist);
                    }
                }

            }//end for loop

            //switch p-values for color scale
            for (int i = 0; i < TIGRrolelabel.length; i++) {
                if (Double.isNaN(TIGRrolelabelfreq[i])) {
                    TIGRrolelabelfreq[i] = 1;
                    TIGRrolelabel[i] = "none";
                } /*else {
                TIGRrolelabelfreq[i] = 1 - TIGRrolelabelfreq[i];
            }*/
            }

            //coverage
            Set TIGRf = foundTIGRrole.keySet();
            Iterator it = TIGRf.iterator();
            while (it.hasNext()) {
                String s = (String) it.next();
                System.out.println("foundTIGRrole " + s);
            }
            System.out.println("foundTIGRrole " + TIGRf.size());

            Set TIGRa = totalTIGRrole.keySet();
            it = TIGRa.iterator();
            while (it.hasNext()) {
                String s = (String) it.next();
                System.out.println("totalTIGRrole " + s);
            }

            TIGRrolecover = (double) TIGRf.size() / (double) TIGRa.size();
            System.out.println("TIGRrolecover biclusters " + TIGRrolecount + "\tunique TIGRroles " + TIGRf.size() + "\t" + (double) TIGRa.size());
        } else {
            System.out.println("TIGRrole TIGRrolefreqar has 0 entries");
        }

        ArrayList ret = new ArrayList();
        ret.add(TIGRrolelabel);
        ret.add(TIGRrolelabelfreq);
        return ret;
    }

    /**
     * @param doCount
     * @return
     */
    private ArrayList assignPath(boolean doCount) {
        System.out.println("assignPath");
        String[] Pathlabel = new String[BIC.size()];
        String[] Pathlabelpvallist = new String[BIC.size()];
        double[] Pathlabelfreq = MoreArray.initArrayD(BIC.size(), Double.NaN);

        ArrayList Pathlabelar = new ArrayList();
        ArrayList Pathfreqar = new ArrayList();
        ArrayList Pathnodelabelar = new ArrayList();

        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            HashMap curmap = new HashMap();

            int count_mapped_in_block = 0;
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];

                ArrayList Pathar = (ArrayList) PATHmap.get(geneids[g]);
                if (Pathar != null) {
                    count_mapped_in_block++;
                    for (int h = 0; h < Pathar.size(); h++) {
                        String Path = (String) Pathar.get(h);
                        if (Path != null && !Path.equals("")) {
                            try {
                                Object test = curmap.get(Path);
                                if (test == null) {
                                    curmap.put(Path, 1.0);
                                    System.out.println("assignPath 1 " + Path + "\t" + 1);
                                } else {
                                    double val = (Double) test;
                                    curmap.put(Path, (val + 1.0));
                                    System.out.println("assignPath >1 " + Path + "\t" + (val + 1));
                                }
                            } catch (Exception e) {
                                //MoreArray.printArray(Path);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            System.out.println("assignPath curmap.size() " + curmap.size());

            if (curmap.size() > 0) {
                Set s = curmap.entrySet();
                Iterator it = s.iterator();

                while (it.hasNext()) {
                    Map.Entry cur = (Map.Entry) it.next();
                    String st = (String) cur.getKey();
                    System.out.println("assignPath " + BIC.size() + "\tst " + st);
                    double val = (Double) cur.getValue();

                    int allPath = countPath(st);
                    if (val != 0 && allPath != 0) {
                        //in block w/ Path
                        int a = (int) val;
                        //in block not Path
                        //int b = geneids.length - (int) val;
                        int b = count_mapped_in_block - (int) val;
                        //out block w/ Path
                        int c = allPath - a;
                        //out block not Path
                        //int d = TFEXP_data.data.length - (a + b) - c;
                        //int d = datageneids.length - (a + b) - c;
                        int d = this.data_to_Pathmap_size - (a + b) - c;

                        System.out.println("genes in block " + (a + b));
                        System.out.println("genes w/ Path " + (a + c));

                        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"Path\",\"noPath\")))";
                        irv.Rexpr = irv.Rengine.eval(rcall1);

                        String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                        double pval = Double.NaN;
                        try {
                            irv.Rexpr = irv.Rengine.eval(rcall2);

                            if (irv.Rexpr != null) {
                                pval = irv.Rexpr.asDouble();

                                System.out.println("assignPath pval " + a + "\t" + b + "\t" + c + "\t" + d);
                                System.out.println("assignPath pval " + pval + "\t" + st);
                            } else {
                                pval = Double.NaN;
                                System.out.println("assignPath error pval = Double.NaN " + a + "\t" + b + "\t" + c + "\t" + d);
                                //System.exit(0);
                            }
                        } catch (Exception e) {
                            pval = Double.NaN;
                            System.out.println("assignPath error pval = Double.NaN " + a + "\t" + b + "\t" + c + "\t" + d);
                            e.printStackTrace();
                            //System.exit(0);
                        }

                        Pathlabelar.add(st);

                        //System.out.println("assignPath " + MoreArray.arrayListtoString(Pathlabelar, ","));

                        Pathfreqar.add(pval);
                        Pathnodelabelar.add(i);
                    }
                }
            }
        }

        //FDR correction of Fisher exact p-values
        double[] unadjustallpvals = MoreArray.ArrayListtoDouble(Pathfreqar);
        System.out.println("unadjustallpvals " + Pathfreqar.size());
        ArrayList ret = new ArrayList();

        if (Pathfreqar.size() > 0) {
            System.out.println("unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
            boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
            //irv.Rengine.eval("data <- c(" + MoreArray.toString(Pathlabelfreq, ",") + ")");
            String rcall2 = "p.adjust(data,\"fdr\")";
            irv.Rexpr = irv.Rengine.eval(rcall2);
            double[] allpvals = irv.Rexpr.asDoubleArray();


            boolean[] trackEnrich = new boolean[BIC.size()];
            //concatenate labels for all significant cases and find top p-value
            for (int i = 0; i < allpvals.length; ) {
                int curind = (Integer) Pathnodelabelar.get(i);
                double curval = allpvals[i];//(Double) Pathfreqar.get(i);
                double curunval = unadjustallpvals[i];//(Double) Pathfreqar.get(i);
                String curlab = (String) Pathlabelar.get(i);
                int curpos = curind;

                ArrayList curindlist = new ArrayList();
                ArrayList curvallist = new ArrayList();
                ArrayList curunvallist = new ArrayList();
                ArrayList curlablist = new ArrayList();

                System.out.println("assignPath bf loop " + i);
                //populate for this block
                while (curind == curpos && i < allpvals.length) {

                    if (curlablist.indexOf(curlab) == -1) {
                        curindlist.add(curind);
                        curvallist.add(curval);
                        curunvallist.add(curunval);
                        //System.out.println("assignPath " + curlab);
                        curlablist.add(curlab);
                    } else {
                        System.out.println("assignPath curlablist already contains " + curlab);
                    }
                    i++;
                    if (i < allpvals.length) {
                        curind = (Integer) Pathnodelabelar.get(i);
                        curval = allpvals[i];
                        curunval = unadjustallpvals[i];
                        curlab = (String) Pathlabelar.get(i);
                    }
                }
                System.out.println("assignPath af loop " + i);

                double[] sorted = MoreArray.ArrayListtoDouble(curvallist);
                double[] unsorted = MoreArray.copy(sorted);
                Arrays.sort(sorted);

                System.out.println("af sort Path " + sorted[0] + "\t" + sorted[sorted.length - 1]);
                System.out.println("sort Path " + MoreArray.toString(sorted, ","));
                System.out.println("unsorted Path " + MoreArray.toString(unsorted, ","));

                String upvallist = "";
                //for all current labels
                for (int m = 0; m < sorted.length; m++) {
                    double p = sorted[m];
                    //index in unsorted to retrieve label
                    //for all significant cases
                    if (p < Path_pvalue_cutoff || do_all_pvals) {
                        int index = MoreArray.getArrayInd(unsorted, p);

                        String s = (String) curlablist.get(index);
                        double up = (Double) curunvallist.get(index);
                        //System.out.println("test assignPath " + p + "\t" + TIGR_pvalue_cutoff + "\t" + index + "\t" + m + "\t" + s);
                        int indexinref = (Integer) curindlist.get(index);

                        if (!trackEnrich[indexinref] && doCount) {
                            Pathcount++;
                            trackEnrich[indexinref] = true;
                        }

                        if (Double.isNaN(Pathlabelfreq[indexinref])) {
                            Pathlabelfreq[indexinref] = p;
                        } else if (p < Pathlabelfreq[indexinref]) {
                            Pathlabelfreq[indexinref] = p;
                        }

                        if (Pathlabel[indexinref] == null) {
                            Pathlabel[indexinref] = "Path: " + s;
                            Pathlabelpvallist[indexinref] = "" + p;
                            upvallist = "" + up;

                            Object o = foundPath.get(s);
                            if (o == null) {
                                foundPath.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundPath.put(s, iv + 1);
                            }
                        } else {
                            if (!Pathlabel[indexinref].equals("Path: " + s) && Pathlabel[indexinref].indexOf("_" + s + "_") == -1 &&
                                    !Pathlabel[indexinref].startsWith("Path: " + s + "_") &&
                                    !Pathlabel[indexinref].endsWith("_" + s)) {

                                Pathlabel[indexinref] += "_" + s;
                                //System.out.println("assignPath Pathlabel " + i + "\t" + m + "\t" + Pathlabel[indexinref]);
                                Pathlabelpvallist[indexinref] += "__" + p;
                                upvallist += "__" + up;
                            }
                            Object o = foundPath.get(s);
                            //only add if unique
                            if (o == null) {
                                foundPath.put(s, 1);
                            } else {
                                int iv = (Integer) o;
                                foundPath.put(s, iv + 1);
                            }
                        }
                        System.out.println("assignPath bicluster Path summary indexinref_" + indexinref + "\ti_" + i + "\t" + m + "\t" +
                                Pathlabel[indexinref] + "\t" +
                                Pathlabelfreq[indexinref] + "\t" + Pathlabelpvallist[indexinref] + "\t" + upvallist);
                    }
                }
            }//end for loop

            Set Pathf = foundPath.keySet();
            Set Patha = totalPath.keySet();
            Pathcover = (double) Pathf.size() / (double) Patha.size();
            System.out.println("Pathcover biclusters " + Pathcount + "\tunique pathways" + Pathf.size() + "\t" + (double) Patha.size());

            //switch p-values for color scale
            for (int i = 0; i < Pathlabel.length; i++) {
                if (Double.isNaN(Pathlabelfreq[i])) {
                    Pathlabelfreq[i] = 1;
                    Pathlabel[i] = "none";
                }
                if (Pathlabel[i] == null) {
                    Pathlabelfreq[i] = 1;
                    Pathlabel[i] = "none";
                    System.out.println("Pathlabel is null " + i);
                }
            }

            Pathlabel = StringUtil.replace(Pathlabel, "'", "");
            ret.add(Pathlabel);
            ret.add(Pathlabelfreq);
            ret.add(Pathlabelpvallist);
        }

        return ret;
    }


    /**
     * @param doCount
     * @return
     */
    private ArrayList assignCOG(boolean doCount) {
        String[] COGlabel = new String[BIC.size()];
        String[] COGlabelpvallist = new String[BIC.size()];
        double[] COGlabelfreq = MoreArray.initArrayD(BIC.size(), Double.NaN);

        ArrayList COGlabelar = new ArrayList();
        ArrayList COGfreqar = new ArrayList();
        ArrayList COGnodelabelar = new ArrayList();

        for (int i = 0; i < BIC.size(); i++) {
            System.out.print(".");
            ValueBlock v = (ValueBlock) BIC.get(i);
            String[] geneids = new String[v.genes.length];
            HashMap curmap = new HashMap();

            int count_mapped_in_block = 0;
            for (int g = 0; g < geneids.length; g++) {
                //geneids[g] = expr_data.ylabels[v.genes[g] - 1];
                geneids[g] = datageneids[v.genes[g] - 1];

                ArrayList COGar = (ArrayList) COGmap.get(geneids[g]);
                if (COGar != null) {

                    count_mapped_in_block++;

                    for (int h = 0; h < COGar.size(); h++) {
                        String COG = (String) COGar.get(h);
                        //for (int a = 0; a < COG1.length(); a++) {
                        //String COG = (String) COGcodemap.get("" + COG1.charAt(a));
                        System.out.println("assignCOG " + COG);
                        if (COG != null && !COG.equals("")) {
                            try {
                                Object test = curmap.get(COG);
                                if (test == null) {
                                    curmap.put(COG, 1.0);
                                    System.out.println("assignCOG 1 " + COG + "\t" + 1);
                                } else {
                                    double val = (Double) test;
                                    curmap.put(COG, (val + 1.0));
                                    System.out.println("assignCOG >1 " + COG + "\t" + (val + 1));
                                }
                            } catch (Exception e) {
                                //MoreArray.printArray(COG);
                                e.printStackTrace();
                            }
                        }
                        //}
                    }
                }
            }
            System.out.println("assignPath curmap.size() " + curmap.size());

            if (curmap.size() > 0) {
                Set s = curmap.entrySet();
                Iterator it = s.iterator();

                while (it.hasNext()) {
                    Map.Entry cur = (Map.Entry) it.next();
                    String st = (String) cur.getKey();
                    System.out.println("assignCOG " + BIC.size() + "\tst " + st);
                    double val = (Double) cur.getValue();

                    int allCOG = countCOG(st);
                    if (val != 0 && allCOG != 0) {
                        //in block w/ Path
                        int a = (int) val;
                        //in block not Path
                        //int b = geneids.length - (int) val;
                        int b = count_mapped_in_block - (int) val;
                        //out block w/ Path
                        int c = allCOG - a;
                        //out block not Path
                        //int d = datageneids.length - (a + b) - c;
                        //int d = COGmap.size() - (a + b) - c;
                        int d = this.data_to_COGmap_size - (a + b) - c;

                        System.out.println("genes in block " + (a + b));
                        System.out.println("genes w/ Path " + (a + c));

                        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"Path\",\"noPath\")))";
                        irv.Rexpr = irv.Rengine.eval(rcall1);

                        String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                        double pval = 0;
                        try {
                            irv.Rexpr = irv.Rengine.eval(rcall2);
                            pval = irv.Rexpr.asDouble();
                            System.out.println("assignCOG pval " + a + "\t" + b + "\t" + c + "\t" + d);
                            System.out.println("assignCOG pval " + pval + "\t" + st);
                        } catch (Exception e) {
                            System.out.println("assignCOG error " + a + "\t" + b + "\t" + c + "\t" + d);
                            e.printStackTrace();
                        }

                        COGlabelar.add(st);

                        //System.out.println("assignPath " + MoreArray.arrayListtoString(COGlabelar, ","));

                        COGfreqar.add(pval);
                        COGnodelabelar.add(i);
                    }
                }
            }
        }

        //FDR correction of Fisher exact p-values
        double[] unadjustallpvals = MoreArray.ArrayListtoDouble(COGfreqar);
        System.out.println("unadjusted unsorted " + MoreArray.toString(unadjustallpvals, ","));
        boolean retjri = irv.Rengine.assign("data", unadjustallpvals);
        //irv.Rengine.eval("data <- c(" + MoreArray.toString(COGlabelfreq, ",") + ")");
        String rcall2 = "p.adjust(data,\"fdr\")";
        irv.Rexpr = irv.Rengine.eval(rcall2);
        double[] allpvals = irv.Rexpr.asDoubleArray();


        boolean[] trackEnrich = new boolean[BIC.size()];
        //concatenate labels for all significant cases and find top p-value
        for (int i = 0; i < allpvals.length; ) {
            int curind = (Integer) COGnodelabelar.get(i);
            double curval = allpvals[i];//(Double) COGfreqar.get(i);
            double curunval = unadjustallpvals[i];//(Double) COGfreqar.get(i);
            String curlab = (String) COGlabelar.get(i);
            int curpos = curind;

            ArrayList curindlist = new ArrayList();
            ArrayList curvallist = new ArrayList();
            ArrayList curunvallist = new ArrayList();
            ArrayList curlablist = new ArrayList();

            System.out.println("assignCOG bf loop " + i);
            //populate for this block
            while (curind == curpos && i < allpvals.length) {

                if (curlablist.indexOf(curlab) == -1) {
                    curindlist.add(curind);
                    curvallist.add(curval);
                    curunvallist.add(curunval);
                    //System.out.println("assignCOG " + curlab);
                    curlablist.add(curlab);
                } else {
                    System.out.println("assignCOG curlablist already contains " + curlab);
                }
                i++;
                if (i < allpvals.length) {
                    curind = (Integer) COGnodelabelar.get(i);
                    curval = allpvals[i];
                    curunval = unadjustallpvals[i];
                    curlab = (String) COGlabelar.get(i);
                }
            }
            System.out.println("assignCOG af loop " + i);

            double[] sorted = MoreArray.ArrayListtoDouble(curvallist);
            double[] unsorted = MoreArray.copy(sorted);
            Arrays.sort(sorted);

            System.out.println("af sort COG " + sorted[0] + "\t" + sorted[sorted.length - 1]);
            System.out.println("sort COG " + MoreArray.toString(sorted, ","));
            System.out.println("unsorted COG " + MoreArray.toString(unsorted, ","));

            String upvallist = "";
            //for all current labels
            for (int m = 0; m < sorted.length; m++) {
                double p = sorted[m];
                //index in unsorted to retrieve label
                //for all significant cases
                if (p < COG_pvalue_cutoff || do_all_pvals) {
                    int index = MoreArray.getArrayInd(unsorted, p);

                    String s = (String) curlablist.get(index);
                    double up = (Double) curunvallist.get(index);
                    //System.out.println("test assignPath " + p + "\t" + TIGR_pvalue_cutoff + "\t" + index + "\t" + m + "\t" + s);
                    int indexinref = (Integer) curindlist.get(index);

                    if (!trackEnrich[indexinref] && doCount) {
                        COGcount++;
                        trackEnrich[indexinref] = true;
                    }

                    if (Double.isNaN(COGlabelfreq[indexinref])) {
                        COGlabelfreq[indexinref] = p;
                    } else if (p < COGlabelfreq[indexinref]) {
                        COGlabelfreq[indexinref] = p;
                    }

                    if (COGlabel[indexinref] == null) {
                        COGlabel[indexinref] = "COG: " + s;
                        COGlabelpvallist[indexinref] = "" + p;
                        upvallist = "" + up;

                        Object o = foundCOG.get(s);
                        if (o == null) {
                            foundCOG.put(s, 1);
                        } else {
                            int iv = (Integer) o;
                            foundCOG.put(s, iv + 1);
                        }
                    } else {
                        if (!COGlabel[indexinref].equals("COG: " + s) && COGlabel[indexinref].indexOf("_" + s + "_") == -1 &&
                                !COGlabel[indexinref].startsWith("COG: " + s + "_") &&
                                !COGlabel[indexinref].endsWith("_" + s)) {

                            COGlabel[indexinref] += "_" + s;
                            //System.out.println("assignCOG COGlabel " + i + "\t" + m + "\t" + COGlabel[indexinref]);
                            COGlabelpvallist[indexinref] += "__" + p;
                            upvallist += "__" + up;
                        }
                        Object o = foundCOG.get(s);
                        //only add if unique
                        if (o == null) {
                            foundCOG.put(s, 1);
                        } else {
                            int iv = (Integer) o;
                            foundCOG.put(s, iv + 1);
                        }
                    }
                    System.out.println("assignCOG bicluster Path summary indexinref_" + indexinref + "\ti_" + i + "\t" + m + "\t" +
                            COGlabel[indexinref] + "\t" +
                            COGlabelfreq[indexinref] + "\t" + COGlabelpvallist[indexinref] + "\t" + upvallist);
                }
            }
        }//end for loop

        Set COGf = foundCOG.keySet();
        Set COGa = totalCOG.keySet();
        COGcover = (double) COGf.size() / (double) COGa.size();
        System.out.println("COGcover biclusters " + COGcount + "\tunique COGs" + COGf.size() + "\t" + (double) COGa.size());

        //switch p-values for color scale
        for (int i = 0; i < COGlabel.length; i++) {
            if (Double.isNaN(COGlabelfreq[i])) {
                COGlabelfreq[i] = 1;
                COGlabel[i] = "none";
            }
            if (COGlabel[i] == null) {
                COGlabelfreq[i] = 1;
                COGlabel[i] = "none";
                System.out.println("COGlabel is null " + i);
            }
        }

        COGlabel = StringUtil.replace(COGlabel, "'", "");
        ArrayList ret = new ArrayList();
        ret.add(COGlabel);
        ret.add(COGlabelfreq);
        ret.add(COGlabelpvallist);
        return ret;
    }


    /**
     * @param go
     * @return
     */
    public int countGO(String go) {
        int ret = 0;
        if (go != null) {
            for (int g = 0; g < datageneids.length; g++) {
                String id = datageneids[g];//StringUtil.replace(expr_data.ylabels[g], "\"", "");
                ArrayList GOar = (ArrayList) GOmap.get(id);
                if (GOar != null)
                    for (int h = 0; h < GOar.size(); h++) {
                        try {
                            String[] GO = (String[]) GOar.get(h);
                            if (go.equals(GO[4])) {
                                ret++;
                                break;
                            }
                        } catch (Exception e) {
                            String GO = (String) GOar.get(h);
                            if (GO != null) {
                                if (go.equals(GO)) {
                                    ret++;
                                    break;
                                }
                            }
                            //e.printStackTrace();
                        }
                    }
            }
        }

        return ret;
    }

    /**
     * @param ti
     * @return
     */
    public int countTIGR(String ti) {
        int ret = 0;
        //if (ti.equals("TIGR00861 MIP family channel proteins"))
        //   System.out.println("countTIGR " + ti);
        for (int g = 0; g < datageneids.length; g++) {
            String id = datageneids[g];//StringUtil.replace(expr_data.ylabels[g], "\"", "");
            ArrayList tiar = (ArrayList) TIGRmap.get(id);
            // if (ti.equals("TIGR00861 MIP family channel proteins"))
            //    System.out.println("countTIGR " + ti + "\t" + id);
            if (tiar != null) {
                for (int h = 0; h < tiar.size(); h++) {
                    String GO = (String) tiar.get(h);
                    //      if (ti.equals("TIGR00861 MIP family channel proteins"))
                    //         System.out.println("countTIGR " + ti + "\t" + id + "\t" + tiar.size() + "\t" + GO);
                    if (ti.equals(GO)) {
                        ret++;
                        //       if (ti.equals("TIGR00861 MIP family channel proteins"))
                        //          System.out.println("countTIGR found " + ret);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @param ti
     * @return
     */
    public int countGMT(String ti) {
        int ret = 0;
        for (int g = 0; g < datageneids.length; g++) {
            String id = datageneids[g];//StringUtil.replace(expr_data.ylabels[g], "\"", "");
            ArrayList tiar = (ArrayList) GMTmap.get(id);
            if (tiar != null)
                for (int h = 0; h < tiar.size(); h++) {
                    String GMT = (String) tiar.get(h);
                    if (ti.equals(GMT)) {
                        ret++;
                        break;
                    }
                }
        }
        return ret;
    }

    /**
     * @param ti
     * @return
     */
    public int countTIGRrole(String ti) {
        int ret = 0;
        for (int g = 0; g < datageneids.length; g++) {
            String id = datageneids[g];//StringUtil.replace(expr_data.ylabels[g], "\"", "");
            ArrayList tiar = (ArrayList) TIGRrolemap.get(id);
            if (tiar != null)
                for (int h = 0; h < tiar.size(); h++) {
                    String GO = (String) tiar.get(h);
                    if (ti.equals(GO)) {
                        ret++;
                        break;
                    }
                }
        }
        return ret;
    }

    /**
     * @param ti
     * @return
     */
    public int countPath(String ti) {
        int ret = 0;
        for (int g = 0; g < datageneids.length; g++) {
            String id = datageneids[g];//StringUtil.replace(expr_data.ylabels[g], "\"", "");
            ArrayList tiar = (ArrayList) PATHmap.get(id);
            if (tiar != null)
                for (int h = 0; h < tiar.size(); h++) {
                    String s = (String) tiar.get(h);
                    if (ti.equals(s)) {
                        ret++;
                        break;
                    }
                }
        }
        return ret;
    }

    /**
     * @param ti
     * @return
     */
    public int countCOG(String ti) {
        int ret = 0;
        for (int g = 0; g < datageneids.length; g++) {
            String id = datageneids[g];//StringUtil.replace(expr_data.ylabels[g], "\"", "");
            ArrayList tiar = (ArrayList) COGmap.get(id);
            if (tiar != null)
                for (int h = 0; h < tiar.size(); h++) {
                    String COG = (String) tiar.get(h);
                    if (ti.equals(COG)) {
                        ret++;
                        break;
                    }
                }
        }
        return ret;
    }

    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        ParsePath pp = null;

        options = MapArgOptions.maptoMap(args, valid_args);

        String inBIC = null;
        String excludefile = null;

        if (options.get("-debug") != null) {
            String f = (String) options.get("-debug");
            if (f.equalsIgnoreCase("y"))
                debug = true;
            else if (f.equalsIgnoreCase("n"))
                debug = false;
        }

        if (options.get("-outdir") != null) {
            outdir = (String) options.get("-outdir");
            if (!outdir.endsWith("/")) {
                outdir += "/";
            }
        }

        if (options.get("-allpvals") != null) {
            String f = (String) options.get("-allpvals");
            if (f.equalsIgnoreCase("y"))
                do_all_pvals = true;
            else if (f.equalsIgnoreCase("n"))
                do_all_pvals = false;
        }

        if (options.get("-pairs") != null) {
            String f = (String) options.get("-pairs");
            if (f.equalsIgnoreCase("y"))
                pairs = true;
            else if (f.equalsIgnoreCase("n"))
                pairs = false;
        }

        if (options.get("-over") != null) {
            String f = (String) options.get("-over");
            if (f.equalsIgnoreCase("gene"))
                overlap_mode = 2;
            else if (f.equalsIgnoreCase("exp"))
                overlap_mode = 3;
            else if (f.equalsIgnoreCase("gene_exp"))
                overlap_mode = 1;
        }
        if (options.get("-bic") != null) {
            inBIC = (String) options.get("-bic");
            try {
                BIC = ValueBlockListMethods.readAny(inBIC, false);//readAny

            } catch (Exception e) {
                e.printStackTrace();
                if (BIC == null || BIC.size() == 0) {
                    System.out.println("bicluster set failed");
                    System.exit(1);
                }
            }
            pp = new ParsePath(inBIC);
            if (BIC != null) {
                System.out.println("BIC.size() " + BIC.size());
                /*for (int i = 0; i < BIC.size(); i++) {
                    ValueBlock v = (ValueBlock) BIC.get(i);
                    BICmap.put(v.index, i);
                }*/
            } else {
                System.out.println("BIC.bicluster_set is null");
            }
        }

        if (options.get("-goobo") != null) {
            String f = (String) options.get("-goobo");
            String[] obo = TabFile.readtoArrayOne(f, false);
            GOrefMAP = new HashMap();
            for (int i = 0; i < obo.length; i++) {
                //System.out.println("goobo "+i + "\t" + obo[i]);
                if (obo[i].indexOf("[Term]") == 0) {
                    i++;
                    int start = obo[i].indexOf("GO");
                    String goid = obo[i].substring(start, obo[i].length()).toLowerCase();
                    i++;
                    int start2 = obo[i].indexOf(" ");
                    String desc = obo[i].substring(start2 + 1, obo[i].length());
                    GOrefMAP.put(goid, desc);
                    System.out.println("GOrefMAP " + goid + "\t" + desc);
                }
            }
        }

        if (options.get("-sgdxref") != null) {
            String f = (String) options.get("-sgdxref");
            sgdxref_data = TabFile.readtoArray(f);


            for (int i = 0; i < sgdxref_data.length; i++) {
                sgdxref_data[i][3] = sgdxref_data[i][3].toUpperCase();
                sgdxref_data[i][5] = sgdxref_data[i][5].toUpperCase();
                Object n = yeast_gene_names.get(sgdxref_data[i][3]);
                if (n == null) {
                    //System.out.println("adding GO_yeast_data " + GO_yeast_data[i][0] + "\t" + GO_yeast_data[i][1]);
                    if (sgdxref_data[i][5].length() > 0 && sgdxref_data[i][5] != null) {

                        Object testo = yeast_gene_names.get(sgdxref_data[i][3]);
                        if (testo == null && sgdxref_data[i][5].length() > 0) {
                            yeast_gene_names.put(sgdxref_data[i][3], sgdxref_data[i][5]);
                        } else if ((String) testo != sgdxref_data[i][5] && sgdxref_data[i][5].length() > 0) {
                            System.out.println("overwriting " + tab_data[i][7] + "\t" + yeast_gene_names.get(tab_data[i][7]) + "\t" + sgdxref_data[i][5]);
                            yeast_gene_names.put(sgdxref_data[i][3], sgdxref_data[i][5]);
                        }

                        //System.out.println("adding yeast_gene_names 1 " + sgdxref_data[i][3] + "\t" + sgdxref_data[i][5]);
                    } /*else {
                        yeast_gene_names.put(sgdxref_data[i][3], sgdxref_data[i][3]);
                        //out.add(sgdxref_data[i][3] + "\t" + sgdxref_data[i][3]);
                        //System.out.println("adding yeast_gene_names 0 :" + sgdxref_data[i][3] + ":\t:" + sgdxref_data[i][3] + ":");
                    }*/
                }
            }


        }


        if (options.get("-goyeast") != null) {
            String f = (String) options.get("-goyeast");
            GO_yeast_data = TabFile.readtoArray(f);

            GOmap = new HashMap();

            for (int i = 0; i < GO_yeast_data.length; i++) {
                GO_yeast_data[i] = StringUtil.replace(GO_yeast_data[i], "\"", "");
                GO_yeast_data[i][0] = GO_yeast_data[i][0].toUpperCase();
                GO_yeast_data[i][1] = GO_yeast_data[i][1].toUpperCase();


                Object n = yeast_gene_names.get(GO_yeast_data[i][0]);
                if (n == null || ((String) n).length() == 0 || (GO_yeast_data[i][0].equals((String) n) &&
                        GO_yeast_data[i][1] != null && (GO_yeast_data[i][1].length() > 0))) {
                    //System.out.println("adding GO_yeast_data " + GO_yeast_data[i][0] + "\t" + GO_yeast_data[i][1]);
                    yeast_gene_names.put(GO_yeast_data[i][0], GO_yeast_data[i][1]);
                    //System.out.println("adding yeast_gene_names 2 :" + GO_yeast_data[i][0] + ":\t:" + GO_yeast_data[i][1] + ":");
                } else if ((String) n == GO_yeast_data[i][0] && GO_yeast_data[i][1].length() > 0) {
                    yeast_gene_names.put(GO_yeast_data[i][0], GO_yeast_data[i][1]);
                }

                String[] add = removeGO(GO_yeast_data[i]);
                //String[] addone = new String[1];
                //addone[0] = add[4];

                if (add[4] != null && add[4].length() > 0) {

                    Object g = totalGO.get(add[4]);
                    if (g == null) {
                        totalGO.put(add[4], 1);
                    } else {
                        int iv = (Integer) g;
                        totalGO.put(add[4], iv + 1);
                    }

                    //System.out.println("goyeast " + i + "\t" + MoreArray.toString(GO_yeast_data[i], ","));
                    Object o = GOmap.get(GO_yeast_data[i][0]);
                    if (o == null) {
                        ArrayList ar1 = new ArrayList();
                        //ar1.add(addone);
                        ar1.add(add);
                        //GOmap.put(GO_yeast_data[i][0], ar1);
                        GOmap.put(GO_yeast_data[i][0], ar1);

                    /*if (StringUtil.countIndexOf(add, "plasma") > 0) {
                        System.out.println("add FOUND ROGUE plasma");
                        System.out.println(MoreArray.toString(add));
                        System.out.println(MoreArray.toString(GO_yeast_data[i]));
                    }*/

                    } else {
                        boolean islist = false;
                        ArrayList a = null;
                        try {
                            a = (ArrayList) o;
                            islist = true;
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        //if not list convert to list and add next
                        if (!islist) {
                            ArrayList ar = new ArrayList();
                            ////ar.add(o);

                            //ar.add(addone);
                            ar.add(add);
                            GOmap.put(GO_yeast_data[i][0], ar);

                            for (int z = 0; z < ar.size(); z++) {
                                System.out.println("GOmap " + GO_yeast_data[i][0] + "\t" + MoreArray.toString((String[]) ar.get(z), "__"));
                                //System.out.println("GOmap " + GO_yeast_data[i][0] + "\t:" + (String) ar.get(z) + ":");
                            }


                        /*if (StringUtil.countIndexOf(add, "plasma") > 0) {
                            System.out.println("add FOUND ROGUE plasma");
                            System.out.println(MoreArray.toString(add));
                            System.out.println(MoreArray.toString(GO_yeast_data[i]));
                        }*/
                        }
                        //if list add to it
                        else {
                            //a.add(addone);
                            a.add(add);
                            GOmap.put(GO_yeast_data[i][0], a);

                            for (int z = 0; z < a.size(); z++) {
                                System.out.println("GOmap " + GO_yeast_data[i][0] + "\t" + MoreArray.toString((String[]) a.get(z), "__"));
                                //System.out.println("GOmap " + GO_yeast_data[i][0] + "\t:" + (String) a.get(z) + ":");
                            }
                        /*if (StringUtil.countIndexOf(add, "plasma") > 0) {
                            System.out.println("add FOUND ROGUE plasma");
                            System.out.println(MoreArray.toString(add));
                            System.out.println(MoreArray.toString(GO_yeast_data[i]));
                        }*/
                        }
                    }

                    if (GO_yeast_data[i][5].length() > 0)
                        //System.out.println("GO_yeast_data[i][3] " + GO_yeast_data[i][3]);
                        if (GO_yeast_data[i][3].equals("C")) {
                            int ind = MoreArray.getArrayInd(GO_C, GO_yeast_data[i][5]);
                            if (ind == -1) {
                                GO_C.add(GO_yeast_data[i][5]);
                                //System.out.println("adding GO_C " + GO_yeast_data[i][5]);
                            }
                        } else if (GO_yeast_data[i][3].equals("F")) {
                            int ind = MoreArray.getArrayInd(GO_F, GO_yeast_data[i][5]);
                            if (ind == -1) {
                                GO_F.add(GO_yeast_data[i][5]);
                                //System.out.println("adding GO_F " + GO_yeast_data[i][5]);
                            }
                        } else if (GO_yeast_data[i][3].equals("P")) {
                            int ind = MoreArray.getArrayInd(GO_P, GO_yeast_data[i][5]);
                            if (ind == -1) {
                                GO_P.add(GO_yeast_data[i][5]);
                                //System.out.println("adding GO_P " + GO_yeast_data[i][5]);
                            }
                        }
                }
            }

            System.out.println("GOmap " + GOmap.size());
        }

        if (options.get("-TFEXP") != null) {
            String f = (String) options.get("-TFEXP");
            TFEXP_data = new SimpleMatrix(f);
        }
        if (options.get("-TFREF") != null) {
            String f = (String) options.get("-TFREF");
            TFREF_data = new SimpleMatrix(f);

            TFREF_data.xlabels = StringUtil.toUpper(StringUtil.replace(TFREF_data.xlabels, "\"", ""));
            TFREF_data.ylabels = StringUtil.toUpper(StringUtil.replace(TFREF_data.ylabels, "\"", ""));
            //System.out.println("motif_data.xlabels");
            //MoreArray.printArray(motif_data.xlabels);
        }
        if (options.get("-expr") != null) {
            String f = (String) options.get("-expr");
            expr_data = new SimpleMatrix(f);

            expr_data.xlabels = StringUtil.replace(expr_data.xlabels, "\"", "");
            expr_data.ylabels = StringUtil.replace(expr_data.ylabels, "\"", "");

            //genes
            datagenemax = expr_data.data.length;
            //exps
            dataexpmax = expr_data.data[0].length;

            datageneids = expr_data.ylabels;

        }
        if (options.get("-mode") != null) {
            String f = (String) options.get("-mode");
            mode = f;
            if (StringUtil.getFirstEqualsIndex(modes, mode) == -1) {
                mode = "member";
                System.out.println("setting mode to default " + mode);
            }
        }
        if (options.get("-cut") != null) {
            String f = (String) options.get("-cut");
            block_overlap_cutoff = Double.parseDouble(f);
        }
        if (options.get("-pval") != null) {
            String f = (String) options.get("-pval");
            TF_pvalue_cutoff = Double.parseDouble(f);
            GO_pvalue_cutoff = TF_pvalue_cutoff;
            TIGR_pvalue_cutoff = TF_pvalue_cutoff;
            TIGRrole_pvalue_cutoff = TF_pvalue_cutoff;
            Path_pvalue_cutoff = TF_pvalue_cutoff;
            COG_pvalue_cutoff = TF_pvalue_cutoff;
            GMT_pvalue_cutoff = TF_pvalue_cutoff;
            System.out.println("custom p-value " + TF_pvalue_cutoff);
        }
        if (options.get("-addcoreg") != null) {
            String f = (String) options.get("-addcoreg");
            addCoRegCases = f.equalsIgnoreCase("y") ? true : false;
            System.out.println("custom p-value " + TF_pvalue_cutoff);
        }
        if (options.get("-exclude") != null) {
            String f = (String) options.get("-exclude");
            exclude_genes = TextFile.readtoArray(f);

            ParsePath p = new ParsePath(f);
            excludefile = p.getFile();
        }


        if (pp != null) {
            prefix = pp.getName() + "_pval" + TF_pvalue_cutoff + "_cut_" + block_overlap_cutoff + "_";
            String exc = "";
            if (exclude_genes != null) {
                exc = "_exclude_";
                prefix += exc;
            }

        }

        System.out.println("cutoffs: block_overlap_cutoff " + block_overlap_cutoff + "\tTF_pvalue_cutoff " + TF_pvalue_cutoff +
                "\tTIGR_pvalue_cutoff" + TIGR_pvalue_cutoff + "\tGO_pvalue_cutoff " + GO_pvalue_cutoff);

        //
        if (exclude_genes != null) {
            ArrayList translateids = new ArrayList();
            for (int i = 0; i < exclude_genes.length; i++) {
                int index = StringUtil.getFirstEqualsIndex(datageneids, exclude_genes[i]);
                if (index != -1) {
                    translateids.add(index + 1);
                }
            }
            int[] exclude_genes_int = MoreArray.ArrayListtoInt(translateids);

            for (int i = 0; i < BIC.size(); i++) {
                ValueBlock v = (ValueBlock) BIC.get(i);
                ArrayList newgenes = new ArrayList();
                for (int j = 0; j < v.genes.length; j++) {
                    try {
                        int index = MoreArray.getArrayInd(exclude_genes_int, v.genes[j]);
                        if (index == -1) {
                            newgenes.add(v.genes[j]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                v.genes = MoreArray.ArrayListtoInt(newgenes);
                //keep only if bicluster af exclusion has >= 2 genes
                if (v.genes.length >= 2) {
                    v.updateArea();
                    BIC.set(i, v);
                } else {
                    BIC.remove(i);
                    i--;
                    System.out.println("bicluster too small after excluding " + v.genes.length + "\t" + BIC.size());
                }
            }

            String s1 = outdir + inBIC + "_EXCLUDE_" + excludefile;
            String out1 = BIC.toString(BIC.header != null ? BIC.header : "#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            System.out.println("writing " + s1);
            util.TextFile.write(out1, s1);
        }


        if (options.get("-geneids") != null) {
            try {
                String[][] sarray = TabFile.readtoArray((String) options.get("-geneids"));
                System.out.println("datageneids g " + sarray.length + "\t" + sarray[0].length);
                int col = 2;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                datageneids = MoreArray.replaceAll(n, "\"", "");
                System.out.println("datageneids gene " + datageneids.length + "\t" + datageneids[0]);
            } catch (Exception e) {
                System.out.println("expecting 2 cols");
                e.printStackTrace();
            }
        }

        if (options.get("-cogcode") != null) {
            try {
                String[][] sarray = TabFile.readtoArray((String) options.get("-cogcode"));
                COGcodemap = new HashMap();
                for (int i = 0; i < sarray.length; i++) {
                    COGcodemap.put(sarray[i][0], sarray[i][1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (options.get("-goref") != null) {
            try {
                String[][] sarray = TabFile.readtoArray((String) options.get("-goref"));
                System.out.println("goref " + sarray.length + "\t" + sarray[0].length);

                GOrefMAP = new HashMap();
                for (int i = 0; i < sarray.length; i++) {
                    String addStr = removeGO(sarray[i][0]);
                    String addStr2 = removeGO(sarray[i][2]);
                    //System.out.println("************");
                    //System.out.println(addStr);
                    //System.out.println(addStr2);
                    if (addStr != null && addStr.length() > 0 && addStr2 != null && addStr2.length() > 0) {
                        //System.out.println("IN IF");
                        //String[] add = removeGO(GO_yeast_data[i]);
                        /*Object g = totalGO.get(addStr2);
                        if (g == null) {
                            totalGO.put(addStr2, 1);
                        } else {
                            int iv = (Integer) g;
                            totalGO.put(addStr2, iv + 1);
                        }*/
                        //System.out.println(sarray[i][0]);
                        //System.out.println(sarray[i][2]);
                        GOrefMAP.put(sarray[i][0], sarray[i][2]);
                    }
                    //System.out.println("&&&&&&&&");
                }
                System.out.println("goref " + datageneids.length + "\t" + datageneids[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (options.get("-geneMapTab") != null) {
            String f = (String) options.get("-geneMapTab");
            geneMapTab_data = TabFile.readtoArray(f);

            System.out.println("read geneMapTab data " + geneMapTab_data.length + "\t" + geneMapTab_data[0].length);
            GMT_data = new String[geneMapTab_data.length][2];
            GMTmap = new HashMap();

            GMT_header = geneMapTab_data[0][1];

            // cellular_component, cell, intracellular,
            String[] go_exclude = {"GO:0005575", "GO:0005623", "GO:0005622"};
            String[] go_exclude_labels = {"cellular_component", " Cell", "Intracellular"};

            for (int i = 1; i < geneMapTab_data.length; i++) {
                geneMapTab_data[i] = StringUtil.replace(geneMapTab_data[i], "\"", "");

                GMT_data[i][0] = geneMapTab_data[i][0];
                GMT_data[i][1] = geneMapTab_data[i][1];

                if (StringUtil.getFirstEqualsIndex(go_exclude_labels, GMT_data[i][1]) == -1 &&
                        geneMapTab_data[i][1] != null && geneMapTab_data[i][1].length() > 0) {
                    Object o = GMTmap.get(geneMapTab_data[i][0]);
                    String termsStrings = geneMapTab_data[i][1];

                    //if (!trimRole.equals("cellular processes") && !trimRole.equals("metabolic process")) {
                    //String[] terms = termsStrings.split("\\s*_|_\\s*");
                    String[] terms = termsStrings.split("_|_");
                    ArrayList add = new ArrayList();
                    for (int x = 0; x < terms.length; x++) {
                        String term = terms[x].trim();
                        Object g = totalGMT.get(term);
                        if (g == null) {
                            totalGMT.put(term, 1);
                        } else {
                            int iv = (Integer) g;
                            totalGMT.put(term, iv + 1);
                        }
                        if (!add.contains(term))
                            add.add(term);
                    }
                    if (o == null) {
                        if (debug)
                            System.out.println("GMTmap *" + geneMapTab_data[i][0] + "*\t*" + add + "*");
                        GMTmap.put(geneMapTab_data[i][0], add);
                    }
                }
            }

            System.out.println("GMTmap " + GMTmap.size());
            System.out.println("totalGMT " + totalGMT.size());

            geneids_gmt = new String[geneMapTab_data.length - 1];
            genenames_gmt = new String[geneMapTab_data.length - 1];
            for (int i = 1; i < geneMapTab_data.length; i++) {
                geneids_gmt[i - 1] = geneMapTab_data[i][0]; // was 7
                genenames_gmt[i - 1] = geneMapTab_data[i][1]; // was 8
            }

            //System.out.println("genenames");
            //System.out.println(MoreArray.toString(genenames_gmt, ","));
        }

        if (options.get("-tab") != null) {
            String f = (String) options.get("-tab");
            tab_data = TabFile.readtoArray(f);

            System.out.println("read tab_data " + tab_data.length + "\t" + tab_data[0].length);
            TIGR_data = new String[tab_data.length][3];
            TIGRmap = new HashMap();
            TIGRrolemap = new HashMap();

            if (GOmap == null) {
                GOmap = new HashMap();
                doGO = true;
            }
            System.out.println("GOmap size " + GOmap.size() + "\t" + doGO);
            /*if (PATHmap == null) {
                PATHmap = new HashMap();
                doPATH = true;
            }*/
            //RAUF CODE CHANGE ORIGINAL CODE WAS:
            //if (COGmap == null && yeast_gene_names == null) {
            if (COGmap == null && options.get("-cogcode") != null) {
                COGmap = new HashMap();
                doCOG = true;
            }

            for (int i = 1; i < tab_data.length; i++) {
                tab_data[i] = StringUtil.replace(tab_data[i], "\"", "");

                Object testo = yeast_gene_names.get(tab_data[i][7]);
                if (testo == null && tab_data[i][8].length() > 0) {
                    yeast_gene_names.put(tab_data[i][7], tab_data[i][8]);
                } else if ((String) testo == tab_data[i][7] && tab_data[i][8].length() > 0) {
                    System.out.println("overwriting " + tab_data[i][7] + "\t" + yeast_gene_names.get(tab_data[i][7]) + "\t" + tab_data[i][8]);
                    yeast_gene_names.put(tab_data[i][7], tab_data[i][8]);
                }

                TIGR_data[i][0] = tab_data[i][7];
                TIGR_data[i][1] = tab_data[i][13];
                TIGR_data[i][2] = tab_data[i][14];
                if (tab_data[i][13] != null && tab_data[i][13].length() > 0) {
                    Object o = TIGRmap.get(tab_data[i][7]);
                    if (o == null) {
                        ArrayList add = new ArrayList();
                        add.add(tab_data[i][13]);
                        TIGRmap.put(tab_data[i][0], add);
                        TIGRmap.put(tab_data[i][7], add);
                        System.out.println("adding TIGR 1 " + tab_data[i][0] + "\t*" + tab_data[i][13] + "*");
                        System.out.println("adding TIGR 1 " + tab_data[i][7] + "\t*" + tab_data[i][13] + "*");

                    } /*else {
                        ArrayList add = (ArrayList) o;
                        add.add(tab_data[i][13]);
                        TIGRmap.put(tab_data[i][0], add);
                        TIGRmap.put(tab_data[i][7], add);
                        //System.out.println("adding TIGR 2 " + tab_data[i][7] + "\t*" + tab_data[i][13] + "*");
                    }*/
                }
                if (tab_data[i][14] != null && tab_data[i][14].length() > 0) {
                    Object o2 = TIGRrolemap.get(tab_data[i][0]);
                    Object o3 = TIGRrolemap.get(tab_data[i][7]);
                    int index = tab_data[i][14].indexOf(":");

                    String trimRole = "";
                    if (index == -1) {
                        trimRole = tab_data[i][14];
                    } else {
                        trimRole = tab_data[i][14].substring(0, index);
                    }
                    // String trimRole = tab_data[i][14].substring(0, index); // RAUF'S CHANGE BECAUSE IT WAS CAUSING ERROR DUE TO INDEX BEING -1
                    if (!trimRole.equals("cellular processes") && !trimRole.equals("metabolic process")) {
                        Object g = totalTIGRrole.get(trimRole);
                        if (g == null) {
                            totalTIGRrole.put(trimRole, 1);
                        } else {
                            int iv = (Integer) g;
                            totalTIGRrole.put(trimRole, iv + 1);
                        }

                        if (o2 == null && o3 == null) {
                            ArrayList add = new ArrayList();
                            add.add(trimRole);

                            TIGRrolemap.put(tab_data[i][0], add);
                            TIGRrolemap.put(tab_data[i][7], add);

                            System.out.println("adding TIGRrole 1 " + tab_data[i][0] + "\t*" + tab_data[i][14] + "*");
                            System.out.println("adding TIGRrole 1 " + tab_data[i][7] + "\t*" + tab_data[i][14] + "*");
                        } /*else {
                        ArrayList add = (ArrayList) o2;
                        add.add(trimRole);
                        TIGRrolemap.put(tab_data[i][0], add);
                        TIGRrolemap.put(tab_data[i][7], add);
                    }*/
                    }
                }
                if (doCOG && tab_data[i][11] != null && tab_data[i][11].length() > 0 && options.get("-cogcode") != null) {

                    Object o2 = COGmap.get(tab_data[i][0]);
                    Object o3 = COGmap.get(tab_data[i][7]);
                    if (o2 == null && o3 == null) {
                        ArrayList add = new ArrayList();
                        for (int a = 0; a < tab_data[i][11].length(); a++) {
                            String s = "" + tab_data[i][11].charAt(a);
                            System.out.println(s);
                            s = (String) COGcodemap.get(s);
                            //System.out.println(s);
                            add.add(s);
                            Object g = totalCOG.get(s);
                            if (g == null) {
                                totalCOG.put(s, 1);
                            } else {
                                int iv = (Integer) g;
                                totalCOG.put(s, iv + 1);
                            }
                        }

                        //add.add(tab_data[i][11]);
                        COGmap.put(tab_data[i][0], add);
                        COGmap.put(tab_data[i][7], add);

                    } /*else {
                        ArrayList add = (ArrayList) o2;
                        add.add(tab_data[i][11]);
                        COGmap.put(tab_data[i][0], add);
                        COGmap.put(tab_data[i][7], add);
                    }*/
                }

                if (doGO && tab_data[i][15] != null && tab_data[i][15].length() > 0) {
                    String addStr = removeGO(tab_data[i][15].toLowerCase());

                    if (addStr != null && addStr.length() > 0) {

                     /*   Object g = totalGO.get(addStr);
                        if (g == null) {
                            totalGO.put(addStr, 1);
                        } else {
                            int iv = (Integer) g;
                            totalGO.put(addStr, iv + 1);
                        }*/

                        Object o2 = GOmap.get(tab_data[i][0]);
                        Object o3 = GOmap.get(tab_data[i][7]);
                        if (o2 == null && o3 == null) {
                            ArrayList add = new ArrayList();
                            String[] split = addStr.split(",");
                            if (GOrefMAP != null) {
                                for (int a = 0; a < split.length; a++) {
                                    String splitadd = (String) GOrefMAP.get(split[0].toUpperCase());
                                   /* System.out.println("HEYYYYY");
                                    System.out.println(splitadd);
                                    System.out.println(split[0]);
                                    System.out.println("---------------------------");*/
                                    Object g = totalGO.get(splitadd);
                                    if (g == null) {
                                        totalGO.put(splitadd, 1);
                                    } else {
                                        int iv = (Integer) g;
                                        totalGO.put(splitadd, iv + 1);
                                    }

                                    //System.out.println("splitadd " + splitadd);
                                    if (add.indexOf(splitadd) == -1)
                                        add.add(splitadd);
                                    //add.add(addStr);
                                }
                            } /*else {
                                for (int a = 0; a < split.length; a++) {
                                    String splitadd = (String) GOrefMAP.get(split[0]);

                                    Object g = totalGO.get(splitadd);
                                    if (g == null) {
                                        totalGO.put(splitadd, 1);
                                        System.out.println("totalGO " +splitadd+ "\t" + 1);
                                    } else {
                                        int iv = (Integer) g;
                                        totalGO.put(splitadd, iv + 1);
                                        System.out.println("totalGO " + splitadd + "\t" + (iv + 1));
                                    }
                                }
                            }*/
                            //System.out.println("+tab_data[i][0] " + tab_data[i][0] + "\t" + tab_data[i][7]);
                            GOmap.put(tab_data[i][0], add);
                            GOmap.put(tab_data[i][7], add);


                            //System.out.println("doGO GOmap1 " + tab_data[i][0] + "\t" + MoreArray.arrayListtoString(add, "__"));
                            System.out.println("doGO GOmap2 " + tab_data[i][7] + "\t" + MoreArray.arrayListtoString(add, "__"));

                            //System.out.println("adding 1 GOmap " + tab_data[i][0] + "\t" + addStr);
                            //System.out.println("adding 1 GOmap " + tab_data[i][7] + "\t" + addStr);
                        } /*else {
                            ArrayList add = (ArrayList) o2;
                            add.add(addStr);
                            GOmap.put(tab_data[i][0], add);
                            GOmap.put(tab_data[i][7], add);
                            System.out.println("adding 2 GOmap " + tab_data[i][0] + "\t" + addStr);
                        }*/
                    }
                }

                /*if (doPATH && tab_data[i][16] != null && tab_data[i][16].length() > 0) {

                    Object g = totalPath.get(tab_data[i][16]);
                    if (g == null) {
                        totalPath.put(tab_data[i][16], 1);
                    } else {
                        int iv = (Integer) g;
                        totalPath.put(tab_data[i][16], iv + 1);
                    }

                    Object o2 = PATHmap.get(tab_data[i][0]);
                    Object o3 = PATHmap.get(tab_data[i][7]);
                    if (o2 == null && o3 == null) {
                        ArrayList add = new ArrayList();
                        add.add(tab_data[i][16]);
                        PATHmap.put(tab_data[i][0], add);
                        PATHmap.put(tab_data[i][7], add);
                        System.out.println("adding 1 PATHmap " + tab_data[i][0] + "\t" + tab_data[i][16]);
                    } *//*else {
                        ArrayList add = (ArrayList) o2;
                        add.add(tab_data[i][16]);
                        PATHmap.put(tab_data[i][0], add);
                        PATHmap.put(tab_data[i][7], add);
                        System.out.println("adding 2 PATHmap " + tab_data[i][0] + "\t" + tab_data[i][16]);
                    }*//*
                }*/

            }

            geneids = new String[tab_data.length - 1];
            genenames = new String[tab_data.length - 1];
            for (int i = 1; i < tab_data.length; i++) {
                geneids[i - 1] = tab_data[i][7];
                genenames[i - 1] = tab_data[i][8];
            }

            //System.out.println("genenames");
            //System.out.println(MoreArray.toString(genenames, ","));
        }

        if (options.get("-PATH") != null) {
            String f = (String) options.get("-PATH");
            String[][] tab_data2 = TabFile.readtoArray(f, true);
            //ArrayList tabdata = TextFile.readtoList(f);

            // System.out.println("read tab_data2 " + f + "\t" + tabdata.size());// + "\t" + tabdata[0].length);
            System.out.println("read tab_data2 " + f + "\t" + tab_data2.length + "\t" + tab_data2[0].length);
            PATHmap = new HashMap();
            //for (int i = 0; i < tabdata.size(); i++) {
            for (int i = 0; i < tab_data2.length; i++) {
                //String[] tab_data2 = ((String) tab_data2.get(i)).split("\t");

                //System.out.println("PATHmap " + i + "\t" + MoreArray.toString(tab_data2[i], ","));
                if (tab_data2[i].length > 3) {
                    tab_data2[i] = StringUtil.replace(tab_data2[i], "\"", "");
                    //System.out.println("HELL0");
                    //System.out.println(tab_data2[i]);
                    //String name = tab_data2[i][3];
                    if (tab_data2[i][3] != null) {
                        //System.out.println(tab_data2[i][3]);
                        int index = StringUtil.getFirstEqualsIndex(genenames, tab_data2[i][3]);
                        //System.out.println("PATHmap " + name + "\t" + index);
                        if (index != -1) {
                            String yid = geneids[index];
                            if (tab_data2[i][0] != null && tab_data2[i][0].length() > 0) {

                                Object g = totalPath.get(tab_data2[i][0]);
                                if (g == null) {
                                    totalPath.put(tab_data2[i][0], 1);
                                } else {
                                    int iv = (Integer) g;
                                    totalPath.put(tab_data2[i][0], iv + 1);
                                }

                                Object o2 = PATHmap.get(yid);
                                if (o2 == null) {
                                    ArrayList add = new ArrayList();
                                    add.add(tab_data2[i][0]);
                                    PATHmap.put(yid, add);
                                    System.out.println("PATHmap adding 1 " + yid + "\t" + tab_data2[i][0]);
                                } else {
                                    ArrayList add = (ArrayList) o2;
                                    if (add.indexOf(tab_data2[i][0]) == -1) {
                                        add.add(tab_data2[i][0]);
                                        PATHmap.put(yid, add);
                                        System.out.println("PATHmap adding 2 " + yid + "\t" + tab_data2[i][0]);
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("PATHmap name is null " + i + "\t" + tab_data2[i][3] + "\t" + MoreArray.toString(tab_data2[i], ","));
                    }
                }
            }
        }

        if (TFREF_data == null) {
            doTFREF = false;
        } else
            doTFREF = true;
        if (TFEXP_data == null) {
            doTFEXP = false;
        } else
            doTFEXP = true;
        if (PATHmap == null) {
            doPATH = false;
        } else
            doPATH = true;
        if (GOmap == null) {
            doGO = false;
        } else
            doGO = true;
        if (COGmap == null) {
            doCOG = false;
        } else
            doCOG = true;
        if (TIGRrolemap == null || TIGRmap == null) {
            doTIGR = false;
        } else
            doTIGR = true;
        if (GMTmap == null) {
            doGMT = false;
        } else
            doGMT = true;

        if (datageneids == null)
            datageneids = expr_data.ylabels;

        System.out.println("init sizes " + doGO + "\t" + doPATH + "\t" + doTIGR + "\t" + doCOG + "\t" + doTIGR + "\t" + TFREF_data + "\t" + doTFEXP);
        System.out.println("init sizes GO " + (GOmap != null ? GOmap.size() : 0));
        System.out.println("init sizes PATH " + (PATHmap != null ? PATHmap.size() : 0));
        System.out.println("init sizes TIGR " + (TIGRmap != null ? TIGRmap.size() : 0));
        System.out.println("init sizes COG " + (COGmap != null ? COGmap.size() : 0));
        System.out.println("init sizes TIGRrole " + (TIGRrolemap != null ? TIGRrolemap.size() : 0));
        System.out.println("init sizes TFREF " + (TFREF_data != null ? TFREF_data.data.length : 0));
        System.out.println("init sizes TFEXP " + (TFEXP_data != null ? TFEXP_data.data.length : 0));


       /* Set keys = yeast_gene_names.keySet();
        Iterator iter = keys.iterator();
        Object next = iter.next();
        int count = 0;
        while (next != null) {
            //for (int i = 0; i < keys.size(); i++) {
            System.out.println("yeast_gene_names keys " + count + "\t" + next + "\t" + yeast_gene_names.get(next));
            try {
                next = iter.next();
                count++;
            } catch (Exception e) {
                next = null;
            }
        }*/


      /*  int data_to_GMTmap_size = 0;
        int data_to_GOmap_size = 0;
        int data_to_COGmap_size = 0;
        int data_to_Pathmap_size = 0;
        int data_to_TIGRmap_size = 0;*/

        if (GMTmap != null) {
            data_to_GMTmap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {
                Object o = GMTmap.get(datageneids[i]);
                if (o != null && ((ArrayList) o).size() > 0)
                    data_to_GMTmap_size++;
                else {
                    System.out.println("GMTmap no " + datageneids[i]);
                    ArrayList add = new ArrayList();
                    String s = "GMT:" + MINER_STATIC.UNLABELED;
                    add.add(s);
                    //if (debug)
                    //    System.out.println("GMTmap " + s);
                    GMTmap.put(datageneids[i], add);
                }
            }

            System.out.println("data_to_GMTmap_size " + data_to_GMTmap_size);
            System.out.println("data_to_GMTmap_size unmapped " + (datageneids.length - data_to_GMTmap_size));
            data_to_GMTmap_size = datageneids.length;
            System.out.println("data_to_GMTmap_size final " + data_to_GMTmap_size);
        }

        if (GOmap != null) {

            data_to_GOmap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {
                Object o = GOmap.get(datageneids[i]);
                if (o != null && ((ArrayList) o).size() > 0)
                    data_to_GOmap_size++;
                else {
                    System.out.println("GOmap no " + datageneids[i]);
                    ArrayList add = new ArrayList();
                    String s = "GO:" + MINER_STATIC.UNLABELED;
                    add.add(s);
                    GOmap.put(datageneids[i], add);
                }
            }


            Set keys = GOmap.keySet();
            ArrayList outGOstr = new ArrayList();

            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                //System.out.println();
                String key = (String) iter.next();
                outGOstr.add(key + "\t" + GOmap.get(key));

            }

            String outpath = "GO_internal_map.txt";
            TextFile tf = new TextFile();
            TextFile.write(outGOstr, outpath);

            System.out.println("wrote omterma; GO mapping");

            System.out.println("data_to_GOmap_size " + data_to_GOmap_size);
            System.out.println("data_to_GOmap_size unmapped " + (datageneids.length - data_to_GOmap_size));
            data_to_GOmap_size = datageneids.length;
            System.out.println("data_to_GOmap_size final " + data_to_GOmap_size);
        }

        if (COGmap != null) {

            data_to_COGmap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {
                Object o = COGmap.get(datageneids[i]);
                if (o != null && ((ArrayList) o).size() > 0)
                    data_to_COGmap_size++;
                else {
                    ArrayList add = new ArrayList();
                    String s = "COG:" + MINER_STATIC.UNLABELED;
                    add.add(s);
                    COGmap.put(datageneids[i], add);
                }
            }

            System.out.println("data_to_COGmap_size " + data_to_COGmap_size);
            System.out.println("data_to_COGmap_size unmapped " + (datageneids.length - data_to_COGmap_size));
            data_to_COGmap_size = datageneids.length;
            System.out.println("data_to_COGmap_size final " + data_to_COGmap_size);
        }

        if (TIGRmap != null) {

            data_to_TIGRmap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {
                Object o = TIGRmap.get(datageneids[i]);
                if (o != null && ((ArrayList) o).size() > 0)
                    data_to_TIGRmap_size++;
                else {
                    System.out.println("TIGRmap no " + datageneids[i]);
                    ArrayList add = new ArrayList();
                    String s = "TIGR:" + MINER_STATIC.UNLABELED;
                    add.add(s);
                    TIGRmap.put(datageneids[i], add);
                }
            }

            System.out.println("data_to_TIGRmap_size " + data_to_TIGRmap_size);
            System.out.println("data_to_TIGRmap_size unmapped " + (datageneids.length - data_to_TIGRmap_size));
            data_to_TIGRmap_size = datageneids.length;
            System.out.println("data_to_TIGRmap_size final " + data_to_TIGRmap_size);
        }

        if (TIGRrolemap != null) {

            data_to_TIGRrolemap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {
                Object o = TIGRrolemap.get(datageneids[i]);
                if (o != null && ((ArrayList) o).size() > 0)
                    data_to_TIGRrolemap_size++;
                else {
                    System.out.println("TIGRrolemap no " + datageneids[i]);
                    ArrayList add = new ArrayList();
                    String s = "TIGRrole:" + MINER_STATIC.UNLABELED;
                    add.add(s);
                    TIGRrolemap.put(datageneids[i], add);
                }
            }

            System.out.println("data_to_TIGRrolemap_size " + data_to_TIGRrolemap_size);
            System.out.println("data_to_TIGRrolemap_size unmapped " + (datageneids.length - data_to_TIGRrolemap_size));
            data_to_TIGRrolemap_size = datageneids.length;
            System.out.println("data_to_TIGRrolemap_size final " + data_to_TIGRrolemap_size);
        }

        if (PATHmap != null) {
            data_to_Pathmap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {
                Object o = PATHmap.get(datageneids[i]);
                if (o != null && ((ArrayList) o).size() > 0)
                    data_to_Pathmap_size++;
                else {
                    System.out.println("PATHmap no " + datageneids[i]);
                    ArrayList add = new ArrayList();
                    String s = "Path:" + MINER_STATIC.UNLABELED;
                    add.add(s);
                    PATHmap.put(datageneids[i], add);
                }
            }

            System.out.println("data_to_Pathmap_size " + data_to_Pathmap_size);
            System.out.println("data_to_Pathmap_size unmapped " + (datageneids.length - data_to_Pathmap_size));
            data_to_Pathmap_size = datageneids.length;
            System.out.println("data_to_Pathmap_size final " + data_to_Pathmap_size);
        }


        TFRrefmap = new HashMap();

        if (TFREF_data != null) {

            data_to_TFmap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {

                int search = StringUtil.getFirstIndexOf(datageneids[i], TFREF_data.xlabels);
                if (search != -1) {
                    data_to_TFmap_size++;
                    TFRrefmap.put(datageneids[i], "TFref:Labeled");
                } else {
                    Object o = yeast_gene_names.get(datageneids[i]);
                    if (o != null)
                        search = StringUtil.getFirstIndexOf((String) o, TFREF_data.xlabels);
                    if (search != -1) {
                        data_to_TFmap_size++;
                        TFRrefmap.put(datageneids[i], "TFref:Labeled");
                    } else {
                        TFRrefmap.put(datageneids[i], "TFref:" + MINER_STATIC.UNLABELED);
                        //System.out.println("TFref unmapped " + datageneids[i]);
                    }
                }

            }
            System.out.println("data_to_TFmap_size " + data_to_TFmap_size);
            System.out.println("data_to_TFmap_size unmapped TF " + (datageneids.length - data_to_TFmap_size));
            data_to_TFmap_size = datageneids.length;
            System.out.println("data_to_TFmap_size final " + data_to_TFmap_size);
        }


          /* TODO implement for TFs */
        /*
        if (TFpairmap != null) {
            //datageneids

            data_to_TFpairmap_size = 0;
            for (int i = 0; i < datageneids.length; i++) {
                Object o = TFpairmap.get(datageneids[i]);
                if (o != null)
                    data_to_TFpairmap_size++;

            }
        }*/


        ArrayList out = new ArrayList();
        for (int i = 0; i < expr_data.ylabels.length; i++) {
            //System.out.println("expr_data.ylabels " + i + "\t:" + expr_data.ylabels[i]
            //        + ":\t" + yeast_gene_names.get(expr_data.ylabels[i]));
            Object o = yeast_gene_names.get(expr_data.ylabels[i]);
            out.add(o != null ? o : expr_data.ylabels[i]);
        }

        TabFile.write(MoreArray.ArrayListtoString(out), outdir + "genenames.txt");
        //System.exit(0);
    }


    /**
     * @param g
     * @return
     */
    public final static String[] removeGO(String[] g) {

        g = StringUtil.replace(g, "structural molecule activity", "");
        g = StringUtil.replace(g, "molecular_function", "");
        g = StringUtil.replace(g, "biological_process", "");
        g = StringUtil.replace(g, "GO:0008150", "");
        g = StringUtil.replace(g, "GO:0003674", "");
        g = StringUtil.replace(g, "GO:0005198", "");
        g = StringUtil.replace(g, "ORF|Verified", "");
        g = StringUtil.replace(g, "ORF|Dubious", "");
        g = StringUtil.replace(g, "other", "");
        g = StringUtil.replace(g, "not_yet_annotated", "");

        g = StringUtil.replace(g, ",,", ",");

        //g = StringUtil.replace(g, "nucleus", "");
        //g = StringUtil.replace(g, "mitochondrion", "");

        //if(g.indexOf("plasma membrane") == -1)

        //g = StringUtil.replace(g, "membrane", "");

        /* ArrayList ar = MoreArray.convtoArrayList(g);
     ar.remove("structural molecule activity");
     ar.remove("molecular_function");
     ar.remove("biological_process");
     ar.remove("GO:0008150");
     ar.remove("GO:0003674");
     ar.remove("GO:0005198");
     ar.remove("ORF|Verified");
     ar.remove("ORF|Dubious");
     String[] add = MoreArray.ArrayListtoString(ar);*/
        return g;
    }

    /**
     * @param g
     * @return
     */
    public final static String removeGOequals(String g) {

        String[] filter = {
                "structural molecule activity",
                "molecular_function",
                "biological_process",
                "GO:0008150",
                "GO:0003674",
                "GO:0005198",
                "ORF|Verified",
                "ORF|Dubious",
                "other",
                "not_yet_annotated",

                "cellular component",
                "cellular_component",
                "cellularcomponent",
                //"cytoplasm",
                //"nucleus",
                //"mitochondrion",
                //"membrane"
        };

        int ind = StringUtil.getFirstEqualsIndex(filter, g);
        if (ind != -1)
            g = "";
        return g;
    }

    /**
     * @param g
     * @return
     */
    public final static String removeGO(String g) {

        g = StringUtil.replace(g, "structural molecule activity", "");
        g = StringUtil.replace(g, "molecular_function", "");
        g = StringUtil.replace(g, "biological_process", "");
        g = StringUtil.replace(g, "GO:0008150", "");
        g = StringUtil.replace(g, "GO:0003674", "");
        g = StringUtil.replace(g, "GO:0005198", "");
        g = StringUtil.replace(g, "ORF|Verified", "");
        g = StringUtil.replace(g, "ORF|Dubious", "");
        g = StringUtil.replace(g, "other", "");
        g = StringUtil.replace(g, "not_yet_annotated", "");
        g = StringUtil.replace(g, ",,", ",");

        if (g.startsWith(","))
            g = g.substring(1);
        if (g.endsWith(","))
            g = g.substring(0, g.length() - 1);
        return g;
    }


    /**
     * @param cmd
     * @param scriptbox
     * @param scriptname
     */
    private void runCmd(String cmd, String scriptbox, String scriptname) {

        TextFile.write(cmd, scriptbox + scriptname);
        Process p = null;

        try {
            p = Runtime.getRuntime().exec("bash " + scriptbox + scriptname + " &> " + scriptbox + scriptname + ".out");
            p.waitFor();
        } catch (Exception ex) {
            if (p != null) p.destroy();
            System.out.println(ex);
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 6 && args.length <= 44) {
            BuildGraph rm = new BuildGraph(args);
        } else {
            System.out.println("syntax: java DataMining.func.BuildGraph\n" +
                    "<-bic valueblock list>\n" +
                    "<-expr expr data path>\n" +
                    "<-mode mode = tf,COG,member>\n" +
                    "<-cut OPTIONAL block_overlap_cutoff = block_overlap_cutoff all edges below, default = 0>\n" +
                    "<-pval OPTIONAL p-value cutoff, default = 0.1>\n" +
                    "<-tab OPTIONAL tab genome annotation file>\n" +
                    "<-geneMapTab OPTIONAL tab seperated file which maps genes to arbitrary term from a classification.>\n" +
                    "<-goyeast OPTIONAL GO yeast annotation file>\n" +
                    "<-TFEXP OPTIONAL TF-gene experimental data file>\n" +
                    "<-TFREF OPTIONAL TF-gene mapping file>\n" +
                    "<-PATH OPTIONAL pathway-gene mapping file>\n" +
                    "<-over OPTIONAL \"gene_exp\",\"gene\",\"exp\", default \"gene_exp\">\n" +
                    "<-addcoreg y/n add TF coregulation cases>\n" +
                    "<-exclude OPTIONAL list of genes to exclude>\n" +
                    "<-pairs OPTIONAL test pairs of TFs>\n" +
                    "<-geneids OPTIONAL>\n" +
                    "<-goref OPTIONAL>\n" +
                    "<-cogcode OPTIONAL>\n" +
                    "<-sgdxref OPTIONAL>\n" +
                    "<-goobo OPTIONAL>\n" +
                    "<-outdir OPTIONAL>\n" +
                    "<-debug OPTIONAL>"
            );
        }
    }
}