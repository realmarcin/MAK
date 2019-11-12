package DataMining;

import DataMining.eval.EvaluateBasic;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * User: mjoachimiak
 * Date: Jul 29, 2009
 * Time: 11:41:45 AM
 */
public class RecomputeTrajectory {


    /**
     * @param args
     */
    public RecomputeTrajectory(String[] args) {

        File f = new File(args[0]);
        String[] list = f.list();
        for (int i = 0; i < list.length; i++) {
            if (list[i].indexOf("toplist") != -1) {
                String path = args[0] + "/" + list[i];
                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read(path, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < vbl.size(); j++) {

                    ValueBlock vb = (ValueBlock) vbl.get(j);
                    ValueBlock test = BlockMethods.computeBlockOverlapWithRef(EvaluateBasic.true1, vb);
                    vb.percentOrigGenes = test.percentOrigGenes;
                    vb.percentOrigExp = test.percentOrigExp;
                    vbl.set(j, vb);
                }
                ValueBlock v = new ValueBlock(EvaluateBasic.true1);
                v.move_type = 0;
                v.exp_mean = 0;
                v.percentOrigGenes = 1.0;
                v.percentOrigExp = 1.0;
                v.pre_criterion = 1.0;
                v.full_criterion = 1.0;
                v.all_criteria = new double[ValueBlock_STATIC.NUM_CRIT];
                v.all_criteria[ValueBlock_STATIC.expr_MEAN_IND] = 1.0;
                v.all_criteria[ValueBlock_STATIC.expr_MSE_IND] = 1.0;
                v.all_criteria[ValueBlock_STATIC.expr_FEM_IND] = 1.0;
                v.all_criteria[ValueBlock_STATIC.expr_KEND_IND] = 1.0;
                v.all_criteria[ValueBlock_STATIC.expr_COR_IND] = 1.0;
                v.all_criteria[ValueBlock_STATIC.interact_IND] = 1.0;
                v.all_criteria[ValueBlock_STATIC.feat_IND] = 1.0;
                v.all_criteria[ValueBlock_STATIC.TF_IND] = 1.0;

                vbl.add(0, v);
                try {
                    String s = args[0] + "/" + list[i] + "_recenter1.txt";
                    System.out.println("writing " + s);
                    PrintWriter pw = new PrintWriter(new FileWriter(s));
                    pw.println(MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
                    for (int k = 0; k < vbl.size(); k++) {
                        ValueBlock cur = (ValueBlock) vbl.get(k);
                        String s1 = cur.toStringShort();
                        pw.println("" + (k + 1) + "\t" + s1);
                    }
                    pw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            RecomputeTrajectory rm = new RecomputeTrajectory(args);
        } else {
            System.out.println("syntax: java DataMining.RecomputeTrajectory\n" +
                    "<in dir>\n"
            );
        }
    }

}
