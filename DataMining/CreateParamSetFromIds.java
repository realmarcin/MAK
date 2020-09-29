package DataMining;

import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.TabFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 9/8/17
 * Time: 8:07 PM
 */
public class CreateParamSetFromIds {

    String[] valid_args = {
            "-param", "-data", "-xids", "-yids"
    };

    Parameters prm;

    String inparam;
    HashMap options;

    String[] yids, xids;
    SimpleMatrix expr_data;

    int mode = 1;

    boolean silent = false;


    /**
     * @param args
     */
    public CreateParamSetFromIds(String[] args) {

        init(args);

        System.out.println("expr_data.ylabels");
        MoreArray.printArray(expr_data.ylabels);

        System.out.println("expr_data.xlabels");
        MoreArray.printArray(expr_data.xlabels);

        ArrayList ylist = new ArrayList();
        for (int i = 0; i < yids.length; i++) {
            int index = MoreArray.getArrayInd(expr_data.ylabels, "\"" + yids[i] + "\"");
            if (index != -1)
                ylist.add(index + 1);
            else
                System.out.println(yids[i] + "\t" + index);
        }

        ArrayList xlist = new ArrayList();
        for (int i = 0; i < xids.length; i++) {
            int index = MoreArray.getArrayInd(expr_data.xlabels, "\"" + xids[i] + "\"");
            if (index != -1)
                xlist.add(index + 1);
            else
                System.out.println(xids[i] + "\t" + index);
        }

        prm.init_block_list = new ArrayList();
        prm.INIT_BLOCKS = new ArrayList[2];
        prm.INIT_BLOCKS[0] = ylist;
        prm.INIT_BLOCKS[1] = xlist;
        prm.init_block_list.add(prm.INIT_BLOCKS);

        String outfile = inparam + "_new_parameters.txt";
        prm.write(outfile);

        System.out.println("wrote " + outfile);
    }


    /**
     * @param args
     */

    private void init(String[] args) {

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-param") != null) {
            inparam = (String) options.get("-param");
            prm = new Parameters();
            prm.read(inparam, false);
            if (!silent) System.out.println(prm.toString());
        } else {
            if (!silent) System.out.println("no parameter file specified, quiting");
            System.exit(0);
        }

        if (options.get("-data") != null) {
            expr_data = new SimpleMatrix((String) options.get("-data"));

        }
        if (options.get("-yids") != null) {
            TabFile tf = new TabFile();
            yids = MoreArray.extractColumnStr(tf.readtoArray((String) options.get("-yids")), 1);
            System.out.println("yids");
            MoreArray.printArray(yids);
        }
        if (options.get("-xids") != null) {
            TabFile tf = new TabFile();
            xids = MoreArray.extractColumnStr(tf.readtoArray((String) options.get("-xids")), 1);
            System.out.println("xids");
            MoreArray.printArray(xids);
        }

    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 8) {
            CreateParamSetFromIds rm = new CreateParamSetFromIds(args);
        } else {
            System.out.println("syntax: java DataMining.CreateParamSet\n" +
                    "<-param OPTIONAL parameter file>\n" +
                    "<-data input data file with ids>\n" +
                    "<-yids 2 column txt file id,label>\n" +
                    "<-xids 2 column txt file id,label>\n"

            );
        }
    }
}

