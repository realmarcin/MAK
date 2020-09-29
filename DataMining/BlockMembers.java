package DataMining;

import mathy.stat;
import util.MoreArray;
import util.TabFile;

import java.util.*;

/**
 * User: marcin
 * Date: Jan 21, 2011
 * Time: 11:20:42 AM
 */
public class BlockMembers {

    final static String version = "0.1";
    HashMap members = new HashMap();

    String dirstr;
    String startstr;
    String outstr;

    public int[][] hammingdistblock;

    String[] xlabels, ylabels;

    String targetcrit = "__MSEC_KendallC_GEECE__";

    /**
     * @param args
     */
    public BlockMembers(String[] args) {
        dirstr = args[0];

        if (args.length == 3) {
            targetcrit = args[2];
        }
        if (args.length == 4) {
            startstr = args[3];
        }
        outstr = args[1] + targetcrit;

        run();

        System.out.println("Done!");
    }

    /**
     * @param h
     * @param o
     */
    public BlockMembers(HashMap h, String o) {
        members = h;
        outstr = o + targetcrit;

        run();
    }

    /**
     *
     */
    private void run() {

        ValueBlockList[] vbl = ValueBlockListMethods.getFirstandLast(dirstr, startstr, targetcrit, outstr);
        ValueBlockList blocksetstart = vbl[0];
        ValueBlockList blocksetlast = vbl[1];

        for (int i = 0; i < blocksetlast.size(); i++) {
            if (i % 100 == 0)
                System.out.print("\n");
            System.out.print(".");

            ValueBlock lastadd = (ValueBlock) blocksetlast.get(i);
            //ValueBlock start = (ValueBlock) blocksetstart.get(i);

            for (int a = 0; a < lastadd.genes.length; a++) {
                for (int b = 0; b < lastadd.exps.length; b++) {
                    String key = "" + lastadd.genes[a] + "_" + lastadd.exps[b];
                    Object get = members.get(key);
                    if (get == null)
                        members.put(key, "" + i);
                    else {
                        String s = (String) get;
                        s += "_" + i;
                        members.put(key, s);
                    }
                }
            }
        }
        System.out.print("\n");

        System.out.println("gene_exp pairs " + (members.keySet()).size());

        String outbic = "";
        int slashind = dirstr.lastIndexOf("/");
        if (slashind > 1 || (dirstr.startsWith("./") && slashind != 1)) {
            outbic = slashind == dirstr.length() - 1 ? dirstr.substring(0, dirstr.length() - 1) : dirstr;
        } else
            outbic = dirstr;
        String outbic1 = outbic + ".bic";
        System.out.println("writing blocksetlast " + outbic1);
        ValueBlockListMethods.writeBIC(outbic1, blocksetlast);
        String outbic2 = outbic + "_start.bic";
        System.out.println("writing blocksetstart " + outbic2);
        ValueBlockListMethods.writeBIC(outbic2, blocksetstart);

        System.out.println("size " + blocksetlast.size());
        doHamming(blocksetlast.size());
    }

    /**
     * @param numblocks
     */
    private void doHamming(int numblocks) {
        int count = 0;
        Set set = members.entrySet();
        Iterator it = set.iterator();

        //remove singleton gene_exp pairs, i.e. belonging to only one block
        ArrayList rem = new ArrayList();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String gene_exp_pair = (String) cur.getKey();
            String val = (String) cur.getValue();
            int countblocks = val.indexOf("_");
            //if missing concatenation symbol
            if (countblocks == -1) {
                rem.add(gene_exp_pair);
            }
        }
        for (int i = 0; i < rem.size(); i++) {
            String s = (String) rem.get(i);
            members.remove(s);
        }

        Set ks = members.keySet();
        int ysize = ks.size();
        int xsize = numblocks;

        ylabels = new String[ysize];
        xlabels = MoreArray.toStringArray(stat.createNaturalNumbers(1, xsize + 1));

        int[][] out = new int[ysize][xsize];
        System.out.println("keys " + xsize + "\tblocks " + ysize);
        System.out.println("sizes 1 " + out.length + "\t" + ylabels.length + "\t" + out[0].length +
                "\t" + xlabels.length);

        set = members.entrySet();
        it = set.iterator();
        while (it.hasNext()) {
            if (count % 100 == 0)
                System.out.print("\n");
            System.out.print(",");

            Map.Entry cur = (Map.Entry) it.next();
            String gene_exp_pair = (String) cur.getKey();
            ylabels[count] = gene_exp_pair;

            String val = (String) cur.getValue();
            if (gene_exp_pair.length() == 0) {
                System.out.println("gene_exp_pair label is empty " + count + "\t" + val);
            }
            String[] blocks = val.split("_");
            for (int i = 0; i < blocks.length; i++) {
                int curb = Integer.parseInt(blocks[i]);
                out[count][curb]++;
            }
            count++;
        }
        System.out.print("\n");

        //System.out.println("WARNING not outputing count matrix");
        System.out.println("sizes 2 " + out.length + "\t" + out[0].length + "\t" + xsize + "\t" + ysize);
        String countf = outstr + "geneexp_member.txt";
        System.out.println("writing " + countf);
        TabFile.write(out, countf, xlabels, ylabels);
        System.out.println("wrote " + countf);
        members = null;

        /*hammingdistblock = new int[xsize][xsize];
        for (int i = 0; i < xsize; i++) {
            if (i % 100 == 0) {
                System.out.print(".");
            }
            for (int j = i; j < xsize; j++) {
                if (i == j) {
                    hammingdistblock[i][j] = 0;
                } else {
                    int d = 0;
                    for (int a = 0; a < ysize; a++) {
                        if (out[a][i] != out[a][j]) {
                            d++;
                        }
                    }
                    hammingdistblock[i][j] = d;
                    hammingdistblock[j][i] = d;
                }

            }
        }
        System.out.print("\n");
        String s = outstr + "hammingblocks.txt";
        System.out.println("writing " + s);
        TabFile.write(hammingdistblock, s, xlabels, xlabels);
        System.out.println("wrote " + s);*/

        //crashes jvm
        /* out = new int[ysize][ysize];

     while (it.hasNext()) {
         if (count % 100 == 0)
             System.out.print("\n");
         System.out.print(",");

         Map.Entry cur = (Map.Entry) it.next();
         String gene_exp_pair = (String) cur.getKey();
         ylabels[count] = gene_exp_pair;
         String[] blocks = ((String) cur.getValue()).split("_");
         for (int i = 0; i < blocks.length; i++) {
             int curb = Integer.parseInt(blocks[i]);
             out[count][curb]++;
         }
         count++;
     }
     System.out.print("\n");


     System.out.println("sizes 2 " + out.length + "\t" + out[0].length + "\t" + xsize + "\t" + ysize);
     String outf2 = outstr + "_geneexp_member.txt";
     System.out.println("writing " + outf2);
     TabFile.write(MoreArray.toString(out, "", ""), outf2, ylabels, ylabels);
     System.out.println("wrote " + outf2);*/
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 2 || args.length == 3 || args.length == 4) {
            BlockMembers rm = new BlockMembers(args);
        } else {
            System.out.println("BlockMembers version " + version);
            System.out.println("syntax: java DataMining.BlockMembers\n" +
                    "<dir of dirs of trajectories or list file of final blocks>\n" +
                    "<outfile>\n" +
                    "<OPTIONAL target crit e.g. '__GEERE__' >\n" +
                    "<OPTIONAL list file of start blocks>"
            );
        }
    }
}
