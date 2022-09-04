package DataMining;

import util.MapArgOptions;
import util.MoreArray;
import util.TextFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rauf on 4/12/16.
 */
public class ParamforErrMissForN {
    HashMap options;

    String[] valid_args = {
            "-out_dir", "-results_dir", "-param_dir"
    };

    String param_dir;
    String results_dir;
    String out_dir;

    public ParamforErrMissForN(String[] args) {
        try {
            init(args);

            File[] result_files = new File(results_dir).listFiles();

            File[] param_files = new File(param_dir).listFiles();

            for (int i = 0; i < param_files.length; i++) {
                File curr_param_file = param_files[i];
                String param_file_name = curr_param_file.getName();
                String param_index = param_file_name.split("_RC_")[1].split("_")[0];
                boolean resultExists = false;
                for (int j = 0; j < result_files.length; j++) {
                    File curr_result_file = result_files[j];
                    String result_file_name = curr_result_file.getName();
                    if (result_file_name.endsWith("toplist.txt")) {
                        String result_index = result_file_name.split("_RC_")[1].split("_")[0];
                        if (result_index.compareToIgnoreCase(param_index) == 0) {
                            resultExists = true;
                        }
                    }
                }
                if (!resultExists) {
                    String cmd = "cp " + param_dir + param_file_name + " " + out_dir;
                    String cmdfile = "cp.sh";
                    runCmd(cmd, ".", cmdfile);
                }
            }

        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-out_dir") != null) {
            out_dir = (String) options.get("-out_dir");
            if (!out_dir.endsWith("/")) {
                out_dir += "/";
            }
        }
        if (options.get("-param_dir") != null) {
            param_dir = (String) options.get("-param_dir");
            if (!param_dir.endsWith("/")) {
                param_dir += "/";
            }
        }
        if (options.get("-results_dir") != null) {
            results_dir = (String) options.get("-results_dir");
            if (!results_dir.endsWith("/")) {
                results_dir += "/";
            }
        }

    }

    private void runCmd(String cmd, String scriptbox, String scriptname) {

        TextFile.write(cmd, scriptbox + scriptname);
        Process p = null;

        try {
            p = Runtime.getRuntime().exec("bash " + scriptbox + scriptname + " &> " + scriptbox + scriptname + ".out");
            p.waitFor();
        } catch (Exception ex) {
            if (p != null) p.destroy();
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {
        System.out.println(args);
        if (args.length >= 5) {
            ParamforErrMissForN rm = new ParamforErrMissForN(args);
        } else {
            System.out.println("syntax: java DataMining.ParamforErrMissForN\n" +
                    "<-out_dir>\n" +
                    "<-param_dir>\n" +
                    "<-results_dir>\n"
            );
        }
    }
}