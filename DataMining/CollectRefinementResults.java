package DataMining;

import util.MapArgOptions;
import util.MoreArray;
import util.TextFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rauf on 3/26/16.
 */
public class CollectRefinementResults {
    HashMap options;

    String[] valid_args = {
            "-indir", "-outfile", "-name_prefix", "-name_suffix"
    };

    String[] keys = new String[20];
    String indir;
    String outfile;
    String name_prefix;
    String name_suffix;

    String outstring = "";

    public CollectRefinementResults(String[] args) {
        try {
            init(args);

            int index = 0;
            boolean fileExists = true;

            //while loop starting at index 0 and going up until no such file exists
            while (fileExists) {
                String curr_file = indir + "/" + name_prefix + index + name_suffix;
                System.out.println(curr_file);
                File cf = new File(curr_file);
                if (cf.exists()) {
                    String finalTraj = getLastTrajectory(curr_file);
                    String[] finalTrajSplit = finalTraj.split("\\t");
                    System.out.println(finalTraj);
                    ArrayList ft = new ArrayList();
                    for (int i = 0; i < finalTrajSplit.length; i++) {
                        if (i == 0) {
                            String strindex = "" + index;
                            ft.add(strindex);
                        } else {
                            ft.add(finalTrajSplit[i]);
                        }
                    }
                    String mod_traj = joinStrings(ft);
                    outstring += mod_traj + '\n';
                } else {
                    fileExists = false;
                }
                index++;
            }
            TextFile.write(outstring, outfile);
        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    String getLastTrajectory(String filesPath) throws IOException {
        FileReader file_to_read = new FileReader(filesPath);
        BufferedReader bf = new BufferedReader(file_to_read);

        String lastTraj = "";

        String lastLine = "";
        String secondLastLine = "";

        String aLine;
        while ((aLine = bf.readLine()) != null) {
            if (aLine.startsWith("runLoop ************End movement loop iteration") || aLine.startsWith("runLoop Total time ")) {
                lastTraj = secondLastLine;
            }
            secondLastLine = lastLine;
            lastLine = aLine;
        }
        bf.close();
        return lastTraj;
    }

    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-outfile") != null) {
            outfile = (String) options.get("-outfile");
        }
        if (options.get("-name_prefix") != null) {
            name_prefix = (String) options.get("-name_prefix");
        }
        if (options.get("-name_suffix") != null) {
            name_suffix = (String) options.get("-name_suffix");
        }
        if (options.get("-indir") != null) {
            indir = (String) options.get("-indir");
            if (!indir.endsWith("/")) {
                indir += "/";
            }
        }
    }

    private String joinStrings(ArrayList<String> arrayList) {
        String result = "";
        int count = 0;
        for (String str : arrayList) {
            if (str.trim() != "" && str != null) {
                if (count == 0) {
                    result += str;
                } else {
                    result += "\t" + str;
                }
                count++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(args);
        if (args.length >= 3) {
            CollectRefinementResults rm = new CollectRefinementResults(args);
        } else {
            System.out.println("syntax: java DataMining.func.CollectRefinementResults\n" +
                    "<-indir>\n" +
                    "<-name_prefix>\n" +
                    "<-name_suffix>\n" +
                    "<-outfile>\n"
            );
        }
    }
}