package DataMining;

import mathy.Matrix;
import mathy.stat;
import util.MoreArray;

import java.util.*;

/**
 * User: marcin
 * Date: Feb 11, 2008
 * Time: 4:52:06 PM
 */
public class BlockMethods {

    /**
     * @param IDform
     * @return
     */
    public final static ArrayList[] ijIDtoIcJctoList(String IDform) {
        String delim = "/";
        int mid = IDform.indexOf(delim);
        /*if (mid == -1) {
            delim = "/";
            mid = IDform.indexOf(delim);
        }*/

        String Ic = IDform.substring(0, mid);
        String Jc = IDform.substring(mid + delim.length(), IDform.length());
        //int cIc = Ic.length();
        //int cJc = Jc.length();
        String[] spliti = Ic.split(",");
        String[] splitj = Jc.split(",");
        int[] retIc = MoreArray.tointArray(MoreArray.convtoArrayList(spliti));
        int[] retJc = MoreArray.tointArray(MoreArray.convtoArrayList(splitj));
        /*System.out.println("ijIDtoIcJc retIc");
        MoreArray.printArray(retIc);
        System.out.println("ijIDtoIcJc retJc");
        MoreArray.printArray(retJc);*/
        ArrayList[] ret = MoreArray.initArrayListArray(2);
        for (int i = 0; i < retIc.length; i++) {
            ret[0].add(new Integer(retIc[i]));
        }
        for (int i = 0; i < retJc.length; i++) {
            ret[1].add(new Integer(retJc[i]));
        }
        return ret;
    }

