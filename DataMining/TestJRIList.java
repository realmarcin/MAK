package DataMining;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/5/16
 * Time: 11:38 PM
 */
public class TestJRIList {


    public REXP Rexpr;
    public org.rosuda.JRI.Rengine Rengine;

    /**
     * @param args
     */
    public TestJRIList(String[] args) {

        String[] R_args = {"--no-save"};
        System.out.println("RunMinerBack standard: starting Rengine");
        Rengine = new org.rosuda.JRI.Rengine(R_args, false, new TextConsole());
        Rengine.DEBUG = 0;
        System.out.println("RunMinerBack standard: Rengine created, waiting for R");
        if (!Rengine.waitForR()) {
            System.out.println("RunMinerBack standard: Cannot load R");
            System.exit(1);
        } else {
            System.out.println("RunMinerBack standard: R started");
        }

        String s0 = "rm(list=ls())";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = Rengine.eval(s0));


        String s1 = "list(c(0.1, 0.2),c(0.3,0.4))";
        System.out.println("R: " + s1);
        System.out.println(Rexpr = Rengine.eval(s1));

        RList rl = Rexpr.asList();
        System.out.println(rl.at(0));

        REXP thisrexp = rl.at(0);
        thisrexp.asList();

        System.exit(0);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            TestJRIList rm = new TestJRIList(args);
        } else {
            System.out.println("syntax: java DataMining.TestJRIList");
        }
    }
}
