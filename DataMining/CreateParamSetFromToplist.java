package DataMining;

import util.FileFilter;
import util.MoreArray;

import java.io.File;

/**
 * User: marcin
 * Date: May 5, 2009
 * Time: 6:31:54 PM
 */
public class CreateParamSetFromToplist {

//RAUF
    /**
     * @param args
     */
    public CreateParamSetFromToplist(String[] args) {
        File dir = new File(args[0]);
        String outdir = args[1];
        File test = new File(outdir);
        if (!test.exists() || !test.isDirectory()) {
            boolean create = test.mkdirs();
            System.out.println("Created new file with outcome " + create);
        }

        FileFilter tops = new FileFilter("toplist.txt");
        FileFilter params = new FileFilter("parameters.txt");

        String[] list_top = dir.list(tops);
        String[] list_params = dir.list(params);
        for (int i = 0; i < list_top.length; i++) {
            String curtop = list_top[i];
            int und = curtop.indexOf("_");
            und = curtop.indexOf("_", und + 1);
            String search = curtop.substring(0, und);
            int pindex = MoreArray.getArrayIndexOfAtZero(list_params, search);

            ValueBlockList vl = null;
            try {
                vl = ValueBlockList.read(args[0] + "/" + list_top[i], false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Parameters prm = new Parameters();
            prm.read(args[0] + "/" + list_params[pindex]);

            ValueBlock last = (ValueBlock) vl.get(vl.size() - 1);
            System.out.println("exps " + last.exps);
            prm.INIT_BLOCKS[1] = MoreArray.convtoArrayList(last.exps);
            prm.FIX_GENES = false;
            prm.FIX_EXPS = true;
            String outfile = outdir + "/" + list_params[pindex];
            prm.write(outfile);
            System.out.println("wrote " + outfile);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            CreateParamSetFromToplist rm = new CreateParamSetFromToplist(args);
        } else {
            System.out.println("syntax: java DataMining.CreateParamSetFromToplist\n" +
                    "<dir of parameter and toplist files>\n" +
                    "<out dir>");
        }
    }
}
