package DataMining;

import mathy.stat;
import util.MoreArray;
import util.ParsePath;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: marcinjoachimiak
 * Date: Oct 23, 2007
 * Time: 10:29:57 AM
 */
public class TestMiner {

    int warnings;
    int fails;

    ArrayList trial_data_fails, trial_data_warnings;
    int tests = 1;

    String outfile;
    PrintWriter pw;

    /*Parameter sets in defined order.*/
    public final static String[] test_param_sets = {
            "parameters.addgene",
            "parameters.addexp",
            "parameters.deletegene",
            "parameters.deleteexp",
            "parameters.addgeneaddexp",
            "parameters.deletegenedeleteexp",
            "parameters.deletegenedeleteexpaddgeneaddexp"
    };

    /*True MSEall etc. values for test cases.*/
    //4,19,25,26,38,39,45/2,4,6,7,15,26,29
    public final static String true_block = "4,19,25,26,38,39,45/2,4,6,7,15,26,29";
    public final static ValueBlock true_vb = new ValueBlock(true_block);
    public final static double true_MSEall = 0.000075466;
    public final static double true_MSEall_pval = 0.9941758;
    public final static double true_Ival = 1;
    public final static double true_Ival_pval = 0.998804;
    public final static double[] true_final_values = {true_MSEall, true_MSEall_pval, true_Ival, true_Ival_pval};

    /*True more optimal blocks*/
    public final static String[] blockIds = {
            "4,19,26,38,39,45/2,4,6,7,15,26,29",
            "4,19,25,26,38,39,45/2,4,6,7,15,26",
            "4,15,19,25,26,38,39,45/2,4,6,7,15,26,29",
            "4,19,25,26,38,39,45/2,4,6,7,15,26,28,29",
            "4,19,26,38,39,45/2,4,6,7,15,26",
            "4,15,19,25,26,38,39,45/2,4,6,7,15,26,28,29",
            "4,15,19,26,38,39,45/2,4,6,7,15,26,28"
    };

    /*True MSE values*/
    public final static double[] true_MSEall_array = {
            0.00007867523,
            0.0000765628,
            0.26,
            0.3232453,
            0.00007939153,
            0.4714452,
            0.4637632
    };

    /*True MSEall value p-values for tests cases.*/
    public final static double[] true_MSEall_pval_array = {
            0.990713,
            0.991344,
            0.9848915,
            0.981312,
            0.9864833,
            0.9668438,
            0.951323
    };

    /*True interaction value for tests cases.*/
    public final static double[] true_Ival_array = {
            1,
            1,
            0.7656,
            1,
            1,
            0.765625,
            0.7346939
    };

    /*True interaction value p-values for tests cases.*/
    public final static double[] true_Ival_pval_array = {
            0.998804,
            0.998804,
            0.9353429,
            0.998804,
            1,
            0.9353429,
            0.9320287
    };

    /**
     * Object is created based on a list of arguments.
     *
     * @param args
     */
    public TestMiner(String[] args) {

        File dir = new File(args[0]);
        String[] param_list = dir.list();

        tests = Integer.parseInt(args[1]);

        if (args.length == 3)
            outfile = args[2];
        else {
            Random r = new Random();
            outfile = "TestMiner_" + r.nextInt() + ".out";
        }

        evaluate(args, param_list);
    }

