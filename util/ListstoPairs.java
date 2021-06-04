package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ListstoPairs {

    HashMap<String, String> mapping;

    /**
     * @param args
     */
    public ListstoPairs(String[] args) {

        if (args.length == 2) {
            System.out.println("WARNING: Assumming header in map!");
            String[][] map_data = TabFile.readtoArray(args[1]);

            mapping = new HashMap();
            for (int i = 1; i < map_data.length; i++) {
                System.out.println("adding *" + map_data[i][0] + "*\t*" + map_data[i][1] + "*");
                mapping.put((map_data[i][0]).toLowerCase(), map_data[i][1]);
            }
        }
        System.out.println("reading");
        String[][] data = TabFile.readtoArray(args[0]);
        System.out.println("WARNING: Assumming header!");
        ArrayList<String> outar = new ArrayList<String>();

        outar.add("subject\tobject");
        for (int i = 1; i < data.length; i++) {
            String cursubj = data[i][0];
            data[i][1] = StringUtil.replace(data[i][1], "\"", "");
            data[i][1] = data[i][1].toLowerCase(Locale.ROOT);
            String[] split = data[i][1].split(",");
            for (int j = 0; j < split.length; j++) {
                String curobj = split[j].trim();
                if (curobj.length() > 0) {
                    if (mapping != null) {
                        String result = mapping.get(curobj);
                        if (result == null) {
                            if (split[j].endsWith("-associated")) {
                                curobj = StringUtil.replace(curobj, "-associated", "");
                                result = mapping.get(curobj);
                            }
                            if (result == null)
                                System.out.println("failed *" + split[j] + "* as " + curobj);
                            else
                                curobj = result;
                        } else
                            curobj = result;
                    }
                    String newtriple = cursubj + "\t" + curobj;
                    outar.add(newtriple);
                } else {
                    String newtriple = cursubj + "\tNone";
                    outar.add(newtriple);
                }
            }
        }

        String outf = args[0].substring(0, args[0].lastIndexOf('.')) + "_pairs.txt";
        if (mapping != null)
            outf = args[0].substring(0, args[0].lastIndexOf('.')) + "_pairsids.txt";
        System.out.println("writing " + outf);
        TextFile.write(outar, outf);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 || args.length == 2) {
            util.ListstoPairs arg = new util.ListstoPairs(args);
        } else {
            System.out.println("syntax: java DataMining.util.ListstoPairs <input file with two columns -- subject and list of objects> <OPTIONAL mapping file to identifiers>\n");
        }
    }
}
