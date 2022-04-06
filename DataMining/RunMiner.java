package DataMining;

import dtype.SystemResource;
import org.rosuda.JRI.REXP;
import util.MapArgOptions;
import util.MoreArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Overarching class to initR data mining algorithm with components in R and C.
 * <p/>
 * <p/>
 * User: marcin
 * Date: Jun 7, 2007
 * Time: 4:53:37 PM
 */
public class RunMiner extends util.Program {

    MapArgOptions map;
    HashMap options;
    String[] valid_args = {"-param", "-debug"};
    SystemResource sysRes;
    // {"-null", "-rdata", "-prev", "-param",
    // "-method", "-mse", "-msesd", "-int", "-PRECRITERIA", "-annot"};

    int debug = MINER_STATIC.DEFAULT_DEBUG;

    /**
     * @param prmfile
     * @param dbg
     */
    public RunMiner(String prmfile, int dbg) {
        super();

        run(prmfile, dbg);
    }

    /**
     * @param args
     */
    public RunMiner(String[] args) {
        super();

        //args = MapArgOptions.compactArgs(args);
        System.out.println("RunMiner args");
        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);
        final String s1 = (String) options.get("-debug");
        if (s1 != null)
            debug = Integer.parseInt(s1);
        System.out.println("RunMiner d " + debug + "\t" + s1);

        System.out.println("RunMiner debug " + debug);
        String s = (String) options.get("-param");

        run(s, debug);
    }

    /**
     * @param prmfile
     * @param dbg
     */
    private void run(String prmfile, int dbg) {
        System.out.println(MINER_STATIC.version);

        sysRes = initSystemResources();

        String s = prmfile;
        debug = dbg;

        File test = new File(s);
        if (!test.exists()) {
            System.out.println("RunMiner input parameter file does not exist:\n" + s + "\nexiting");
            System.exit(0);
        }
        if (debug != 0) {
            RunMinerBack rmb = new RunMinerBack(s, sysRes, debug);//options);

            REXP Rexpr = rmb.irv.Rengine.eval("tmp_dir <- tempdir()");
            System.out.println("removing Rtmp "+Rexpr.toString());
            Rexpr = rmb.irv.Rengine.eval("unlink(tmp_dir, recursive = T)");
            System.out.println("removing Rtmp "+Rexpr.toString());
            Rexpr = rmb.irv.Rengine.eval("dir.exists(tmp_dir)");
            System.out.println("removed Rtmp "+Rexpr.toString());
            /*tmp_dir <- tempdir()
            unlink(tmp_dir, recursive = T)
            dir.exists(tmp_dir)*/

            rmb.irv.Rengine.end();
            System.exit(0);
        } else {
            RunMinerBack rmb = new RunMinerBack(s, sysRes);//options);

            REXP Rexpr = rmb.irv.Rengine.eval("tmp_dir <- tempdir()");
            System.out.println("removing Rtmp "+Rexpr.toString());
            Rexpr = rmb.irv.Rengine.eval("unlink(tmp_dir, recursive = T)");
            System.out.println("removing Rtmp "+Rexpr.toString());
            //Rexpr = rmb.irv.Rengine.eval("dir.exists(tmp_dir)");
            //System.out.println("removed Rtmp "+Rexpr.toString());
                    
            rmb.irv.Rengine.end();
            System.exit(0);
        }
    }


    /**
     * @param vb
     * @param f
     */
    public RunMiner(ValueBlock vb, String f) {
        super();

        System.out.println(MINER_STATIC.version);

        sysRes = initSystemResources();

        File test = new File(f);
        if (!test.exists()) {
            System.out.println("RunMiner input parameter file does not exist:\n" + f + "\nexiting");
            System.exit(0);
        }


        Parameters prm = new Parameters();

        System.out.println("loading parameters " + f);
        prm.param_path = f;
        //orig_prm.debug = 0;
        try {
            prm.read(f);
        } catch (Exception e) {
            System.out.println("Failed to load parameter file, exiting.");
            e.printStackTrace();
        }

        String s = MoreArray.toString(vb.genes, ",") + "/" + MoreArray.toString(vb.exps, ",");
        prm.init_block_list = (ArrayList) Arrays.asList(s);

        /*TODO if exps not defined than start with all exps, if genes not defined start with all genes */

        //if (debug != 0) {
        RunMinerBack rmb = new RunMinerBack(prm, sysRes, debug);
        System.exit(0);
       /* } else {
            RunMinerBack rmb = new RunMinerBack(prm, sysRes);
            System.exit(0);
        }*/
    }

    /**
     * @param prm
     */
    public RunMiner(Parameters prm) {
        super();

        System.out.println(MINER_STATIC.version);

        sysRes = initSystemResources();

        if (prm == null) {
            System.out.println("RunMiner input parameter object is null");
            System.exit(0);
        }
        RunMinerBack rmb = new RunMinerBack(prm, sysRes, debug);
        System.exit(0);
    }


    /**
     * @param prm
     */
    public RunMiner(Parameters prm, int debug) {
        super();

        System.out.println(MINER_STATIC.version);

        sysRes = initSystemResources();

        if (prm == null) {
            System.out.println("RunMiner input parameter object is null");
            System.exit(0);
        }
        RunMinerBack rmb = new RunMinerBack(prm, sysRes, debug);
        System.exit(0);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args == null)
            System.out.println("RunMiner main args is null");

        if (args.length == 2 || args.length == 4) {
            //System.out.println("Code update successful");
            RunMiner rm = new RunMiner(args);
        } else {
            System.out.println("syntax: java DataMining.RunMiner\n" +
                    "<-param 'parameter file' >\n" +
                    "<-debug 0,1,2,3 levels (0 = no debug) >"
            );
        }
    }
}