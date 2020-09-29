package DataMining.eval;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import util.MoreArray;
import util.TextFile;
import util.runShell;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: May 9, 2011
 * Time: 6:22:37 PM
 */
public class ExtractFABIATest {

    REXP Rexpr;
    public Rengine Rengine;
    String outdir;

    /**
     * @param args
     */
    public ExtractFABIATest(String[] args) {

        outdir = args[1];
        /* String[] R_args = {"--no-save"};
        System.out.println("ExtractFABIATest standard: starting Rengine");
        Rengine = new Rengine(R_args, false, new TextConsole());
        System.out.println("ExtractFABIATest standard: Rengine created, waiting for R");
        if (!Rengine.waitForR()) {
            System.out.println("ExtractFABIATest standard: Cannot load R");
            System.exit(1);
        } else {
            System.out.println("ExtractFABIATest standard: R started");
        }*/


        ArrayList header = new ArrayList();

        header.add("setwd(\"" + args[0] + "\")");

        header.add("list2ascii <- function(x,file=paste(deparse(substitute(x)),\".txt\",sep=\"\")) {\n" +
                "   tmp.wid = getOption(\"width\")  # save current width\n" +
                "   options(width=10000)          # increase output width\n" +
                "   sink(file)                    # redirect output to file\n" +
                "   print(x)                      # print the object\n" +
                "   sink()                        # cancel redirection\n" +
                "   options(width=tmp.wid)        # restore linewidth\n" +
                "   return(invisible(NULL))       # return (nothing) from function\n" +
                "\n" +
                "}");

        File in = new File(args[0]);
        String[] list = in.list();
        System.out.println("args[0] " + args[0]);
        int length = list.length;
        System.out.println("length " + length);
        ArrayList cur = new ArrayList(header);

        runShell rs = new runShell();

        for (int i = 0; i < length; i++) {

            cur.add("load(\"" + list[i] + "\")\n");

            cur.add("cn <- colnames(dat$X)\n");

            cur.add("for(i in 1:length(cn)) \n" +
                    "{ \n" +
                    "cn[i] <- paste(\"V\", cn[i],sep=\"\")\n" +
                    "} \n" +
                    "colnames(dat$X) <- cn\n");

            cur.add("write.table(dat$X, file=\"" + outdir + "/" + list[i] + ".txt" + "\",sep=\"\t\")\n");
            cur.add("expr_data <- dat$X \n");
            cur.add("save(expr_data, file=\"" + outdir + "/" + list[i] + "\")\n");


            cur.add("dims <- gsub(\"\\\"\", \"\", paste(length(unlist(dat$LC[1])),length(unlist(dat$ZC[1])),sep=\" \")) \n" +
                    "outlist <- list(dims) \n" +
                    "outlist <- c(outlist,dat$LC[1]) \n" +
                    "outlist <- c(outlist,dat$ZC[1]) \n" +
                    "for(i in 2:10) { \n" +
                    "dims <- gsub(\"\\\"\", \"\", paste(length(unlist(dat$LC[i])),length(unlist(dat$ZC[i])),sep=\" \")) \n" +
                    "outlist <- c(outlist, dims) \n" +
                    "outlist <- c(outlist, dat$LC[i]) \n" +
                    "outlist <- c(outlist, dat$ZC[i]) \n" +
                    "}\n");

            cur.add("list2ascii(outlist, file=\"" + outdir + "/" + list[i] + "_indices.txt" + "\")\n");
            String outpath = list[i] + ".R";
            TextFile.write(MoreArray.ArrayListtoString(cur), outpath);

            String str = "R --vanilla < " + outpath + " &> " + outpath + ".out";
            System.out.println(str);
            rs.execute(str);
        }


    }

    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 2) {
            ExtractFABIATest rm = new ExtractFABIATest(args);
        } else {
            System.out.println("syntax: java DataMining.eval.ExtractFABIATest\n" +
                    "<in dir>\n" +
                    "<out dir>"
            );
        }
    }
}
