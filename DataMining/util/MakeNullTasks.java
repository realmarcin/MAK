package DataMining.util;

import DataMining.MINER_STATIC;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 10/5/16
 * Time: 10:51 PM
 */
public class MakeNullTasks {

    /**
     * @param args
     */
    public MakeNullTasks(String[] args) {

        String datafile = args[0];
        String rfile = args[1];
        String prefix = args[2];
        String outpath = args[3];
        String abs = args[4];
        String outfile = args[5];

        ArrayList out = new ArrayList();

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < MINER_STATIC.CRIT_LABELS.length; j++) {

                final String curc = MINER_STATIC.CRIT_LABELS[j];

                //only single criteria
                if (curc.indexOf("_") == -1) {
                    if (curc.indexOf("Binary") == -1 && curc.indexOf("noninvert") == -1 && curc.indexOf("MEAN") == -1 && curc.indexOf("LARS") == -1 && curc.indexOf("feat") == -1 && curc.indexOf("inter") == -1 && curc.indexOf("MAXTF") == -1) {

                        out.add(("java -Xmx2G DataMining.MakeNull -source /global/home/users/marcin/common/Miner.R " +
                                //"-intxt /global/homes/m/marcinj/common/fake110427_4c_const_nono_expr_rand.txt " +
                                "-intxt " + datafile + " " +
                                ///"-inR /global/homes/m/marcinj/common/fake110427_4c_const_nono_0.25ppi_rand " +
                                "-inR  " + rfile + " " +
                                //"-out $SCRATCH/fake_noabs/fake_4c_const_nono_noabs_" + i + "_00 -gmin 2 " +
                                "-out " + outpath + "/" + prefix + "_" + i + "_00 -gmin 2 " +
                                //"-gmax 50 -emin 2 -emax 50 -nsamp 100 -seed " + i + " -debug n -abs 0,0,0 " +
                                "-gmax 50 -emin 2 -emax 50 -nsamp 100 -seed " + i + " -debug n -abs " + abs + " " +
                                "-frxnsign F -crits " + curc + " " +
                                //"&> $SCRATCH/out/fake_4c_const_nono_noabs_" + i + "_00.out"));
                                "&> " + outpath + "_out/" + prefix + "_" + curc + "_" + i + "_00.out"));
                    }
                }
            }

        }

        TextFile.write(out, outfile);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 6) {
            MakeNullTasks mn = new MakeNullTasks(args);
        } else {
            System.out.println("syntax: java DataMining.util.MakeNullTasks\n" +
                    "<in data> " +
                    "<in Rdata> " +
                    "<prefix> " +
                    "<outpath> " +
                    "<abs> " +
                    "<outfile>\n"
            );
        }
    }
}
