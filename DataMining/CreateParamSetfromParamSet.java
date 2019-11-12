package DataMining;

import util.MoreArray;
import util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Class to create a set of parameters files based on a parameter template and a set of parameter files. The set of parameter
 * files provides the initial block and random seed info.
 * <p/>
 * User: marcinjoachimiak
 * Date: Apr 23, 2008
 * Time: 11:12:20 AM
 */
public class CreateParamSetfromParamSet {


    /**
     * @param args
     */
    public CreateParamSetfromParamSet(String[] args) {
        File testtemp = new File(args[0]);
        if (!testtemp.exists()) {
            System.out.println("parameter file does not exist");
            System.exit(0);
        }

        Parameters template = null;
        try {
            template = new Parameters(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("CreateParamSetfromParamSet\n" + template);

        File dir = new File(args[1]);
        String outdir = args[2];
        File test = new File(outdir);
        if (!test.exists() || !test.isDirectory()) {
            boolean create = test.mkdirs();
            System.out.println("Created new file with outcome " + create);
        }
        String[] list = dir.list();
        for (int i = 0; i < list.length; i++) {
            String cur = list[i];
            int dashind = cur.indexOf("-");
            int lastundind = cur.lastIndexOf("_");
            int prevlastundind = StringUtil.lastIndexBefore(cur, "_", lastundind);
            int lastdotind = cur.lastIndexOf(".");
            System.out.println(i + "\t" + cur + "\tdashind " + dashind + "\tlastundind " + lastundind + "\tlastdotind " + lastdotind);
            int seed = Integer.parseInt(cur.substring(prevlastundind + 1, lastundind));
            System.out.println(i + "\t" + cur + "\tdashind " + dashind + "\tlastundind " + lastundind + "\tlastdotind " + lastdotind + "\tseed " + seed);
            String genesStr = cur.substring(0, dashind);
            String expsStr = cur.substring(dashind + 1, lastundind);
            String[] genes = StringUtil.parsetoArray(genesStr, "_");
            String[] exps = StringUtil.parsetoArray(expsStr, "_");
            int[] genesInt = MoreArray.tointArray(genes);
            int[] expsInt = MoreArray.tointArray(exps);
            Parameters out = new Parameters(template);
            out.RANDOM_SEED = seed;
            out.init_block_list = new ArrayList();
            out.INIT_BLOCKS = new ArrayList[2];
            out.INIT_BLOCKS[0] = MoreArray.convtoArrayList(genesInt);
            out.INIT_BLOCKS[1] = MoreArray.convtoArrayList(expsInt);
            //remove last element which is actually random seed
            //out.INIT_BLOCKS[1].remove(out.INIT_BLOCKS[1].size() - 1);
            out.init_block_list.add(out.INIT_BLOCKS);
            String outfile = outdir + "/" + cur;
            out.write(outfile);
            System.out.println("wrote " + outfile);
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            CreateParamSetfromParamSet rm = new CreateParamSetfromParamSet(args);
        } else {
            System.out.println("syntax: java DataMining.CreateParamSetfromParamSet\n" +
                    "<template parameter file>\n" +
                    "<dir of parameter files>\n" +
                    "<out dir>");
        }
    }
}