    /**
     * @param args
     * @param param_list
     */
    private void evaluate(String[] args, String[] param_list) {
        RunMinerBack rmb = null;
        try {
            pw = new PrintWriter(new FileWriter(outfile));
            for (int j = 0; j < tests; j++) {
                warnings = 0;
                fails = 0;
                for (int i = 0; i < param_list.length; i++) {
                    ParsePath pp = new ParsePath(param_list[i]);
                    if (!pp.getExt().equals("toplist")) {
                        int test_param_index = MoreArray.getArrayInd(test_param_sets, param_list[i]);
                        if (test_param_index != -1) {
                            System.out.println("TestMiner Running " + param_list[i] + "\tindex " + test_param_index
                                    + "\t" + test_param_sets[test_param_index]);
                            double[] start = {true_MSEall_array[test_param_index], true_MSEall_pval_array[test_param_index],
                                    true_Ival_array[test_param_index], true_Ival_pval_array[test_param_index]};
                            if (rmb == null) {
                                rmb = new RunMinerBack(args[0] + "/" + param_list[i]);
                                evaluateTestCase(rmb, test_param_index, start);
                            } else {
                                rmb.updateParameters(args[0] + "/" + param_list[i]);
                                rmb.run();
                                evaluateTestCase(rmb, test_param_index, start);
                            }
                        }
                    }
                    if (tests > 1) {
                        if (trial_data_fails == null) {
                            trial_data_fails = new ArrayList();
                            trial_data_warnings = new ArrayList();
                        }
                        trial_data_fails.add(new Double(fails));
                        trial_data_warnings.add(new Double(warnings));
                    }
                }
            }

            if (tests > 1) {
                double mean_fails = stat.listAvg(trial_data_fails);
                double mean_warnings = stat.listAvg(trial_data_warnings);
                double sd_fails = stat.listSD(trial_data_fails, mean_fails);
                double sd_warnings = stat.listSD(trial_data_warnings, mean_warnings);
                String s = "SUMMARY: For " + tests + " tests there were " + mean_fails + "+/-" + sd_fails
                        + " FAILS and " + mean_warnings + "+/-" + sd_warnings + " WARNINGS.";
                System.out.println(s);
                pw.println();
                pw.println(s);
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rmb
     * @param test_param_index
     * @param start
     */
    private void evaluateTestCase(RunMinerBack rmb, int test_param_index, double[] start) {
        ArrayList ar = rmb.evaluateVSTrue(start, true_final_values, true_vb);
        double[] diffStart = (double[]) ar.get(0);
        double[] diffFinal = (double[]) ar.get(1);
        double[] maxpercsim = (double[]) ar.get(2);
        double[] identity = (double[]) ar.get(3);
        if (Math.abs(diffStart[1]) > 0.0001) {
            String s = "MSEp TEST FAILED start " + test_param_sets[test_param_index] +
                    "\nStart block MSE p-value is different by " +
                    diffStart[1] + " from true " + start[1];
            System.out.println(s);
            pw.println(s);
            fails++;
        } else {
            String s = "MSEp TEST PASSED start " + test_param_sets[test_param_index] +
                    "\nStart block MSE p-value is different by " +
                    diffStart[1] + " from true " + start[1];
            System.out.println(s);
            pw.println(s);
        }
        if (Math.abs(diffFinal[1]) > 0.0001) {
            String s = "MSEp TEST FAILED final " + test_param_sets[test_param_index] +
                    "\nMin block MSE p-value is different by " +
                    diffFinal[1] + " from true " + true_final_values[1];
            System.out.println(s);
            pw.println(s);
            fails++;
        } else {
            String s = "MSEp TEST PASSED final " + test_param_sets[test_param_index] +
                    "\nMin block MSE p-value is different by " +
                    diffFinal[1] + " from true " + true_final_values[1];
            System.out.println(s);
            pw.println(s);
        }
        if (maxpercsim[0] < 1.0) {
            String s = "maxpercsim TEST FAILED final " + test_param_sets[test_param_index] +
                    "\nMaximum percent similarity only " + maxpercsim[0];
            System.out.println(s);
            pw.println(s);
            fails++;
        } else {
            String s = "maxpercsim TEST PASSED final " + test_param_sets[test_param_index] +
                    "\nMaximum percent similarity " + maxpercsim[0];
            System.out.println(s);
            pw.println(s);
        }
        if (identity[0] == -1) {
            String s = "identity TEST WARNING final " + test_param_sets[test_param_index] +
                    " no identical block found";
            System.out.println(s);
            pw.println(s);
        } else {
            String s = "identity TEST PASSED final " + test_param_sets[test_param_index] +
                    " identical block " + identity[0];
            System.out.println(s);
            pw.println(s);
            warnings++;
        }
        String s = "*******************FAILS " + fails + "\tWARNINGS " + warnings;
        System.out.println(s);
        pw.println(s);
        pw.println();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            TestMiner rm = new TestMiner(args);
        } else {
            System.out.println("syntax: java DataMining.TestMiner\n" +
                    "<parameter dir>\n" +
                    "<number of independent tests to run>\n" +
                    "<(optional)outfile>"
            );
        }
    }

}
