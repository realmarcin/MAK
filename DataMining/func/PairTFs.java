package DataMining.func;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.ParsePath;
import util.StringUtil;

import java.util.HashMap;

/**
 * Skeleton class, not functional.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Sep 18, 2012
 * Time: 11:08:25 PM
 */
public class PairTFs {

    String[] valid_args = {
            "-bic", "-TFREF", "-out", "-debug"
    };

    SimpleMatrix TFREF_data;
    HashMap options;
    String outfile;
    String inBIC;
    ValueBlockList BIC;
    int bicluster_set_size;
    ParsePath pp;


    /**
     * @param args
     */
    public PairTFs(String[] args) {

        init();
    }

    /**
     *
     */
    public void init() {
        if (options.get("-TFREF") != null) {
            String f = (String) options.get("-TFREF");
            TFREF_data = new SimpleMatrix(f);

            TFREF_data.xlabels = StringUtil.replace(TFREF_data.xlabels, "\"", "");
            TFREF_data.ylabels = StringUtil.replace(TFREF_data.ylabels, "\"", "");
            //System.out.println("motif_data.xlabels");
            //MoreArray.printArray(motif_data.xlabels);
        }
        if (options.get("-out") != null) {
            outfile = (String) options.get("-out");
        }
        if (options.get("-bic") != null) {
            inBIC = (String) options.get("-bic");
            try {
                BIC = ValueBlockListMethods.readAny(inBIC, false);
                bicluster_set_size = BIC.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pp = new ParsePath(inBIC);
            if (BIC != null) {
                System.out.println("BIC.bicluster_set_size()" + BIC.size());
                /*for (int i = 0; i < BIC.bicluster_set_size(); i++) {
                    ValueBlock v = (ValueBlock) BIC.get(i);
                    BICmap.put(v.index, i);
                }*/
            }
        }
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 3) {
            PairTFs rm = new PairTFs(args);
        } else {
            System.out.println("syntax: java DataMining.func.PairTFs\n" +
                    "<-tfdata TF regulation matrix>\n" +
                    "<-bic BuildGraph output>\n" +
                    "<-annot BuildGraph output>\n" +
                    "<-out output file>"
            );
        }
    }
}


