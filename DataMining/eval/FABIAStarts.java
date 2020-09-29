package DataMining.eval;

import util.TextFile;
import util.runShell;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jun 7, 2011
 * Time: 10:40:25 PM
 */
public class FABIAStarts {

    public FABIAStarts() {

        runShell rs = new runShell();
        for (int i = 1; i < 101; i++) {
            String script = "";
            script += "Imax=100\n";
            script += "Imin=5\n";
            script += "Jmax=100\n";
            script += "Jmin=5\n";

            //script += "library(amap)\n";
            script += "library(fastcluster)\n";
            script += "library(Hmisc)\n";

            script += "setwd(\"~/integr8functgenom/data/FABIA/ready/mult/\")\n";
            script += "source(\"/usr2/people/marcin/Genomics/html/applet/MicroArrayUp/MicroArrayUp/DataMining_R/Miner.R\")\n";


            script += "load(paste(\"exp_\"," + i + ",\".RData\",sep=\"\"))\n";

            script += "nbs=allpossibleInitial(expr_data,Imin,Imax,Jmin,Jmax,\"correlation\")\n";
            script += "write.table(nbs, file=paste(\"exp_\"," + i + ",\"_STARTS.txt\",sep=\"\"), sep=\"\t\")\n";

            String outfile = "exp_" + i + "_STARTS.R";
            TextFile.write(script, outfile);

            String e = "R --vanilla < " + outfile + " &> " + outfile + ".out";
            rs.execute(e);
        }

    }

    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 0) {
            FABIAStarts rm = new FABIAStarts();
        } else {
            System.out.println("syntax: java DataMining.eval.FABIAStarts"
            );
        }
    }

}
