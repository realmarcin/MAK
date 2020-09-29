package DataMining.eval;

import DataMining.*;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 12/15/12
 * Time: 8:28 PM
 */
public class EvalCOALESCE {

    String[] repsbare = {"00", "01", "02", "03", "04"};


    public final static String header =
            "label\treplicate\trefbicluster\tbicluster\tfirst re. count\tlast ref. count\tregulon_size\t" +
                    "first ref. ratio\tlast ref. ratio\tnum genes first\tnum genes last\tpercent num genes last\t" +
                    "num exps first\tnum exps last\tpercent exp last\tlast.percentOrigGenes\t" +
                    "last.percentOrigExp\tfirst.precrit\tlast.precrit\tnumber moves\tpassed\tpassed_final\t" +
                    "F1g\tspecificityg\tsensitivityg\tF1e\tspecifitye\tsensitivitye\tspecificityge\tsensitivityge\tF1ge\truntime";

    String suffix = "_1_MSE_Kendall_GEE_top_cut_scoreperc66.0_0.0__nr_trim.txt";

    /**
     * @param args
     */
    public EvalCOALESCE(String[] args) {

        /*
        ~/integr8functgenom/data/COALESCE/coalesce_ds4/y1/00/with.txt.bic
        ~/integr8functgenom/data/COALESCE/coalesce_ds4/y1/00/with.txt.bic/postp.out.bic
        results_coalesce_1_toplist_00_cut_expr1.0_0.2_0.25_0.25_c_reconstructed.txt
        ~/integr8functgenom/data/COALESCE/coalesce_ds4/y1/00/with.pcl_00.txt
        */

        //with.txt.bic
        String refdir = args[0];

        //postp.out.bic
        String coalescedir = args[1];

        String makdir = args[2];

        //with.pcl_00.txt
        String coalescedata = args[3];

        if(args.length == 5) {
            suffix = args[4];
        }

        ArrayList out = new ArrayList();
        out.add(header);
        //for all replicates
        for (int i = 0; i < 5; i++) { ///repsbare.length; i++) {
            System.out.println("repsbare " + i + "\t" + repsbare[i]);
            String reffile = refdir + "/" + repsbare[i] + "/with.txt.bic";
            String cfile = coalescedir + "/" + repsbare[i] + "/postp.out";//with.pcl_" + repsbare[i] + ".txt";
            //results_coalesce_1_toplist_00_cut_scoreperc90.0_0.0.txt
            //String mfile = makdir + "/" + "results_coalesce_1_toplist_" + repsbare[i] + "_cut_scoreperc90.0_0.0.txt";
            //00_1_top_cut_scoreperc66.0_0.0_0.25_1.0_c_liven_reconstructed.txt
            //String mfile = makdir + "/" + repsbare[i] + "_1_top_cut_scoreperc66.0_0.0_0.25_1.0_c_liven_reconstructed.txt";
            //00_1_top_cut_scoreperc66.0_0.0__nr.txt
            String mfile = makdir + "/" + repsbare[i] + suffix;//
            String datafile = coalescedata + "/" + repsbare[i] + "/with.pcl_" + repsbare[i] + ".txt";

            System.out.println("reffile " + reffile);
            System.out.println("cfile " + cfile);
            System.out.println("mfile " + mfile);
            System.out.println("datafile " + datafile);

            SimpleMatrix sm = new SimpleMatrix(datafile);

            ValueBlockList reflist = null;
            ValueBlockList clist = null;
            ValueBlockList mlist = null;
            try {
                reflist = ValueBlockListMethods.readCOALESCESim(reffile);
                System.out.println("reflist " + reflist.size());
                System.out.println("test readCOALESCESim " + (ValueBlock) reflist.get(0));
                ValueBlock test = (ValueBlock) reflist.get(0);
                System.out.println("test readCOALESCESim " + test.blockId());
                String header = "#";
                String s = reflist.toString(header + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
                String outfile = reffile + ".vbl";
                util.TextFile.write(s, outfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                clist = ValueBlockListMethods.readCOALESCE(cfile, sm.ylabels, sm.xlabels);
                System.out.println("clist " + clist.size());
                System.out.println("test readCOALESCE " + (ValueBlock) clist.get(0));
                ValueBlock test = (ValueBlock) clist.get(0);
                System.out.println("test readCOALESCE " + test.blockId());
                String header = "#";
                String s = clist.toString(header + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
                String outfile = cfile + ".vbl";
                util.TextFile.write(s, outfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mlist = ValueBlockListMethods.readAny(mfile, false);
                System.out.println("mlist " + mlist.size());
                System.out.println("test readAny " + (ValueBlock) mlist.get(0));
                ValueBlock test = (ValueBlock) mlist.get(0);
                System.out.println("test readAny " + test.blockId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //int position = clist.size() - 1;
            //ValueBlock curblock = (ValueBlock) clist.get(position);

            System.out.println("summarizing");

            String typelabel = "COALESCE";
            for (int a = 0; a < clist.size(); a++) {
                ValueBlockList vblj = new ValueBlockList();
                ValueBlock curblock = (ValueBlock) clist.get(a);
                System.out.println("clist " + a + "\t" + curblock.blockId());
                vblj.add(curblock);
                int cmax = -1;
                double dcmax = -1;
                ArrayList ccur = new ArrayList();
                for (int j = 0; j < reflist.size(); j++) {
                    ValueBlock vj = (ValueBlock) reflist.get(j);
                    System.out.println("reflist c " + j + "\t" + vj.blockId());
                    double[] add = BlockMethods.summarizeResults(vj, curblock, typelabel, -1,
                            vblj, cfile, sm.ylabels.length, sm.xlabels.length, null, false);

                    if (add[add.length - 3] + add[add.length - 4] > dcmax) {
                        dcmax = add[add.length - 3] + add[add.length - 4];
                        cmax = j;
                        System.out.println("reflist max c " + j + "\t" + cmax + "\t" + cmax + "\t" + vj.blockId());
                    }
                    //System.out.println(typelabel);
                    //MoreArray.printArray(add);

                    ccur.add(typelabel + "\t" + i + "\t" + j + "\t" + a + "\t" + MoreArray.toString(add, "\t"));
                }
                out.add(ccur.get(cmax));
            }


            typelabel = "MAK";
            for (int a = 0; a < mlist.size(); a++) {
                ValueBlockList vblj = new ValueBlockList();
                ValueBlock curblock = (ValueBlock) mlist.get(a);
                vblj.add(curblock);
                int mmax = -1;
                double dmmax = -1;
                ArrayList mcur = new ArrayList();
                for (int j = 0; j < reflist.size(); j++) {
                    ValueBlock vj = (ValueBlock) reflist.get(j);
                    System.out.println("reflist m " + j + "\t" + vj.blockId());
                    double[] add2 = BlockMethods.summarizeResults(vj, curblock, typelabel, -1,
                            vblj, mfile, sm.ylabels.length, sm.xlabels.length, null, false);

                    if (add2[add2.length - 3] + add2[add2.length - 4] > dmmax) {
                        dmmax = add2[add2.length - 3] + add2[add2.length - 4];
                        mmax = j;
                        System.out.println("reflist max m " + j + "\t" + dmmax + "\t" + mmax + "\t" + vj.blockId());
                    }
                    //System.out.println(typelabel);
                    //MoreArray.printArray(add2);

                    mcur.add(typelabel + "\t" + i + "\t" + j + "\t" + a + "\t" + MoreArray.toString(add2, "\t"));
                }
                out.add(mcur.get(mmax));
            }

            typelabel = "2D-HCL";
                        for (int a = 0; a < mlist.size(); a++) {
                            ValueBlockList vblj = new ValueBlockList();
                            ValueBlock curblock = (ValueBlock) mlist.get(a);
                            vblj.add(curblock);
                            int mmax = -1;
                            double dmmax = -1;
                            ArrayList mcur = new ArrayList();
                            for (int j = 0; j < reflist.size(); j++) {
                                ValueBlock vj = (ValueBlock) reflist.get(j);
                                System.out.println("reflist m " + j + "\t" + vj.blockId());
                                double[] add2 = BlockMethods.summarizeResults(vj, curblock, typelabel, -1,
                                        vblj, mfile, sm.ylabels.length, sm.xlabels.length, null, false);

                                if (add2[add2.length - 3] + add2[add2.length - 4] > dmmax) {
                                    dmmax = add2[add2.length - 3] + add2[add2.length - 4];
                                    mmax = j;
                                    System.out.println("reflist max m " + j + "\t" + dmmax + "\t" + mmax + "\t" + vj.blockId());
                                }
                                //System.out.println(typelabel);
                                //MoreArray.printArray(add2);

                                mcur.add(typelabel + "\t" + i + "\t" + j + "\t" + a + "\t" + MoreArray.toString(add2, "\t"));
                            }
                            out.add(mcur.get(mmax));
                        }
        }

        String sout = "eval_COALESCE_out.txt";
        System.out.println("writing " + sout);
        TextFile.write(out, sout);
    }


    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 4 || args.length == 5) {
            EvalCOALESCE arg = new EvalCOALESCE(args);
        } else {
            System.out.println("syntax: java DataMining.ParamforErrMiss\n" +
                    "<ref dir>\n" +
                    "<COALESCE dir>\n" +
                    "<MAK dir>\n" +
                    "<COALESCE data dir>\n"       +
                    "<OPTIONAL SUFFIX to '00'>"
            );
        }
    }
}