    /**
     * @param IDform
     * @return
     */
    public final static int[][] ijIDtoIcJc(String IDform) {
        int[][] ret = new int[0][];
        try {
//System.out.println("ijIDtoIcJc IDform " + IDform);
            String delim = "/";
            int mid = IDform.indexOf(delim);
            if (mid == -1) {
                mid = IDform.length();
            }

            String Ic = IDform.substring(0, mid);
            String[] spliti = Ic.split(",");

            String[] splitj = new String[0];
            if (mid != -1) {
                try {
                    final String subs = IDform.substring(mid + delim.length(), IDform.length());
                    String Jc = subs;
                    if (Jc.length() > 0)
                        splitj = Jc.split(",");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int[] retIc = null;
            int[] retJc = null;
            ret = new int[2][];

            try {
                retIc = MoreArray.tointArray(MoreArray.convtoArrayList(spliti));
                ret[0] = retIc;
            } catch (Exception e) {
                ret[0] = null;
                e.printStackTrace();
            }
            if (mid != -1) {
                try {
                    retJc = MoreArray.tointArray(MoreArray.convtoArrayList(splitj));
                    ret[1] = retJc;
                } catch (Exception e) {
                    //int[] tmp = new int[1];
                    //tmp[1] = -1;
                    ret[1] = null;
                    e.printStackTrace();
                }
            } else {
                ret[1] = null;
            }

            /*System.out.println("ijIDtoIcJc retIc");
                MoreArray.printArray(retIc);
                System.out.println("ijIDtoIcJc retJc");
                MoreArray.printArray(retJc);*/
            /*ArrayList[] ret = MoreArray.initArrayListArray(2);
                for (int i = 0; i < retIc.length; i++) {
                    ret[0].add(new Integer(retIc[i]));
                }
                for (int i = 0; i < retJc.length; i++) {
                    ret[1].add(new Integer(retJc[i]));
                }*/
        } catch (Exception e) {
            System.out.println("ijIDtoIcJc " + IDform);
            e.printStackTrace();
        }

        //System.out.println("ijIDtoIcJc " + ret[0].length + "\t\t" + (ret[1] != null ? ret[1].length : "null"));

        return ret;
    }

    /**
     * @param a
     * @return
     */
    public final static String IcJctoijID(ArrayList[] a) {
        try {
            int[] retIc = MoreArray.tointArray(a[0]);
            int[] retJc = MoreArray.tointArray(a[1]);
            /*System.out.println("IcJctoijID retIc");
                MoreArray.printArray(retIc);
                System.out.println("IcJctoijID retJc");
                MoreArray.printArray(retJc);*/
            return MoreArray.toString(retIc, ",") + "/" + MoreArray.toString(retJc, ",");
        } catch (Exception e) {
            String[] retIc = MoreArray.ArrayListtoString(a[0]);
            String[] retJc = MoreArray.ArrayListtoString(a[1]);
            return MoreArray.toString(retIc, ",") + "/" + MoreArray.toString(retJc, ",");
        }
    }

    /**
     * @param a
     * @return
     */
    public final static String IcJctoijID(ArrayList a) {
        try {
            int[] retIc = MoreArray.tointArray(a);
            /*System.out.println("IcJctoijID retIc");
            MoreArray.printArray(retIc);
            System.out.println("IcJctoijID retJc");
            MoreArray.printArray(retJc);*/
            return MoreArray.toString(retIc, ",");
        } catch (Exception e) {
            String[] retIc = MoreArray.ArrayListtoString(a);
            return MoreArray.toString(retIc, ",");
        }
    }


    /**
     * @param a
     * @return
     */
    public final static String IcJctoijID(int[][] a) {
        return MoreArray.toString(a[0], ",") + "/" + MoreArray.toString(a[1], ",");
    }

    /**
     * @param a
     * @return
     */
    public final static String IcJctoijID(int[] a) {
        return MoreArray.toString(a, ",");
    }

    /**
     * @param g
     * @param e
     * @return
     */
    public final static String IcJctoijID(int[] g, int[] e) {
        String s = null;
        try {
            s = MoreArray.toString(g, ",");
        } catch (Exception e1) {
            s = "";
            //e1.printStackTrace();
        }
        String s1 = null;
        try {
            s1 = MoreArray.toString(e, ",");
        } catch (Exception e1) {
            s1 = "";
            //e1.printStackTrace();
        }
        return s + "/" + s1;
    }

    /**
     * @param a
     * @param init_genes
     * @param init_exp
     * @return
     */
    public final static String IcJctoijID(ArrayList[] a, String init_genes, String init_exp) {
        int[] retIc = MoreArray.tointArray(a[0]);
        int[] retJc = MoreArray.tointArray(a[1]);
        /*System.out.println("IcJctoijID retIc");
        MoreArray.printArray(retIc);
        System.out.println("IcJctoijID retJc");
        MoreArray.printArray(retJc);*/
        String genes = init_genes + (retIc != null && retIc.length > 0 ? "," + MoreArray.toString(retIc, ",") : "");
        String experiments = init_exp + (retJc != null && retJc.length > 0 ? "," + MoreArray.toString(retJc, ",") : "");
        return genes + "/" + experiments;
    }

    /**
     * @param IDform
     * @param vbp
     * @return
     */
    public final static String splice(String IDform, ValueBlockPre vbp) {
        String ret = "";
        String delim = "/";
        int mid = IDform.indexOf(delim);
        /* if (mid == -1) {
            delim = "/";
            mid = IDform.indexOf(delim);
        }*/
        int[] retIc = vbp.genes;//MoreArray.tointArray(vbp.coords[0]);
        int[] retJc = vbp.exps;//MoreArray.tointArray(vbp.coords[1]);
        if (IDform.substring(0, mid).length() > 0)
            ret = MoreArray.toString(retIc, ",") + IDform.substring(0, mid) + "/" + MoreArray.toString(retJc, ",");
        else
            ret = MoreArray.toString(retIc, ",") + "/" + MoreArray.toString(retJc, ",") + IDform.substring(mid + delim.length(), IDform.length());
        return ret;
    }

    /**
     * Method that returns a random block of specified size and based on the provided data set dimensions.
     *
     * @param ilen
     * @param jlen
     * @param maxX
     * @param maxY
     * @return
     */
    public final static ValueBlock createRandomBlock(int ilen, int jlen, int maxX, int maxY) {
        ValueBlock vb = new ValueBlock();
        Random rand = new Random();
        vb.genes = new int[ilen];
        vb.exps = new int[jlen];
        int[] donex = new int[maxX];
        int[] doney = new int[maxY];
        for (int i = 0; i < ilen; i++) {
            int next = rand.nextInt(maxX) + 1;
            while (donex[next - 1] == 1)
                next = rand.nextInt(maxX) + 1;
            if (donex[next - 1] == 0) {
                donex[next - 1] = 1;
                vb.genes[i] = next;
            }
        }
        for (int i = 0; i < jlen; i++) {
            int next = rand.nextInt(maxY) + 1;
            while (doney[next - 1] == 1)
                next = rand.nextInt(maxY) + 1;
            if (doney[next - 1] == 0) {
                doney[next - 1] = 1;
                vb.exps[i] = next;
            }
        }
        Arrays.sort(vb.genes);
        Arrays.sort(vb.exps);
        int[][] coords = {vb.genes, vb.exps};
        //System.out.println("createRandomStartBlock " + IcJctoijID(coords));
        return vb;
    }

    /**
     * Method that returns a random block of specified size and based on the provided data set dimensions.
     *
     * @param ilen
     * @param jlen
     * @param maxX
     * @param maxY
     * @return
     */
    public final static ValueBlock createRandomBlock(int ilen, int jlen, int maxX, int maxY, Random rand) {
        ValueBlock vb = new ValueBlock();
        vb.genes = new int[ilen];
        vb.exps = new int[jlen];
        int[] donex = new int[maxX];
        int[] doney = new int[maxY];
        for (int i = 0; i < ilen; i++) {
            int next = rand.nextInt(maxX) + 1;
            while (donex[next - 1] == 1)
                next = rand.nextInt(maxX) + 1;
            if (donex[next - 1] == 0) {
                donex[next - 1] = 1;
                vb.genes[i] = next;
            }
        }
        for (int i = 0; i < jlen; i++) {
            int next = rand.nextInt(maxY) + 1;
            while (doney[next - 1] == 1)
                next = rand.nextInt(maxY) + 1;
            if (doney[next - 1] == 0) {
                doney[next - 1] = 1;
                vb.exps[i] = next;
            }
        }
        Arrays.sort(vb.genes);
        Arrays.sort(vb.exps);
        int[][] coords = {vb.genes, vb.exps};
        //System.out.println("createRandomStartBlock " + IcJctoijID(coords));
        return vb;
    }

    /**
     * @param vb
     * @param maxX
     * @param maxY
     * @return
     */
    public final static ValueBlock createRandomBlock(ValueBlock vb, int maxX, int maxY) {
        Random rand = new Random();
        int ilen = vb.genes.length;
        int jlen = vb.exps.length;
        vb.genes = new int[ilen];
        vb.exps = new int[jlen];
        int[] donex = new int[maxX];
        int[] doney = new int[maxY];
        for (int i = 0; i < ilen; i++) {
            int next = rand.nextInt(maxX) + 1;
            while (donex[next - 1] == 1)
                next = rand.nextInt(maxX) + 1;
            if (donex[next - 1] == 0) {
                donex[next - 1] = 1;
                vb.genes[i] = next;
            }
        }
        for (int i = 0; i < jlen; i++) {
            int next = rand.nextInt(maxY) + 1;
            while (doney[next - 1] == 1)
                next = rand.nextInt(maxY) + 1;
            if (doney[next - 1] == 0) {
                doney[next - 1] = 1;
                vb.exps[i] = next;
            }
        }
        Arrays.sort(vb.genes);
        Arrays.sort(vb.exps);
        int[][] coords = {vb.genes, vb.exps};
        //System.out.println("createRandomBlock " + IcJctoijID(coords));
        return vb;
    }

    /**
     * @param vb
     * @param geneMax
     * @param expMax
     * @return
     */
    public final static ValueBlockPre createRandomBlock(ValueBlockPre vb, int geneMax, int expMax, Random rand, boolean debug) {
        //System.out.println("createRandomBlock " + geneMax + "\t" + expMax);
        int ilen = vb.genes.length;
        int jlen = vb.exps.length;
        vb.genes = new int[ilen];
        vb.exps = new int[jlen];
        int[] donex = new int[geneMax];
        int[] doney = new int[expMax];
        if (debug)
            System.out.print(">");
        //genes
        for (int i = 0; i < ilen; i++) {
            int next = rand.nextInt(geneMax) + 1;
            if (debug)
                System.out.print("|" + donex[next - 1]);
            while (donex[next - 1] == 1) {
                if (debug)
                    System.out.print("while");
                next = rand.nextInt(geneMax) + 1;
            }
            if (debug)
                System.out.print("_");
            if (donex[next - 1] == 0) {
                donex[next - 1] = 1;
                vb.genes[i] = next;
            }
        }
        if (debug)
            System.out.print(">");
        //exps
        for (int i = 0; i < jlen; i++) {
            int next = rand.nextInt(expMax) + 1;
            if (debug)
                System.out.print("|" + doney[next - 1]);
            while (doney[next - 1] == 1) {
                if (debug)
                    System.out.print("while" + next + ":");
                next = rand.nextInt(expMax) + 1;
            }
            if (debug)
                System.out.print("_");
            if (doney[next - 1] == 0) {
                doney[next - 1] = 1;
                vb.exps[i] = next;
            }
        }
        Arrays.sort(vb.genes);
        Arrays.sort(vb.exps);
        //int[][] coords = {vb.genes, vb.exps};
        //System.out.println("createRandomBlock " + IcJctoijID(coords));
        vb.updateArea();
        return vb;
    }

    /**
     * Changes input ValueBlock in place.
     *
     * @param vb
     * @param geneMax
     * @param expMax
     * @return
     */
    public final static ValueBlock createRandomBlock(ValueBlock vb, int geneMax, int expMax, Random rand, boolean debug) {
        //System.out.println("createRandomBlock " + geneMax + "\t" + expMax);
        int ilen = vb.genes.length;
        int jlen = vb.exps.length;
        vb.genes = new int[ilen];
        vb.exps = new int[jlen];
        int[] donex = new int[geneMax];
        int[] doney = new int[expMax];
        if (debug)
            System.out.print(">");
        //genes
        for (int i = 0; i < ilen; i++) {
            int next = rand.nextInt(geneMax) + 1;
            if (debug)
                System.out.print("|" + donex[next - 1]);
            while (donex[next - 1] == 1) {
                if (debug)
                    System.out.print("while");
                next = rand.nextInt(geneMax) + 1;
            }
            if (debug)
                System.out.print("_");
            if (donex[next - 1] == 0) {
                donex[next - 1] = 1;
                vb.genes[i] = next;
            }
        }
        if (debug)
            System.out.print(">");
        //exps
        for (int i = 0; i < jlen; i++) {
            int next = rand.nextInt(expMax) + 1;
            if (debug)
                System.out.print("|" + doney[next - 1]);
            while (doney[next - 1] == 1) {
                if (debug)
                    System.out.print("while" + next + ":");
                next = rand.nextInt(expMax) + 1;
            }
            if (debug)
                System.out.print("_");
            if (doney[next - 1] == 0) {
                doney[next - 1] = 1;
                vb.exps[i] = next;
            }
        }
        Arrays.sort(vb.genes);
        Arrays.sort(vb.exps);
        //int[][] coords = {vb.genes, vb.exps};
        //System.out.println("createRandomBlock " + IcJctoijID(vb.genes, vb.exps));
        vb.updateArea();
        return vb;
    }

    /**
     * @param vb
     * @param maxX
     * @return
     */
    public final static ValueBlockPre createRandomGeneBlock(ValueBlockPre vb, int maxX, Random rand) {
        int ilen = vb.genes.length;
        vb.genes = new int[ilen];
        int[] donex = new int[maxX];
        for (int i = 0; i < ilen; i++) {
            int next = rand.nextInt(maxX) + 1;
            while (donex[next - 1] == 1)
                next = rand.nextInt(maxX) + 1;
            if (donex[next - 1] == 0) {
                donex[next - 1] = 1;
                vb.genes[i] = next;
            }
        }
        Arrays.sort(vb.genes);
        int[][] coords = {vb.genes, vb.exps};
        //System.out.println("createRandomGeneBlock " + IcJctoijID(coords));
        return vb;
    }


    /**
     * Operates on ValueBlock in place
     *
     * @param vb
     * @return
     */
    public final static ValueBlock resampleBlock(ValueBlock vb, Random rand) {
        //System.out.println("createRandomBlock " + geneMax + "\t" + expMax);
        int ilen = vb.genes.length;
        int jlen = vb.exps.length;

        int[] genesample = MoreArray.copy(vb.genes);
        int[] expsample = MoreArray.copy(vb.exps);

        vb.genes = new int[ilen];
        vb.exps = new int[jlen];

        //genes
        for (int i = 0; i < ilen; i++) {
            int next = rand.nextInt(ilen);
            vb.genes[i] = genesample[next];
        }
        //exps
        for (int i = 0; i < jlen; i++) {
            int next = rand.nextInt(jlen);
            vb.exps[i] = expsample[next];
        }
        Arrays.sort(vb.genes);
        Arrays.sort(vb.exps);

        return vb;
    }


    /**
     * @param vb
     * @param replace_position
     * @param maxX
     * @param rand
     * @return
     */
    public final static ValueBlockPre replaceGeneWithRand(ValueBlockPre vb, int replace_position, int maxX, Random rand) {
        //int[][] coords1 = {vb.genes, vb.exps};
        //System.out.println("replaceGeneWithRand b/f " + IcJctoijID(coords1));
        int[] donex = new int[maxX];
        for (int i = 0; i < vb.genes.length; i++) {
            if (i != replace_position)
                donex[i] = 1;
        }

        int next = rand.nextInt(maxX) + 1;
        while (donex[next - 1] == 1) {
            next = rand.nextInt(maxX) + 1;
        }
        if (donex[next - 1] == 0) {
            vb.genes[replace_position] = next;
        }

        Arrays.sort(vb.genes);
        //int[][] coords = {vb.genes, vb.exps};
        //System.out.println("replaceGeneWithRand a/f " + IcJctoijID(coords));
        return vb;
    }


    /**
     * @param vb
     * @param maxY
     * @return
     */
    public final static ValueBlockPre createRandomExpBlock(ValueBlockPre vb, int maxY, Random rand) {
        int jlen = vb.exps.length;
        vb.exps = new int[jlen];
        int[] doney = new int[maxY];
        for (int i = 0; i < jlen; i++) {
            int next = rand.nextInt(maxY) + 1;
            while (doney[next - 1] == 1)
                next = rand.nextInt(maxY) + 1;
            if (doney[next - 1] == 0) {
                doney[next - 1] = 1;
                vb.exps[i] = next;
            }
        }
        Arrays.sort(vb.exps);
        int[][] coords = {vb.genes, vb.exps};
        //System.out.println("createRandomExpBlock " + IcJctoijID(coords));
        return vb;
    }

    /**
     * @param vb
     * @param replace_position
     * @param maxY
     * @param rand
     * @return
     */
    public final static ValueBlockPre replaceExpWithRand(ValueBlockPre vb, int replace_position, int maxY, Random rand) {
        int[] donex = new int[maxY];
        for (int i = 0; i < vb.exps.length; i++) {
            if (i != replace_position)
                donex[i] = 1;
        }

        int next = rand.nextInt(maxY) + 1;
        while (donex[next - 1] == 1) {
            next = rand.nextInt(maxY) + 1;
        }
        if (donex[next - 1] == 0) {
            vb.exps[replace_position] = next;
        }

        Arrays.sort(vb.exps);
        //int[][] coords = {vb.genes, vb.exps};
        //System.out.println("replaceExpWithRand " + IcJctoijID(coords));
        return vb;
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    /* public final static double computeBlockF1(ValueBlock ref, ValueBlock test, boolean debug) {
        double overlapG = 0, overlapE = 0;
        int[] countref = new int[ref.genes.length];
        for (int a = 0; a < ref.genes.length; a++) {
            //System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.genes.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (countref[a] == 0 && ref.genes[a] == test.genes[b]) {
                    countref[a] = 1;
                    overlapG++;
                } else if (countref[a] == 1 && ref.genes[a] == test.genes[b]) {
                    System.out.println("computeBlockF1 repeated gene " + test.genes[b]);
                    System.out.println(MoreArray.toString(test.genes, ","));
                }
            }
        }
        countref = new int[ref.exps.length];
        for (int a = 0; a < ref.exps.length; a++) {
            //System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.exps.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (countref[a] == 0 && ref.exps[a] == test.exps[b]) {
                    countref[a] = 1;
                    overlapE++;
                } else if (countref[a] == 1 && ref.exps[a] == test.exps[b]) {
                    System.out.println("computeBlockF1 repeated exps " + test.exps[b]);
                    System.out.println(MoreArray.toString(test.exps, ","));
                }
            }
        }

        double v = 2.0 * ((overlapG * overlapE) / ((double) ref.genes.length + (double) ref.exps.length));
        if (debug)
            System.out.println("computeBlockOverlapGeneAndExp overlapG " +
                    overlapG + "\toverlapE " + overlapE + "\tref.genes " + ref.genes.length
                    + "\tref.exps " + ref.exps.length + "\tF1 " + v);
        return v;
    }
    */

    /**
     * (p * r )
     * 2 * --------
     * p + r
     *
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockF1(ValueBlock ref, ValueBlock test, boolean debug) {

        HashMap refh = BlockMethods.blocktoHash(ref.genes, ref.exps);
        HashMap testh = BlockMethods.blocktoHash(test.genes, test.exps);
        double overlap = BlockMethods.computeBlockOverlapGeneAndExpWithRef(testh, refh);
        double p = overlap / (double) testh.size();
        double r = overlap / (double) refh.size();

        return 2 * ((p * r) / (p + r));
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockPrecisionGenes(ValueBlock ref, ValueBlock test) {
        double overlapG = 0;
        int[] countref = new int[ref.genes.length];
        for (int b = 0; b < test.genes.length; b++) {
            //System.out.println("a b " + a + "\t" + b);
            for (int a = 0; a < ref.genes.length; a++) {
                //System.out.println("a " + a);
                //for all genesStr in test
                if (countref[a] == 0 && ref.genes[a] == test.genes[b]) {
                    countref[a] = 1;
                    overlapG++;
                } else if (countref[a] == 1 && ref.genes[a] == test.genes[b]) {
                    System.out.println("computeBlockPrecisionGenes repeated gene " + test.genes[b]);
                    System.out.println(MoreArray.toString(test.genes, ","));
                }
            }
        }
        /*System.out.println("computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + ref.coords[1].size() + "\tproduct " + product);*/
        return overlapG / (double) test.genes.length;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double[] computeBlockPrecisionRecallGenesAndExps(ValueBlock ref, ValueBlock test) {
        double[] ret = new double[2];
        double overlap = 0;

        ArrayList test_list = new ArrayList();
        for (int i = 0; i < test.genes.length; i++) {
            for (int j = 0; j < test.exps.length; j++) {
                test_list.add("" + test.genes[i] + "_" + test.exps[j]);
            }
        }
        ArrayList ref_list = new ArrayList();
        for (int i = 0; i < ref.genes.length; i++) {
            for (int j = 0; j < ref.exps.length; j++) {
                ref_list.add("" + ref.genes[i] + "_" + ref.exps[j]);
            }
        }

        int[] countref = new int[ref_list.size()];
        for (int b = 0; b < test_list.size(); b++) {
            String cur = (String) test_list.get(b);
            int index = ref_list.indexOf(cur);
            if (index != -1) {
                if (countref[index] == 0) {
                    countref[index] = 1;
                    overlap++;
                } else if (countref[index] == 1) {
                    System.out.println("computeBlockPrecisionGenes repeated gene_exp " + test.genes[b]);
                    System.out.println(MoreArray.toString(test.genes, ","));
                }
            }
        }
        /*System.out.println("computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + ref.coords[1].size() + "\tproduct " + product);*/
        ret[0] = overlap / (double) test_list.size();
        ret[1] = overlap / (double) ref_list.size();
        return ret;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockSpecificityGenes(ValueBlock ref, ValueBlock test, int dim) {
        double tn = 0;
        double fp = 0;
        double tp = 0;
        double fn = 0;
        int[] countref = new int[ref.genes.length];
        for (int b = 0; b < test.genes.length; b++) {
            //System.out.println("a b " + a + "\t" + b);
            for (int a = 0; a < ref.genes.length; a++) {
                //System.out.println("a " + a);
                //for all genesStr in test
                if (countref[a] == 0 && ref.genes[a] == test.genes[b]) {
                    countref[a] = 1;
                } else if (countref[a] == 1 && ref.genes[a] == test.genes[b]) {
                    System.out.println("computeBlockSpecificityGenes repeated gene " + test.genes[b]);
                    System.out.println(MoreArray.toString(test.genes, ","));
                }
            }
        }
        for (int a = 0; a < ref.genes.length; a++) {
            if (countref[a] == 1)
                tp++;
        }
        fn = ref.genes.length - tp;
        fp = test.genes.length - tp;
        tn = dim - (tp + fn + fp);

        /* if (crit != null && (crit.equals("external"))) {
        System.out.println("computeBlockSpecificityGenes " + file);
        *//* System.out.println("computeBlockSpecificityGenes dim " + dim + "\tref " +
                    ref.genes.length + "\ttest " + test.genes.length);
            System.out.println("tp " + tp);
            System.out.println("tn " + tn);
            System.out.println("fp " + fp);
            System.out.println("fn " + fn);*//*
            System.out.println("specificity " + (tn / (tn + fp)));
        }*/

        /*System.out.println("computeBlockOverlapGeneAndExp overlap_count " +
overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
+ "\tref.coords[1].size()) " + ref.coords[1].size() + "\tproduct " + product);*/
        return tn / (tn + fp);
    }

    //computeBlockSpecificityExps

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockSpecificityExps(ValueBlock ref, ValueBlock test, int dim) {
        double tn = 0;
        double fp = 0;
        double tp = 0;
        double fn = 0;
        int[] countref = new int[ref.exps.length];
        for (int b = 0; b < test.exps.length; b++) {
            //System.out.println("a b " + a + "\t" + b);
            for (int a = 0; a < ref.exps.length; a++) {
                //System.out.println("a " + a);
                //for all genesStr in test
                if (countref[a] == 0 && ref.exps[a] == test.exps[b]) {
                    countref[a] = 1;
                } else if (countref[a] == 1 && ref.exps[a] == test.exps[b]) {
                    System.out.println("computeBlockSpecificityExps repeated exps " + test.exps[b]);
                    System.out.println(MoreArray.toString(test.exps, ","));
                }
            }
        }
        for (int a = 0; a < ref.exps.length; a++) {
            if (countref[a] == 1)
                tp++;
        }
        fn = ref.exps.length - tp;
        fp = test.exps.length - tp;
        tn = dim - (tp + fn + fp);
        /* if (crit != null && (crit.equals("external"))) {
        System.out.println("computeBlockSpecificityExps " + file);
        *//* System.out.println("computeBlockSpecificityGenes dim " + dim + "\tref " +
                    ref.genes.length + "\ttest " + test.genes.length);
            System.out.println("tp " + tp);
            System.out.println("tn " + tn);
            System.out.println("fp " + fp);
            System.out.println("fn " + fn);*//*
            System.out.println("specificity " + (tn / (tn + fp)));
        }*/
        /*System.out.println("computeBlockOverlapGeneAndExp overlap_count " +
overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
+ "\tref.coords[1].size()) " + ref.coords[1].size() + "\tproduct " + product);*/
        return tn / (tn + fp);
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockSensitivityGenes(ValueBlock ref, ValueBlock test, int dim) {
        double tn = 0;
        double fp = 0;
        double tp = 0;
        double fn = 0;
        int[] countref = new int[ref.genes.length];
        for (int b = 0; b < test.genes.length; b++) {
            //System.out.println("a b " + a + "\t" + b);
            for (int a = 0; a < ref.genes.length; a++) {
                //System.out.println("a " + a);
                //for all genesStr in test
                if (countref[a] == 0 && ref.genes[a] == test.genes[b]) {
                    countref[a] = 1;
                } else if (countref[a] == 1 && ref.genes[a] == test.genes[b]) {
                    System.out.println("computeBlockSensitivityGenes repeated gene " + test.genes[b]);
                    System.out.println(MoreArray.toString(test.genes, ","));
                }
            }
        }
        for (int a = 0; a < ref.genes.length; a++) {
            if (countref[a] == 1)
                tp++;
        }
        fn = ref.genes.length - tp;
        fp = test.genes.length - tp;
        tn = dim - (tp + fn + fp);

        return tp / (tp + fn);
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockSensitivityExps(ValueBlock ref, ValueBlock test, int dim) {
        double tn = 0;
        double fp = 0;
        double tp = 0;
        double fn = 0;
        int[] countref = new int[ref.exps.length];
        for (int b = 0; b < test.exps.length; b++) {
            //System.out.println("a b " + a + "\t" + b);
            for (int a = 0; a < ref.exps.length; a++) {
                //System.out.println("a " + a);
                //for all expsStr in test
                if (countref[a] == 0 && ref.exps[a] == test.exps[b]) {
                    countref[a] = 1;
                } else if (countref[a] == 1 && ref.exps[a] == test.exps[b]) {
                    System.out.println("computeBlockSensitivityExps repeated exps " + test.exps[b]);
                    System.out.println(MoreArray.toString(test.exps, ","));
                }
            }
        }
        for (int a = 0; a < ref.exps.length; a++) {
            if (countref[a] == 1)
                tp++;
        }
        fn = ref.exps.length - tp;
        fp = test.exps.length - tp;
        tn = dim - (tp + fn + fp);

        return tp / (tp + fn);
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double[] computeBlockSensitivitySpecificityGenesAndExps(ValueBlock ref, ValueBlock test, int dim) {
        double tn = 0;
        double fp = 0;
        double tp = 0;
        double fn = 0;

        ArrayList test_list = new ArrayList();
        for (int i = 0; i < test.genes.length; i++) {
            for (int j = 0; j < test.exps.length; j++) {
                test_list.add("" + test.genes[i] + "_" + test.exps[j]);
            }
        }
        ArrayList ref_list = new ArrayList();
        for (int i = 0; i < ref.genes.length; i++) {
            for (int j = 0; j < ref.exps.length; j++) {
                ref_list.add("" + ref.genes[i] + "_" + ref.exps[j]);
            }
        }

        int[] countref = new int[ref_list.size()];

        for (int a = 0; a < test_list.size(); a++) {
            String curt = (String) test_list.get(a);
            int ind = ref_list.indexOf(curt);
            if (ind != -1) {
                countref[ind] = 1;
            }
        }
        for (int a = 0; a < ref_list.size(); a++) {
            if (countref[a] > 0)
                tp++;
        }
        fn = ref_list.size() - tp;
        fp = test_list.size() - tp;
        tn = dim - (tp + fn + fp);


        double sens = tp / (tp + fn);
        double spec = tn / (tn + fp);

        /* if (tp > 0) {
            System.out.println("sizes " + ref.genes.length + "\t" + ref.exps.length +
                    "\ttest " + test.genes.length + "\t" + test.exps.length);
            System.out.println("sizes " + ref_list.size() + "\ttest " + test_list.size());
            System.out.println("tp " + tp + "\tfn " + fn + "\tfp " + fp + "\ttn " + tn);
            System.out.println("sens " + sens + "\t1-spec " + (1 - spec));
        }*/

        double[] ret = {
                //sensitivity
                sens,
                //specificity
                spec
        };
        return ret;
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockPrecisionExps(ValueBlock ref, ValueBlock test) {
        double overlapE = 0;
        int[] countref = new int[ref.exps.length];
        for (int b = 0; b < test.exps.length; b++) {
            //System.out.println("a b " + a + "\t" + b);
            for (int a = 0; a < ref.exps.length; a++) {
                //System.out.println("a " + a);
                //for all genesStr in test
                if (countref[a] == 0 && ref.exps[a] == test.exps[b]) {
                    countref[a] = 1;
                    overlapE++;
                } else if (countref[a] == 1 && ref.exps[a] == test.exps[b]) {
                    System.out.println("computeBlockPrecisionExps repeated exp " + test.exps[b]);
                    System.out.println(MoreArray.toString(test.exps, ","));
                }
            }
        }
        /*System.out.println("computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + ref.coords[1].size() + "\tproduct " + product);*/
        return overlapE / (double) test.exps.length;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockRecallGenes(ValueBlock ref, ValueBlock test) {
        double overlapG = 0;
        int[] countref = new int[ref.genes.length];
        for (int a = 0; a < ref.genes.length; a++) {
            //System.out.println("a " + a);
            for (int b = 0; b < test.genes.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                //for all genesStr in test
                if (countref[a] == 0 && ref.genes[a] == test.genes[b]) {
                    countref[a] = 1;
                    overlapG++;
                    //System.out.println("a b " + a + "\t" + b + "\t" + ref.genes[a] + "\t" + test.genes[b]);
                } else if (countref[a] == 1 && ref.genes[a] == test.genes[b]) {
                    System.out.println("computeBlockRecallGenes repeated genes " + test.genes[b]);
                    System.out.println(MoreArray.toString(test.genes, ","));
                }
            }
        }
        double r = overlapG / (double) ref.genes.length;
        /* if (r > 1) {
            System.out.println("ref " + MoreArray.toString(ref.genes, ","));
            System.out.println("test " + MoreArray.toString(test.genes, ","));
            System.out.println("computeBlockOverlapGeneAndExp overlap_count " +
                    overlapG + "\tref.genes.length " + ref.genes.length
                    + "\ttest.genes.length " + test.genes.length + "\tproduct " + r);
            for (int b = 0; b < test.genes.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                for (int a = 0; a < ref.genes.length; a++) {
                    //System.out.println("a " + a);
                    //for all genesStr in test
                    if (ref.genes[a] == test.genes[b]) {
                        System.out.println("a b " + a + "\t" + b + "\t" + ref.genes[a] + "\t" + test.genes[b]);
                    }
                }
            }
        }*/
        return r;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockRecallExps(ValueBlock ref, ValueBlock test) {
        double overlapE = 0;
        int[] countref = new int[ref.exps.length];
        for (int b = 0; b < test.exps.length; b++) {
            //System.out.println("a b " + a + "\t" + b);
            for (int a = 0; a < ref.exps.length; a++) {
                //System.out.println("a " + a);
                //for all genesStr in test
                if (countref[a] == 0 && ref.exps[a] == test.exps[b]) {
                    countref[a] = 1;
                    overlapE++;
                } else if (countref[a] == 1 && ref.exps[a] == test.exps[b]) {
                    System.out.println("computeBlockRecallExps repeated exps " + test.exps[b]);
                    System.out.println(MoreArray.toString(test.exps, ","));
                }
            }
        }
        /*System.out.println("computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + ref.coords[1].size() + "\tproduct " + product);*/
        return overlapE / (double) ref.exps.length;
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    /*public final static double computeCountOverlapGeneAndExpWithRef(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //for all genesStr in ref
        int[] countref = new int[ref.genes.length];
        for (int a = 0; a < ref.genes.length; a++) {
            //System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.genes.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (countref[a] == 0 && ref.genes[a] == test.genes[b]) {
                    countref[a] = 1;
                    overlap_count++;
                } else if (countref[a] == 1 && ref.genes[a] == test.genes[b]) {
                    System.out.println("computeCountOverlapGeneAndExpWithRef repeated genes " + test.genes[b]);
                    //System.out.println("computeCountOverlapGeneAndExpWithRef test");
                    System.out.println(MoreArray.toString(test.genes, ","));
                    //System.out.println("computeCountOverlapGeneAndExpWithRef ref");
                    //System.out.println(MoreArray.toString(ref.genes, ","));
                }
            }
        }
        countref = new int[ref.exps.length];
        for (int a = 0; a < ref.exps.length; a++) {
            //System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.exps.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (countref[a] == 0 && ref.exps[a] == test.exps[b]) {
                    countref[a] = 1;
                    overlap_count++;
                } else if (countref[a] == 1 && ref.exps[a] == test.exps[b]) {
                    System.out.println("computeCountOverlapGeneAndExpWithRef repeated exps " + test.exps[b]);
                    System.out.println(MoreArray.toString(test.exps, ","));
                }
            }
        }
        return overlap_count;
    }*/

    /**
     * @param ref
     * @param test
     * @return
     */
    /*public final static double computeCountOverlapGeneAndExpWithRefPair(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;

         overlap_count = countGeneAndExpPairOverlap(overlap_count, ref.genes, ref.exps, test.genes, test.exps);

        //for all genesStr in ref
        int[][] countref = new int[ref.genes.length][ref.exps.length];
        //int[] countexpref = new int[ref.exps.length];
        for (int a = 0; a < ref.genes.length; a++) {
            for (int b = 0; b < ref.exps.length; b++) {
                //System.out.println("a " + a);
                //for all genesStr in test
                for (int c = 0; c < test.genes.length; c++) {
                    for (int d = 0; d < test.exps.length; d++) {
                        //System.out.println("a b " + a + "\t" + b);
                        if (countref[a][b] == 0 && ref.genes[a] == test.genes[c] && ref.exps[b] == test.exps[d]) {
                            countref[a][b] = 1;
                            overlap_count++;
                        } else if (countref[a][b] != 0 && ref.genes[a] == test.genes[c] && ref.exps[b] == test.exps[d]) {
                            System.out.println("computeCountOverlapGeneAndExpWithRef repeated gene/exp pair " +
                                    test.genes[c] + "\t" + test.exps[d]);
                        }
                    }
                }
            }
        }
        return overlap_count;
    }*/

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computefrxnGeneAndExpWithRefPairMin(ValueBlock ref, ValueBlock test, boolean debug) {
        double overlap_count = BlockMethods.countGeneAndExpPairOverlap(ref, test, debug);
        int denom = Math.min(ref.genes.length * ref.exps.length, test.genes.length * test.exps.length);
        double ret = overlap_count / (double) denom;
        //System.out.println("computefrxnGeneAndExpWithRefPairMin " + overlap_count + "\t" + denom + "\t" + ret);
        return ret;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneAndExpWithRef(ValueBlock ref, ValueBlock test) {
        HashMap refh = BlockMethods.blocktoHash(ref.genes, ref.exps);

        return computeBlockOverlapGeneAndExpWithRef(test, refh);
    }

    /**
     * @param test
     * @param refh
     * @return
     */
    public final static double computeBlockOverlapGeneAndExpWithRef(ValueBlock test, HashMap refh) {
        HashMap testh = BlockMethods.blocktoHash(test.genes, test.exps);

        return computeBlockOverlapGeneAndExpWithRef(refh, testh);
    }

    /**
     * @param testh
     * @param refh
     * @return
     */
    public final static double computeBlockOverlapGeneAndExpWithRef(HashMap testh, HashMap refh) {
        double overlap_count = 0;

        return countGeneAndExpPairOverlap(overlap_count, refh, testh, false);
    }


    /**
     * @param r
     * @param t
     * @return
     */
    private static double countGeneAndExpPairOverlap(ValueBlock r, ValueBlock t, boolean debug) {

        HashMap refh = BlockMethods.blocktoHash(r.genes, r.exps);
        HashMap testh = BlockMethods.blocktoHash(t.genes, t.exps);

        return countGeneAndExpPairOverlap(0, refh, testh, debug);
    }

    /**
     * @param r
     * @param t
     * @return
     */
    private static double countGeneAndExpPairSumOverlap(ValueBlock r, ValueBlock t, boolean debug) {

        HashMap refh = BlockMethods.blocktoHash(r.genes, r.exps);
        HashMap testh = BlockMethods.blocktoHash(t.genes, t.exps);
        HashMap all = BlockMethods.blocktoHash(t.genes, t.exps, refh);

        return countGeneAndExpPairSumOverlap(0, refh, testh, debug) / (double) all.size();
    }


    /**
     * @param overlap_count
     * @param refh
     * @param testh
     * @param debug
     * @return
     */
    public static double countGeneAndExpPairOverlap(double overlap_count, HashMap refh, HashMap testh, boolean debug) {
        if (debug)
            System.out.println("countGeneAndExpPairOverlap");

        Set refset = refh.entrySet();
        Iterator it = refset.iterator();
        int count = 0;
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String k = (String) cur.getKey();
            if (debug)
                System.out.println("countGeneAndExpPairOverlap key " + k + "\toverlap " + overlap_count +
                        "\tcount" + count + "\ttotal " + refset.size());
            //int v = (Integer) cur.getValue();
            if (testh.containsKey(k))
                overlap_count++;
            count++;
        }
        return overlap_count;
    }

    /**
     * @param overlap_count
     * @param refh
     * @param testh
     * @param debug
     * @return
     */
    public static double countGeneAndExpPairSumOverlap(double overlap_count, HashMap refh, HashMap testh, boolean debug) {
        if (debug)
            System.out.println("countGeneAndExpPairOverlap");

        Set refset = refh.entrySet();
        Iterator it = refset.iterator();
        int count = 0;
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String k = (String) cur.getKey();
            if (debug)
                System.out.println("countGeneAndExpPairOverlap key " + k + "\toverlap " + overlap_count +
                        "\tcount" + count + "\ttotal " + refset.size());
            //int v = (Integer) cur.getValue();
            if (testh.containsKey(k))
                overlap_count++;
            count++;
        }
        return overlap_count;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public final static HashMap blocktoHash(int[] genes, int[] exps) {
        HashMap ha = new HashMap();
        for (int a = 0; a < genes.length; a++) {
            for (int b = 0; b < exps.length; b++) {
                String key = genes[a] + "_" + exps[b];
                if (!ha.containsKey(key)) {
                    ha.put(key, 1);
                }
            }
        }
        return ha;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public final static HashMap blocktoHash(int[] genes, int[] exps, HashMap store) {
        for (int a = 0; a < genes.length; a++) {
            for (int b = 0; b < exps.length; b++) {
                String key = genes[a] + "_" + exps[b];
                if (!store.containsKey(key)) {
                    store.put(key, 1);
                }
            }
        }
        return store;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneRootProduct(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.genes == test.genes) {
        //    System.out.println("ref and test identical computeBlockOverlapGeneRootProduct");
        //}
        for (int a = 0; a < ref.genes.length; a++) {
            //System.out.println("a " + a);
            for (int b = 0; b < test.genes.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (ref.genes[a] == test.genes[b]) {
                    //System.out.println("computeBlockOverlapGeneRootProduct identical " + a + "\tref_genes" + ref_genes[a] +
                    //       "\t" + b + "\ttest_genes " + test_genes[b]);
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        double sqrt_product = Math.sqrt(ref.genes.length * test.genes.length);
        double frxn = overlap_count / sqrt_product;
        /*System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + test.coords[0].size() + "\tsqrt_product " + sqrt_product);*/

        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
            System.out.println("computeBlockOverlapGeneRootProduct > 1 " + overlap_count + "\t" + sqrt_product);
            System.out.println("computeBlockOverlapGeneRootProduct > 1 " + ref.genes.length + "\t" + test.genes.length);
        }
        if (Double.isNaN(frxn)) {
            System.out.println("computeBlockOverlapGeneRootProduct NaN " + overlap_count + "\t" + sqrt_product);
            System.out.println("computeBlockOverlapGeneRootProduct NaN " + ref.genes.length + "\t" + test.genes.length);
        }
        return frxn;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneNum(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.genes == test.genes) {
        //   System.out.println("ref and test identical computeBlockOverlapGeneNum");
        //}
        for (int a = 0; a < ref.genes.length; a++) {
            //System.out.println("a " + a);
            for (int b = 0; b < test.genes.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (ref.genes[a] == test.genes[b]) {
                    //System.out.println("computeBlockOverlapGeneRootProduct identical " + a + "\tref_genes" + ref_genes[a] +
                    //       "\t" + b + "\ttest_genes " + test_genes[b]);
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        return overlap_count;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapExpNum(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.exps == test.exps) {
        //    System.out.println("ref and test identical computeBlockOverlapExpNum");
        //}
        for (int a = 0; a < ref.exps.length; a++) {
            //System.out.println("a " + a);
            for (int b = 0; b < test.exps.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (ref.exps[a] == test.exps[b]) {
                    //System.out.println("computeBlockOverlapGeneRootProduct identical " + a + "\tref_genes" + ref_genes[a] +
                    //       "\t" + b + "\ttest_genes " + test_genes[b]);
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        return overlap_count;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapExpRootProduct(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.exps == test.exps) {
        //    System.out.println("ref and test identical computeBlockOverlapExpRootProduct");
        //}
        for (int a = 0; a < ref.exps.length; a++) {
            //System.out.println("a " + a);
            for (int b = 0; b < test.exps.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (ref.exps[a] == test.exps[b]) {
                    //System.out.println("computeBlockOverlapGeneRootProduct identical " + a + "\tref_genes" + ref_genes[a] +
                    //       "\t" + b + "\ttest_genes " + test_genes[computeBlockOverlapExpRootProduct]);
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        double sqrt_product = Math.sqrt(ref.exps.length * test.exps.length);
        double frxn = overlap_count / sqrt_product;
        /*System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + test.coords[0].size() + "\tsqrt_product " + sqrt_product);*/
        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
            System.out.println("computeBlockOverlapExpRootProduct > 1 " + overlap_count + "\t" + sqrt_product);
            System.out.println("computeBlockOverlapExpRootProduct > 1 " + ref.exps.length + "\t" + test.exps.length);
        }
        if (Double.isNaN(frxn)) {
            System.out.println("computeBlockOverlapExpRootProduct NaN " + overlap_count + "\t" + sqrt_product);
            System.out.println("computeBlockOverlapExpRootProduct NaN " + ref.exps.length + "\t" + test.exps.length);
        }
        return frxn;
    }

    /**
     * The fraction of the test block contained in the ref block.
     *
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneExpFraction(ValueBlock ref, ValueBlock test, boolean debug) {
        double overlap_count = 0;
        if (ref == null)
            System.out.println("ref is null");
        else if (ref.genes == null)
            System.out.println("ref.genes is null");
        if (test == null)
            System.out.println("test is null");
        else if (test.genes == null)
            System.out.println("test.genes is null");

        if (debug && ref.genes == test.genes) {
            System.out.println("ref and test identical");
        }

        for (int b = 0; b < test.genes.length; b++) {
            for (int a = 0; a < ref.genes.length; a++) {
                if (test.genes[b] == ref.genes[a]) {
                    for (int j = 0; j < test.exps.length; j++) {
                        for (int i = 0; i < ref.exps.length; i++) {
                            //System.out.println("a b " + a + "\t" + b);
                            if (test.exps[j] == ref.exps[i]) {
                                overlap_count++;
                                //go to next ref exp
                                //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                                //  j = test.exps.length;

                                if (debug)
                                    System.out.println("computeBlockOverlapGeneExpFraction identical g " +
                                            test.genes[b] + "\t" + ref.genes[a] + "\t\te " + test.exps[j] + "\t" + ref.exps[i] + "\t\t" + overlap_count);

                                //if found match end loop
                                i = ref.exps.length;
                            }
                        }
                    }
                    //if found match end loop
                    a = ref.genes.length;
                }
            }
        }

        return overlap_count / ((double) test.genes.length * test.exps.length);
    }

    /**
     * The fraction of the test block contained in the ref block.
     *
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneFraction(ValueBlock ref, ValueBlock test, boolean debug) {
        double overlap_count = 0;
        if (ref == null)
            System.out.println("ref is null");
        else if (ref.genes == null)
            System.out.println("ref.genes is null");
        if (test == null)
            System.out.println("test is null");
        else if (test.genes == null)
            System.out.println("test.genes is null");

        if (debug && ref.genes == test.genes) {
            System.out.println("ref and test identical");
        }

        for (int b = 0; b < test.genes.length; b++) {
            for (int a = 0; a < ref.genes.length; a++) {
                if (test.genes[b] == ref.genes[a]) {
                    overlap_count++;

                    if (debug)
                        System.out.println("computeBlockOverlapGeneExpFraction identical g " +
                                test.genes[b] + "\t" + ref.genes[a] + "\t\t" + overlap_count);

                    //if found match end loop
                    a = ref.genes.length;
                }
            }
        }

        return overlap_count / ((double) test.genes.length);
    }

    /**
     * The fraction of the test block contained in the ref block.
     *
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapExpFraction(ValueBlock ref, ValueBlock test, boolean debug) {
        double overlap_count = 0;
        if (ref == null)
            System.out.println("ref is null");
        else if (ref.exps == null)
            System.out.println("ref.exps is null");
        if (test == null)
            System.out.println("test is null");
        else if (test.exps == null)
            System.out.println("test.exps is null");

        if (debug && ref.genes == test.exps) {
            System.out.println("ref and test identical");
        }

        for (int b = 0; b < test.exps.length; b++) {
            for (int a = 0; a < ref.exps.length; a++) {
                if (test.exps[b] == ref.exps[a]) {
                    overlap_count++;

                    if (debug)
                        System.out.println("computeBlockOverlapGeneExpFraction identical g " +
                                test.exps[b] + "\t" + ref.exps[a] + "\t\t" + overlap_count);

                    //if found match end loop
                    a = ref.exps.length;
                }
            }
        }

        return overlap_count / ((double) test.exps.length);
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneExpRootProduct(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.genes == test.genes) {
        //    System.out.println("ref and test identical");
        //}
        for (int a = 0; a < ref.genes.length; a++) {
            for (int b = 0; b < test.genes.length; b++) {
                if (ref.genes[a] == test.genes[b]) {
                    for (int i = 0; i < ref.exps.length; i++) {
                        for (int j = 0; j < test.exps.length; j++) {
                            //System.out.println("a b " + a + "\t" + b);
                            if (ref.exps[i] == test.exps[j]) {
                                //System.out.println("computeBlockOverlapGeneExpRootProduct identical " + a + "\tref_genes" + ref_genes[a] +
                                //       "\t" + b + "\ttest_genes " + test_genes[b]);
                                overlap_count++;
                                //go to next ref exp
                                //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                                j = test.exps.length;
                            }
                        }
                    }
                }
            }
        }
        double totalcount = 0;
        for (int a = 0; a < ref.genes.length; a++) {
            for (int b = 0; b < test.genes.length; b++) {
                for (int i = 0; i < ref.exps.length; i++) {
                    for (int j = 0; j < test.exps.length; j++) {
                        //System.out.println("a b " + a + "\t" + b);
                        //System.out.println("computeBlockOverlapGeneExpRootProduct identical " + a + "\tref_genes" + ref_genes[a] +
                        //       "\t" + b + "\ttest_genes " + test_genes[b]);
                        totalcount++;
                        //go to next ref exp
                        //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    }
                }
            }
        }

        double product = (double) ref.genes.length * (double) test.genes.length * (double) ref.exps.length * (double) test.exps.length;
        double sqrt_product = Math.sqrt(product);
        double orig = overlap_count / sqrt_product;
        if (ref.genes.length == test.genes.length && ref.exps.length == test.exps.length) {
            if (orig > 1) {
                System.out.println("frxn > 1 " + orig);
                System.out.println("computeBlockOverlapGeneExpRootProduct > 1 " + overlap_count + "\t" + totalcount + "\t" + sqrt_product);
                System.out.println("computeBlockOverlapGeneExpRootProduct > 1 " + ref.genes.length + "\t" + test.genes.length
                        + "\t" + ref.exps.length + "\t" + test.exps.length + "\t" + sqrt_product + "\t" + product);
            }
            sqrt_product = ref.genes.length * ref.exps.length;
            System.out.println("computeBlockOverlapGeneExpRootProduct set sqrt_product " + orig + "\t" + sqrt_product +
                    "\ttotalcount " + totalcount);
        }
        double frxn = orig;
        /*System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + test.coords[0].size() + "\tsqrt_product " + sqrt_product);*/

        if (Double.isNaN(frxn)) {
            System.out.println("computeBlockOverlapGeneExpRootProduct NaN " + overlap_count + "\t" + sqrt_product);
            System.out.println("computeBlockOverlapGeneExpRootProduct NaN " + ref.genes.length + "\t" + test.genes.length
                    + "\t" + ref.exps.length + "\t" + test.exps.length + "\t" + sqrt_product + "\t" + product);
        }
        return frxn;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double JaccardIndexGenesExps(ValueBlock ref, ValueBlock test) {
        return computeBlockOverlapGeneExpSum(ref, test);
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneExpSum(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.genes == test.genes) {
        //    System.out.println("ref and test identical");
        //}

        HashMap<String, Integer> all = new HashMap();
        HashMap<String, Integer> refmap = new HashMap();
        HashMap<String, Integer> testmap = new HashMap();
        ArrayList<String> refar = new ArrayList();
        ArrayList<String> testar = new ArrayList();

        for (int a = 0; a < ref.genes.length; a++) {
            for (int i = 0; i < ref.exps.length; i++) {
                String label = "" + ref.genes[a] + "_" + ref.exps[i];
                if (all.get(label) == null) {
                    all.put(label, 1);
                    refmap.put(label, 1);
                } else {
                    int count = refmap.get(label);
                    int i1 = count + 1;
                    refmap.put(label, i1);
                    System.out.println("duplicate gene-exp pair in ref " + label + "\t" + i1);
                }
                refar.add(label);
            }
        }

        for (int b = 0; b < test.genes.length; b++) {
            for (int j = 0; j < test.exps.length; j++) {
                String label2 = "" + test.genes[b] + "_" + test.exps[j];
                if (all.get(label2) == null) {
                    all.put(label2, 1);
                    testmap.put(label2, 1);
                } else {
                    int count = refmap.get(label2);
                    int i1 = count + 1;
                    refmap.put(label2, i1);
                    if (i1 > 2)
                        System.out.println("duplicate gene-exp pair in test " + label2 + "\t" + i1);
                }
                testar.add(label2);
            }
        }

        for (int a = 0; a < ref.genes.length; a++) {
            for (int b = 0; b < test.genes.length; b++) {
                if (ref.genes[a] == test.genes[b]) {
                    for (int i = 0; i < ref.exps.length; i++) {
                        for (int j = 0; j < test.exps.length; j++) {
                            //System.out.println("a b " + a + "\t" + b);
                            if (ref.exps[i] == test.exps[j]) {
                                //System.out.println("computeBlockOverlapGeneExpRootProduct identical " + a + "\tref_genes" + ref_genes[a] +
                                //       "\t" + b + "\ttest_genes " + test_genes[b]);
                                overlap_count++;
                                //go to next ref exp
                                //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                                j = test.exps.length;
                            }
                        }
                    }
                }
            }
        }

        //double product = (float) ref.genes.length * (float) test.genes.length * (float) ref.exps.length * (float) test.exps.length;
        //double sqrt_product = Math.sqrt(product);
        double frxn = overlap_count / (double) all.size();
        /*System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + test.coords[0].size() + "\tsqrt_product " + sqrt_product);*/

        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
            System.out.println("computeBlockOverlapGeneExpSum > 1 " + overlap_count + "\t" + all.size() + "\t" + refar.size() + "\t" + testar.size());
            System.out.println("computeBlockOverlapGeneExpSum > 1 " + ref.genes.length + "\t" + test.genes.length
                    + "\t" + ref.exps.length + "\t" + test.exps.length + "\t" + all.size());
        }
        if (Double.isNaN(frxn)) {
            System.out.println("computeBlockOverlapGeneExpSum NaN " + overlap_count + "\t" + all.size());
            System.out.println("computeBlockOverlapGeneExpSum NaN " + ref.genes.length + "\t" + test.genes.length
                    + "\t" + ref.exps.length + "\t" + test.exps.length + "\t" + all.size());
        }
        return frxn;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneExpFraction(HashMap ref, HashMap test,
                                                                  int refglen, int refelen, int testglen, int testelen) {
        double overlap_count = 0;

        Set refset = ref.entrySet();
        Set testset = test.entrySet();
        Iterator it = refset.iterator();

        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String k = (String) cur.getKey();
            Object o = test.get(k);
            if (o != null) {
                overlap_count++;
            }
        }

        double product = (float) refglen * (float) refelen;
        //double product = (float) refglen * (float) testglen * (float) refelen * (float) testelen;

        double frxn = overlap_count / product;
        //System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
        //       overlap_count + "\trefset.size() " + refset.size()
        //       + "\ttestset.size()) " + testset.size() + "\tsqrt_product " + sqrt_product);
        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
        }

        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
            System.out.println("computeBlockOverlapGeneExpRootProduct > 1 " + overlap_count + "\t" + product);
            System.out.println("computeBlockOverlapGeneExpRootProduct > 1 " + refglen + "\t" + testglen
                    + "\t" + refelen + "\t" + testelen + "\t" + product + "\t" + product);
        }
        if (Double.isNaN(frxn)) {
            System.out.println("computeBlockOverlapGeneExpRootProduct NaN " + overlap_count + "\t" + product);
            System.out.println("computeBlockOverlapGeneExpRootProduct NaN " + refglen + "\t" + testglen
                    + "\t" + refelen + "\t" + testelen + "\t" + product + "\t" + product);
        }
        return frxn;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneExpRootProduct(HashMap ref, HashMap test,
                                                                     int refglen, int refelen, int testglen, int testelen) {
        double overlap_count = 0;

        Set refset = ref.entrySet();
        //Set testset = test.entrySet();
        Iterator it = refset.iterator();

        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String k = (String) cur.getKey();
            Object o = test.get(k);
            if (o != null) {
                overlap_count++;
            }
        }

        double product = (float) refglen * (float) testglen * (float) refelen * (float) testelen;
        double sqrt_product = Math.sqrt(product);
        double frxn = overlap_count / sqrt_product;
        //System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
        //       overlap_count + "\trefset.size() " + refset.size()
        //       + "\ttestset.size()) " + testset.size() + "\tsqrt_product " + sqrt_product);
        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
        }

        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
            System.out.println("computeBlockOverlapGeneExpRootProduct > 1 " + overlap_count + "\t" + sqrt_product);
            System.out.println("computeBlockOverlapGeneExpRootProduct > 1 " + refglen + "\t" + testglen
                    + "\t" + refelen + "\t" + testelen + "\t" + sqrt_product + "\t" + product);
        }
        if (Double.isNaN(frxn)) {
            System.out.println("computeBlockOverlapGeneExpRootProduct NaN " + overlap_count + "\t" + sqrt_product);
            System.out.println("computeBlockOverlapGeneExpRootProduct NaN " + refglen + "\t" + testglen
                    + "\t" + refelen + "\t" + testelen + "\t" + sqrt_product + "\t" + product);
        }
        return frxn;
    }

    /**
     * @param ref
     * @param test
     * @param debug
     * @return
     */
    public final static double computeBlockOverlapGeneMin(ValueBlock ref, ValueBlock test, boolean debug) {
        double overlap_count = 0;
        if (debug)
            System.out.println("ref and test len " + ref.genes.length + "\t" + test.genes.length);
        if (debug && ref.genes == test.genes) {
            System.out.println("ref and test identical");
        }
        //for all genesStr in ref
        for (int a = 0; a < ref.genes.length; a++) {
            if (debug)
                System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.genes.length; b++) {
                if (debug)
                    System.out.println("a b " + a + "\t" + b);
                if (ref.genes[a] == test.genes[b]) {
                    if (debug)
                        System.out.println("computeBlockOverlapGeneMin identical");
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        double min = Math.min(ref.genes.length, test.genes.length);
        double frxn = overlap_count / min;
        /*System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + test.coords[0].size() + "\tmin " + min);*/
        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
        }
        return frxn;
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double JaccardIndexGenes(ValueBlock ref, ValueBlock test) {
        return computeBlockOverlapGeneSum(ref, test);
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapGeneSum(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //if (debug)
        //   System.out.println("ref and test len " + ref.genes.length + "\t" + test.genes.length);
        //if (ref.genes == test.genes) {
        //    System.out.println("ref and test identical");
        //}
        //for all genesStr in ref
        for (int a = 0; a < ref.genes.length; a++) {
            // if (debug)
            //    System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.genes.length; b++) {
                //  if (debug)
                //     System.out.println("a b " + a + "\t" + b);
                if (ref.genes[a] == test.genes[b]) {
                    //  if (debug)
                    //       System.out.println("computeBlockOverlapGeneMin identical");
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        //double min = Math.min(ref.genes.length, test.genes.length);

        HashMap all = new HashMap();
        for (int a = 0; a < ref.genes.length; a++) {
            Object o = all.get(ref.genes[a]);
            if (o == null)
                all.put(ref.genes[a], 1);
        }
        for (int a = 0; a < test.genes.length; a++) {
            Object o = all.get(test.genes[a]);
            if (o == null)
                all.put(test.genes[a], 1);
        }
        double frxn = overlap_count / (double) all.size();

        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
            System.out.println(frxn + " computeBlockOverlapGeneSum overlap_count " +
                    overlap_count + "\tref " + ref.genes.length
                    + "\ttest " + test.genes.length + "\tall " + all.size());
        }
        return frxn;
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double JaccardIndexExps(ValueBlock ref, ValueBlock test) {
        return computeBlockOverlapExpSum(ref, test);
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapExpSum(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //if (debug)
        //    System.out.println("ref and test len " + ref.exps.length + "\t" + test.exps.length);
        //if (ref.exps == test.exps) {
        //    System.out.println("ref and test identical");
        //}
        //for all genesStr in ref
        for (int a = 0; a < ref.exps.length; a++) {
            // if (debug)
            //    System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.exps.length; b++) {
                //   if (debug)
                //      System.out.println("a b " + a + "\t" + b);
                if (ref.exps[a] == test.exps[b]) {
                    //      if (debug)
                    //        System.out.println("computeBlockOverlapExpSum identical");
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        //double min = Math.min(ref.genes.length, test.genes.length);

        HashMap all = new HashMap();
        for (int a = 0; a < ref.exps.length; a++) {
            Object o = all.get(ref.exps[a]);
            if (o == null)
                all.put(ref.exps[a], 1);
        }
        for (int a = 0; a < test.exps.length; a++) {
            Object o = all.get(test.exps[a]);
            if (o == null)
                all.put(test.exps[a], 1);
        }
        double frxn = overlap_count / (double) all.size();
        /*System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
                overlap_count + "\tref.coords[0].size() " + ref.coords[0].size()
                + "\tref.coords[1].size()) " + test.coords[0].size() + "\tmin " + min);*/
        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
        }
        return frxn;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double computeBlockOverlapExpMin(ValueBlock ref, ValueBlock test) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.exps == test.exps) {
        //    System.out.println("ref and test identical");
        //}
        //for all genesStr in ref
        for (int a = 0; a < ref.exps.length; a++) {
            //if (debug)
            //    System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.exps.length; b++) {
                //  if (debug)
                //     System.out.println("a b " + a + "\t" + b);
                if (ref.exps[a] == test.exps[b]) {
                    //   if (debug)
                    //      System.out.println("computeBlockOverlapExpMin identical");
                    overlap_count++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        double min = Math.min(ref.exps.length, test.exps.length);
        double frxn = overlap_count / min;
        //System.out.println(frxn + " computeBlockOverlapGeneAndExp overlap_count " +
        //        overlap_count + "\tmin " + min);
        if (frxn > 1) {
            System.out.println("frxn > 1 " + frxn);
        }
        return frxn;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static double[] getGeneOccupancy(ValueBlock ref, ValueBlock test, double[] occup) {
        double overlap_count = 0;
        //System.out.println("ref and test len " + ref_genes.length + "\t" + test_genes.length);
        //if (ref.genes == test.genes) {
        //    System.out.println("ref and test identical");
        //}
        //for all genesStr in ref
        for (int a = 0; a < ref.genes.length; a++) {
            //System.out.println("a " + a);
            //for all genesStr in test
            for (int b = 0; b < test.genes.length; b++) {
                //System.out.println("a b " + a + "\t" + b);
                if (ref.genes[a] == test.genes[b]) {
                    //System.out.println("getGeneOccupancy identical " + a + "\tref_genes" + ref_genes[a] +
                    //       "\t" + b + "\ttest_genes " + test_genes[b]);
                    occup[a]++;
                    //go to next ref exp
                    //System.out.println("break go to next ref exp. a b c d " + a + "\t" + b);
                    break;
                }
            }
        }
        return occup;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static ValueBlockPre computeBlockOverlapWithRef(ValueBlockPre ref, ValueBlockPre test) {
        double remxcount = 0, remycount = 0;
        String ref_block_id = BlockMethods.IcJctoijID(ref.genes, ref.exps);
        int startBlockSlashInd = ref_block_id.indexOf("/");
        for (int a = 1; a < test.genes.length + 1; a++) {
            //Integer cur = (Integer) test.coords[0].get(a - 1);
            int cur = test.genes[a - 1];
            String test1 = "" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur;
            //String str = "" + cur;
            if (ref_block_id.indexOf(test1) == 0
                    || (ref_block_id.indexOf(test2) != -1 && ref_block_id.indexOf(test2) < startBlockSlashInd)
                    || ref_block_id.indexOf(test3) == startBlockSlashInd - test3.length()) {
                remxcount++;
            }
        }
        for (int b = 0; b < test.exps.length; b++) {
            //Integer cur = (Integer) test.coords[1].get(b);
            int cur = test.exps[b];
            String test1 = "/" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur;
            if (ref_block_id.indexOf(test1, startBlockSlashInd) == startBlockSlashInd
                    || (ref_block_id.indexOf(test2, startBlockSlashInd) != -1 &&
                    ref_block_id.indexOf(test2, startBlockSlashInd) > startBlockSlashInd)
                    || ref_block_id.indexOf(test3, startBlockSlashInd) == ref_block_id.length() - test3.length()) {

                remycount++;
            }
        }
        test.percentOrigGenes = remxcount / (double) ref.genes.length;
        test.percentOrigExp = remycount / (double) ref.exps.length;
        return test;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static ValueBlock computeBlockOverlapWithRef(ValueBlock ref, ValueBlock test, boolean debug) {
        double remxcount = 0, remycount = 0;
        String ref_block_id = BlockMethods.IcJctoijID(ref.genes, ref.exps);
        // int startBlockSlashInd = ref_block_id.indexOf("/");
        String delim = "/";
        int startBlockSlashInd = ref_block_id.indexOf(delim);
        /*if (startBlockSlashInd == -1) {
            delim = "/";
            startBlockSlashInd = ref_block_id.indexOf(delim);
        }*/

        for (int a = 1; a < test.genes.length + 1; a++) {
            //Integer cur = (Integer) test.coords[0].get(a - 1);
            int cur = test.genes[a - 1];
            String test1 = "" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur + "/";
            if (ref_block_id.indexOf(test1) == 0
                    || (ref_block_id.indexOf(test2) != -1 && ref_block_id.indexOf(test2) < startBlockSlashInd)
                    || ref_block_id.indexOf(test3) != -1) {
                remxcount++;
            }
        }
        for (int b = 0; b < test.exps.length; b++) {
            //Integer cur = (Integer) test.coords[1].get(b);
            int cur = test.exps[b];
            String test1 = "/" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur;
            if (ref_block_id.indexOf(test1, startBlockSlashInd) == startBlockSlashInd
                    || (ref_block_id.indexOf(test2, startBlockSlashInd) != -1 &&
                    ref_block_id.indexOf(test2, startBlockSlashInd) > startBlockSlashInd)
                    || ref_block_id.indexOf(test3, startBlockSlashInd) == ref_block_id.length() - test3.length()) {
                remycount++;
            }
        }
        test.percentOrigGenes = remxcount / (double) ref.genes.length;
        test.percentOrigExp = remycount / (double) ref.exps.length;
        if (debug)
            System.out.println("\ncomputeBlockOverlapWithRef " + test.percentOrigGenes + "\t" +
                    test.percentOrigExp + "\t" + remxcount + "\t" + remycount + "\t" + ref.genes.length + "\t" + ref.exps.length);
        return test;
    }


    /**
     * @param ref
     * @param test
     * @return
     */
    public final static int countGeneOverlapWithRef(ValueBlock ref, ValueBlock test) {
        int remxcount = 0;
        String ref_block_id = BlockMethods.IcJctoijID(ref.genes, ref.exps);
        int startBlockSlashInd = ref_block_id.indexOf("/");

        for (int a = 1; a < test.genes.length + 1; a++) {
            //Integer cur = (Integer) test.coords[0].get(a - 1);
            int cur = test.genes[a - 1];
            String test1 = "" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur + "/";
            if (ref_block_id.indexOf(test1) == 0
                    || (ref_block_id.indexOf(test2) != -1 && ref_block_id.indexOf(test2) < startBlockSlashInd)
                    || ref_block_id.indexOf(test3) != -1) {
                remxcount++;
            }
        }
        return remxcount;
    }

    /**
     * @param ref
     * @param test
     * @return
     */
    public final static int countExpOverlapWithRef(ValueBlock ref, ValueBlock test) {
        int remycount = 0;
        String ref_block_id = BlockMethods.IcJctoijID(ref.genes, ref.exps);
        String delim = "/";
        int startBlockSlashInd = ref_block_id.indexOf(delim);
        /*if (startBlockSlashInd == -1) {
            delim = "/";
            startBlockSlashInd = ref_block_id.indexOf(delim);
        }*/

        for (int b = 0; b < test.exps.length; b++) {
            //Integer cur = (Integer) test.coords[1].get(b);
            int cur = test.exps[b];
            String test1 = "/" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur;
            if (ref_block_id.indexOf(test1, startBlockSlashInd) == startBlockSlashInd
                    || (ref_block_id.indexOf(test2, startBlockSlashInd) != -1 &&
                    ref_block_id.indexOf(test2, startBlockSlashInd) > startBlockSlashInd)
                    || ref_block_id.indexOf(test3, startBlockSlashInd) == ref_block_id.length() - test3.length()) {
                remycount++;
            }
        }
        return remycount;
    }

    /**
     * @param labels
     * @param genes
     * @param exps
     * @return
     */
    public final static ArrayList[] IcJcLabelstoijID(ArrayList[] labels, String[] genes, String[] exps) {
        ArrayList[] ret = new ArrayList[2];
        ret[0] = new ArrayList();
        ret[1] = new ArrayList();
        for (int i = 0; i < labels[0].size(); i++) {
            String cur = (String) labels[0].get(i);
            int index = MoreArray.getArrayInd(genes, cur);
            ret[0].add(new Integer(index + 1));
        }
        for (int i = 0; i < labels[1].size(); i++) {
            String cur = (String) labels[1].get(i);
            int index = MoreArray.getArrayInd(exps, cur);
            ret[1].add(new Integer(index + 1));
        }
        return ret;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public final static ArrayList[] IcJcLabelstoijID(String[] genes, String[] exps) {
        ArrayList[] ret = new ArrayList[2];
        ret[0] = new ArrayList();
        ret[1] = new ArrayList();
        for (int i = 0; i < genes.length; i++) {
            ret[0].add(new Integer(genes[i]));
        }
        for (int i = 0; i < exps.length; i++) {
            ret[1].add(new Integer(exps[i]));
        }
        return ret;
    }

    /**
     * @param e
     * @param g
     * @return
     */
    public final static int[][] combineGeneExp(int[] e, int[] g) {
        int[][] ret = new int[Math.max(e.length, g.length)][2];
        for (int i = 0; i < e.length; i++) {
            ret[0][i] = e[i];
        }
        for (int j = 0; j < g.length; j++) {
            ret[1][j] = g[j];
        }
        return ret;
    }

    /**
     * @param ref_genes
     * @param move_genes
     * @return
     */
    public final static int addedGene(int[] ref_genes, int[] move_genes) {
        for (int j = 0; j < move_genes.length; j++) {
            boolean added = false;
            for (int i = 0; i < ref_genes.length; i++) {
                if (move_genes[j] == ref_genes[i]) {
                    added = true;
                    break;
                }
            }
            if (!added) {
                return move_genes[j];
            }
        }
        return -1;
    }

    /**
     * @param ref_exp
     * @param move_exps
     * @return
     */
    public final static int addedExp(int[] ref_exp, int[] move_exps) {
        for (int j = 0; j < move_exps.length; j++) {
            boolean added = false;
            for (int i = 0; i < ref_exp.length; i++) {
                if (move_exps[j] == ref_exp[i]) {
                    added = true;
                    break;
                }
            }
            if (!added) {
                return move_exps[j];
            }
        }
        return -1;
    }

    /**
     * public final static String HEADER_EVAL =
     * "criterion\tfirst re. count\tlast ref. count\tregulon_size\t" +
     * "first ref. ratio\tlast ref. ratio\tnum genes first\tnum genes last\tpercent num genes last\t" +
     * "num exps first\tnum exps last\tpercent exp last\tlast.percentOrigGenes\t" +
     * "last.percentOrigExp\tfirst.precrit\tlast.precrit\tnumber moves\tpassed\tpassed_final\t" +
     * "F1g\tprecisiong\trecallg\tF1e\tprecisione\trecalle\tprecisionge\trecallge\tF1recallge\truntime";
     *
     * @param refblock
     * @param curBlock
     * @param typelabel
     * @param geneindex
     * @param vbl
     * @param filename
     * @return
     */
    public final static double[] summarizeResults(ValueBlock refblock, ValueBlock curBlock, String typelabel,
                                                  int geneindex, ValueBlockList vbl, String filename,
                                                  int gene_dim, int exp_dim, String cur_use_crit, boolean debug) {
        double[] add = AnalyzeBlock.analyzeBlockBasicTests(refblock, vbl, typelabel, geneindex);
        //System.out.println("summarizeResults");
        //MoreArray.printArray(add);
        add[add.length - 9] = BlockMethods.computeBlockSpecificityGenes(refblock, curBlock, gene_dim);
        if (add[add.length - 9] > 1) {
            System.out.println("summarizeResults Specificity g > 1 " + add[add.length - 9] + "\t" + filename);
        }
        add[add.length - 8] = BlockMethods.computeBlockSensitivityGenes(refblock, curBlock, gene_dim);
        if (add[add.length - 8] > 1) {
            System.out.println("summarizeResults Sensitivity g > 1 " + add[add.length - 8] + "\t" + filename);
        }
        add[add.length - 10] = 2.0 / (1.0 / add[add.length - 9] + 1.0 / add[add.length - 8]);
        if (add[add.length - 10] > 1) {
            System.out.println("summarizeResults F1g > 1 " + add[add.length - 10] + "\t" + filename);
        }

        add[add.length - 6] = BlockMethods.computeBlockSpecificityExps(refblock, curBlock, exp_dim);
        if (add[add.length - 6] > 1) {
            System.out.println("summarizeResults Specificity ne > 1 " + add[add.length - 6] + "\t" + filename);
        }
        add[add.length - 5] = BlockMethods.computeBlockSensitivityExps(refblock, curBlock, exp_dim);
        if (add[add.length - 5] > 1) {
            System.out.println("summarizeResults Sensitivity e > 1 " + add[add.length - 5] + "\t" + filename);
        }
        add[add.length - 7] = 2.0 / (1.0 / add[add.length - 6] + 1.0 / add[add.length - 5]);
        if (add[add.length - 7] > 1) {
            System.out.println("summarizeResults F1e > 1 " + add[add.length - 7] + "\t" + filename);
        }

        /*double pnum = BlockMethods.computeCountOverlapGeneAndExpWithRef(refblock, curBlock);
        double pge = (pnum) / (refblock.genes.length + refblock.exps.length);
        double rge = (pnum) / (curBlock.genes.length + curBlock.exps.length);*/

        double pairnum = BlockMethods.computeBlockOverlapGeneAndExpWithRef(refblock, curBlock);
        double ppair = (pairnum) / (refblock.genes.length * refblock.exps.length);
        double rpair = (pairnum) / (curBlock.genes.length * curBlock.exps.length);

        double tp = pairnum;
        double fn = refblock.genes.length * refblock.exps.length - tp;
        double fp = curBlock.genes.length * curBlock.exps.length - tp;
        double tn = gene_dim * exp_dim - (tp + fn + fp);

        if (debug) {
            System.out.println("summarizeResults refblock g e " + refblock.genes.length + "\t" + refblock.exps.length);
            System.out.println("summarizeResults curBlock g e " + curBlock.genes.length + "\t" + curBlock.exps.length);
            System.out.println("summarizeResults tp fn fp tn " + tp + "\t" + fn + "\t" + fp + "\t" + tn);
        }

        double ge_specificity = tn / (tn + fp);
        double ge_sensitivity = tp / (tp + fn);
        if (debug)
            System.out.println("summarizeResults ge_specificity ge_sensitivity " + ge_specificity + "\t" + ge_sensitivity);

        add[add.length - 4] = ge_specificity;
        if (add[add.length - 4] > 1) {
            System.out.println("summarizeResults specificity ge > 1 " + ge_specificity + "\t" + filename);
        }
        add[add.length - 3] = ge_sensitivity;
        if (add[add.length - 3] > 1) {
            System.out.println("summarizeResults sensitivity ge > 1 " + ge_sensitivity + "\t" + filename);
        }
        add[add.length - 2] = 2 * (ppair * rpair) / (ppair + rpair);
        if (add[add.length - 2] > 1) {
            System.out.println("summarizeResults F1ge > 1 " + add[add.length - 2] + "\t" + filename);
        }

        /* if (cur_use_crit != null && (cur_use_crit.equals("external"))) {
            System.out.println(file);
            System.out.println("g spec " + add[add.length - 9] + "\tsens " + add[add.length - 8]);
            System.out.println("e spec " + add[add.length - 6] + "\tsens " + add[add.length - 5]);
            System.out.println("ge spec " + add[add.length - 4] + "\tsens " + add[add.length - 3]);
        }*/

        if (vbl != null && vbl.runtime != -1) {
            add[add.length - 1] = vbl.runtime;
        }

        return add;
    }

    /**
     * @param blockIds
     * @param genes
     * @return
     */
    public final static int[][] cooccurGenes(ArrayList blockIds, int genes) {
        int[][] ret = new int[genes][genes];
        HashMap hm = new HashMap();
        for (int j = 0; j < blockIds.size(); j++) {
            String s = (String) blockIds.get(j);
            int[][] indices = BlockMethods.ijIDtoIcJc(s);
            for (int a = 0; a < indices[0].length; a++) {
                for (int b = a; b < indices[0].length; b++) {
                    String key = indices[0][a] + "_" + indices[0][b];
                    Object o = hm.get(key);
                    if (o == null) {
                        hm.put(key, 1);
                    } else {
                        int cur = (Integer) o;
                        hm.put(key, cur + 1);
                    }
                }
            }
        }
        Set set = hm.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String k = (String) cur.getKey();
            int v = (Integer) cur.getValue();
            int und = k.indexOf("_");
            int a = Integer.parseInt(k.substring(0, und));
            int b = Integer.parseInt(k.substring(und + 1));
            ret[a - 1][b - 1] = v;
            if (a != b)
                ret[a - 1][b - 1] = v;
        }
        return ret;
    }

    /**
     * @param blockIds
     * @param exps
     * @return
     */
    public final static int[][] cooccurExps(ArrayList blockIds, int exps) {
        int[][] ret = new int[exps][exps];
        HashMap hm = new HashMap();
        for (int j = 0; j < blockIds.size(); j++) {
            String s = (String) blockIds.get(j);
            int[][] indices = BlockMethods.ijIDtoIcJc(s);
            for (int a = 0; a < indices[1].length; a++) {
                for (int b = a; b < indices[1].length; b++) {
                    String key = indices[1][a] + "_" + indices[1][b];
                    Object o = hm.get(key);
                    if (o == null) {
                        hm.put(key, 1);
                    } else {
                        int cur = (Integer) o;
                        hm.put(key, cur + 1);
                    }
                }
            }
        }
        Set set = hm.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String k = (String) cur.getKey();
            int v = (Integer) cur.getValue();
            int und = k.indexOf("_");
            int a = Integer.parseInt(k.substring(0, und));
            int b = Integer.parseInt(k.substring(und + 1));
            ret[a - 1][b - 1] = v;
            if (a != b)
                ret[a - 1][b - 1] = v;
        }
        return ret;
    }

    /**
     * @param blockIds
     * @param exps
     * @return
     */
    public final static int[][] cooccurGenesandExps(ArrayList blockIds, int genes, int exps) {
        int[][] ret = new int[genes * exps][genes * exps];
        HashMap hm = new HashMap();
        for (int j = 0; j < blockIds.size(); j++) {
            String s = (String) blockIds.get(j);
            int[][] indices = BlockMethods.ijIDtoIcJc(s);

            for (int a = 0; a < indices[0].length; a++) {
                for (int c = 0; c < indices[1].length; c++) {
                    for (int b = a + 1; b < indices[0].length; b++) {
                        for (int d = c + 1; d < indices[1].length; d++) {
                            String key = indices[0][a] + "_" + indices[1][c] + "x" + indices[0][b] + "_" + indices[1][d];
                            Object o = hm.get(key);
                            if (o == null) {
                                hm.put(key, 1);
                            } else {
                                int cur = (Integer) o;
                                hm.put(key, cur + 1);
                            }
                        }
                    }
                }
            }
        }
        Set set = hm.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String k = (String) cur.getKey();
            int v = (Integer) cur.getValue();
            int xind = k.indexOf("x");
            int undind = k.indexOf("_");
            int undind2 = k.indexOf("_", undind + 1);
            int a = Integer.parseInt(k.substring(0, undind));
            int c = Integer.parseInt(k.substring(undind + 1, xind));
            int b = Integer.parseInt(k.substring(xind + 1, undind2));
            int d = Integer.parseInt(k.substring(undind2 + 1));
            int x = c - 1 + (a - 1) * exps;
            int y = d - 1 + (b - 1) * exps;
            ret[x][y] = v;
            if (a != b && c != d) {
                ret[y][x] = v;
            }
        }

        int[] colsums = Matrix.sumOverRows(ret);
        int[] rowsums = Matrix.sumOverCols(ret);
        ArrayList remc = new ArrayList();
        for (int i = 0; i < colsums.length; i++) {
            if (colsums[i] == 0) {
                remc.add(new Integer(i + 1));
            }
        }
        ret = Matrix.removeColumns(ret, remc);
        ArrayList remr = new ArrayList();
        for (int i = 0; i < rowsums.length; i++) {
            if (rowsums[i] == 0) {
                remr.add(new Integer(i + 1));
            }
        }
        ret = Matrix.removeRows(ret, remr);
        return ret;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public final static ValueBlock mergeBlocks(ValueBlock a, ValueBlock b) {

        ArrayList genes = MoreArray.convtoArrayList(a.genes);
        ArrayList exps = MoreArray.convtoArrayList(a.exps);
        for (int i = 0; i < b.genes.length; i++) {
            if (genes.indexOf(b.genes[i]) == -1)
                genes.add(b.genes[i]);
        }
        for (int i = 0; i < b.exps.length; i++) {
            if (exps.indexOf(b.exps[i]) == -1)
                exps.add(b.exps[i]);
        }
        ValueBlock vb = new ValueBlock(genes, exps);

        return vb;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public final static ValueBlock intersectBlocks(ValueBlock a, ValueBlock b) {

        ArrayList genes = new ArrayList();
        ArrayList exps = new ArrayList();
        for (int i = 0; i < b.genes.length; i++) {
            int arrayInd = -1;
            try {
                arrayInd = MoreArray.getArrayInd(a.genes, b.genes[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (arrayInd != -1)
                genes.add(b.genes[i]);
        }
        for (int i = 0; i < b.exps.length; i++) {
            int arrayInd = -1;
            try {
                arrayInd = MoreArray.getArrayInd(a.exps, b.exps[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (arrayInd != -1)
                exps.add(b.exps[i]);
        }
        ValueBlock vb = new ValueBlock(genes, exps);

        return vb;
    }

    /**
     * @param tomark
     * @param marks
     * @return
     */
    public final static int[][] markGeneExps(ValueBlock tomark, int[][] marks, ValueBlock ref) {
        if (marks == null) {
            marks = MoreArray.initArray(tomark.genes.length, tomark.exps.length, 1);
        } else {
            for (int i = 0; i < tomark.genes.length; i++) {
                int gind = -1;
                try {
                    gind = MoreArray.getArrayInd(ref.genes, tomark.genes[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (gind != -1)
                    for (int j = 0; j < tomark.exps.length; j++) {
                        int eind = -1;
                        try {
                            eind = MoreArray.getArrayInd(ref.exps, tomark.exps[j]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (eind != -1) {
                            try {
                                marks[gind][eind]++;
                            } catch (Exception e) {
                                System.out.println("gind " + gind + "\teind " + eind);
                                System.out.println("marks " + marks.length + "\t" + marks[0].length);
                                System.out.println("ref " + ref.genes.length + "\t" + ref.exps.length);
                                e.printStackTrace();
                            }
                        }
                    }
            }
        }
        return marks;
    }


    /**
     * @param counts
     * @param oldgenes
     * @param oldexps
     * @param newgenes
     * @param newexps
     * @return
     */
    public final static int[][] expandCounts(int[][] counts, int[] oldgenes, int[] oldexps, int[] newgenes, int[] newexps) {
        int[][] ret = new int[newgenes.length][newexps.length];
        for (int i = 0; i < ret.length; i++) {
            int a = -1;
            try {
                a = MoreArray.getArrayInd(oldgenes, newgenes[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (a != -1) {
                for (int j = 0; j < ret[0].length; j++) {
                    int b = -1;
                    try {
                        b = MoreArray.getArrayInd(oldexps, newexps[j]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (b != -1)
                        ret[i][j] = counts[a][b];
                }
            }
        }
        return ret;
    }


    /**
     * @param genes
     * @param exps
     * @param refgenes
     * @param refexps
     * @return
     */
    public final static boolean isGeneMove(int[] genes, int[] exps, int[] refgenes, int[] refexps) {
        boolean ret = true;
        if (genes.length > refgenes.length || genes.length < refgenes.length) {
            ret = true;
        } else if (exps.length > refexps.length || exps.length < refexps.length)
            ret = false;
        return ret;
    }

    /**
     * @param genes
     * @param exps
     * @param refgenes
     * @param refexps
     * @return
     */
    public final static int[] findDifference(int[] genes, int[] exps, int[] refgenes, int[] refexps, boolean isGene) {
        ArrayList ret = new ArrayList();
        if (isGene) {
            if (genes.length > refgenes.length) {
                for (int i = 0; i < refgenes.length; i++) {
                    int arrayInd = -1;
                    try {
                        arrayInd = MoreArray.getArrayInd(genes, refgenes[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (arrayInd == -1) {
                        ret.add(refgenes[i]);
                    }
                }
            } else if (genes.length < refgenes.length) {
                for (int i = 0; i < genes.length; i++) {
                    int arrayInd = -1;
                    try {
                        arrayInd = MoreArray.getArrayInd(refgenes, genes[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (arrayInd == -1) {
                        ret.add(genes[i]);
                    }
                }
            }
        } else {
            if (exps.length > refexps.length) {
                for (int i = 0; i < refexps.length; i++) {
                    int arrayInd = -1;
                    try {
                        arrayInd = MoreArray.getArrayInd(exps, refexps[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (arrayInd == -1) {
                        ret.add(refexps[i]);
                    }
                }
            } else if (exps.length < refexps.length) {
                for (int i = 0; i < exps.length; i++) {
                    int arrayInd = -1;
                    try {
                        arrayInd = MoreArray.getArrayInd(refexps, exps[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (arrayInd == -1) {
                        ret.add(exps[i]);
                    }
                }
            }
        }

        return MoreArray.ArrayListtoInt(ret);
    }


    /**
     * @param v
     * @param expr_data
     * @param abs_axis
     * @return
     */
    public final static ValueBlock[] splitBlockByExprMean(ValueBlock v, double[][] expr_data, String abs_axis,
                                                          double gmiss, double emiss) {

        v.setDataAndMean(expr_data);

        ArrayList[] newcoordp = MoreArray.initArrayListArray(2);
        ArrayList[] newcoordn = MoreArray.initArrayListArray(2);

        if (abs_axis.equals("R")) {
            for (int j = 0; j < v.genes.length; j++) {
                double m = stat.avg(v.exp_data[j]);
                if (m >= 0) {
                    newcoordp[0].add(v.genes[j]);
                } else if (m < 0) {
                    newcoordn[0].add(v.genes[j]);
                }
            }

            for (int j = 0; j < v.exps.length; j++) {
                newcoordp[1].add(v.exps[j]);
                newcoordn[1].add(v.exps[j]);
            }
        } else if (abs_axis.equals("C")) {
            for (int j = 0; j < v.exps.length; j++) {
                double m = stat.avg(Matrix.extractColumn(v.exp_data, j));
                if (m >= 0) {
                    newcoordp[1].add(v.exps[j]);
                } else if (m < 0) {
                    newcoordn[1].add(v.exps[j]);
                }
            }
            for (int j = 0; j < v.genes.length; j++) {
                newcoordp[0].add(v.genes[j]);
                newcoordn[0].add(v.genes[j]);
            }
        }

        ValueBlock[] ret = new ValueBlock[2];
        ret[0] = new ValueBlock(v);
        ret[0].update(newcoordp);
        ret[0].setDataAndMean(expr_data);

        ret[1] = new ValueBlock(v);
        ret[1].update(newcoordn);
        ret[1].setDataAndMean(expr_data);
        return ret;
    }


    /**
     *
     */
    public final static ValueBlock makeGeneExps(ValueBlock v, int[] g, int[] e) {
        ValueBlock newv = new ValueBlock(v, g, e);
        return newv;
    }

    /**
     *
     */
    public final static ValueBlock makeNRGeneExps(ValueBlock v) {
        Set set1 = new HashSet(Arrays.asList(MoreArray.toStringArray(v.genes)));
        int[] g = MoreArray.tointArray((String[]) set1.toArray(new String[0]));

        Set set2 = new HashSet(Arrays.asList(MoreArray.toStringArray(v.exps)));
        int[] e = MoreArray.tointArray((String[]) set2.toArray(new String[0]));
        ValueBlock newv = new ValueBlock(v, g, e);
        return newv;
    }
}