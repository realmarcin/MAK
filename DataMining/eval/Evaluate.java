package DataMining.eval;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.MoreArray;

import java.io.File;

/**
 * User: marcin
 * Date: Nov 3, 2009
 * Time: 6:00:51 PM
 */
public class Evaluate {

    public final static String[] external_methods = {"CC", "HCL", "ISA", "eans", "OPSM", "xMotif", "SAMBA", "QUBIC", "FABIA", "Starts"};
    public final static String[] external_methods_full = {"CC", "HCL", "ISA", "K-means", "OPSM", "xMotif", "SAMBA", "QUBIC", "FABIA", "Starts"};

    public ValueBlockList[] external_results = new ValueBlockList[external_methods.length];
    public int[][] maxexternal = new int[external_methods.length][];

    /**
     * @param path
     */
    public Evaluate(String path, ValueBlockList refblocks, boolean doMax, int gene_dim, int exp_dim) {
        //maxexternal = MoreArray.initArray(8, -1);
        maxexternal = MoreArray.initArray(external_methods.length, refblocks.size(), -1);
        double[][][] maxval = new double[external_methods.length][refblocks.size()][2];
        double[][][] maxTFPN = new double[external_methods.length][refblocks.size()][4];

        if (path != null)
            readExternal(path);

        if (doMax) {
            //for each external method
            for (int a = 0; a < external_methods.length; a++) {
                if (external_results[a] != null) {
                    //for all reference blocks
                    for (int j = 0; j < refblocks.size(); j++) {
                        ValueBlock refblock = (ValueBlock) refblocks.get(j);
                        //for all blocks for each external method
                        double max = 0;
                        //System.out.println("external_results[a].size() " + external_results[a].size());
                        for (int i = 0; i < external_results[a].size(); i++) {
                            ValueBlock curBlock = (ValueBlock) external_results[a].get(i);

                            double tp = BlockMethods.computeBlockOverlapGeneAndExpWithRef(refblock, curBlock);
                            double fn = refblock.genes.length * refblock.exps.length - tp;
                            double fp = curBlock.genes.length * curBlock.exps.length - tp;
                            double tn = gene_dim * exp_dim - (tp + fn + fp);

//                            System.out.println("Evaluate " + external_methods[a] + "\ttrue" + (j + 1) +
//                                    "\ttp " + tp + "\ttn " + tn + "\tfp " + fp + "\tfn " + fn);

                            double specificity = tn / (tn + fp);
                            double sensitivity = tp / (tp + fn);
                            double sum = specificity + sensitivity;
//                            System.out.println("Evaluate " + external_methods[a] + "\ttrue" + (j + 1) +
//                                    "\tFP " + (1 - specificity) + "\tTP " + sensitivity + "\tsum " + sum);
                            if (sum > max && sensitivity != 0 && specificity != 0) {
                                //System.out.println("Evaluate new max " + max + "\t" + sum);
                                max = sum;
                                maxval[a][j][0] = specificity;
                                maxval[a][j][1] = sensitivity;
                                maxTFPN[a][j][0] = tp;
                                maxTFPN[a][j][1] = tn;
                                maxTFPN[a][j][2] = fp;
                                maxTFPN[a][j][3] = fn;
                                maxexternal[a][j] = i;
                            }
                        }
                        if (maxexternal[a][j] == -1) {
                            System.out.println("Evaluate no max " + external_methods[a] + "\t" + maxexternal[a][j]);
                        }
                    }
                } else {
                    System.out.println("no results for " + external_methods[a]);
                }
            }
        }

        for (int j = 0; j < refblocks.size(); j++) {
            for (int a = 0; a < external_methods.length; a++) {
                System.out.println("Evaluate refblock " + (j + 1) + "\t" +
                        external_methods[a] + "\tmax " + maxexternal[a][j] +
                        "\tmaxspec " + maxval[a][j][0] + "\tmaxsens " + maxval[a][j][1]);
                // System.out.println("Evaluate refblock tp " + maxTFPN[a][j][0] + "\ttn " + maxTFPN[a][j][1] + "\tfp " +
                //       maxTFPN[a][j][2] + "\tfn " + maxTFPN[a][j][3]);
            }
        }
    }

    /**
     * @param path
     */
    private void readExternal(String path) {
        System.out.println("readExternal " + path);
        File dir = new File(path);
        String[] list = dir.list();
        for (int i = 0; i < list.length; i++) {
            int type = Evaluate.getExtMethod(list[i]);
            String f = path + "/" + list[i];

            System.out.println("readExternal " + f);
            try {
                external_results[type] = ValueBlockListMethods.readBICTranslatetoInt(f);
            } catch (Exception e) {
                try {
                    external_results[type] = ValueBlockListMethods.readSimple(f);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (external_results[type] != null)
                System.out.println("readExternal " + external_results[type].size());
            else
                System.out.println("readExternal results are null");
        }
    }


    /**
     * @param s
     * @return
     */

    public final static int getExtMethod(String s) {
        for (int i = 0; i < external_methods.length; i++) {
            if (s.indexOf(external_methods[i]) != -1)
                return i;
        }
        return -1;
    }
}
