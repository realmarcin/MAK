package DataMining.util;

import util.MoreArray;
import util.TextFile;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 12/17/17
 * Time: 11:44 AM
 */
public class MarkDownIndex {

    /**
     * @param args
     */
    public MarkDownIndex(String[] args) {

        File dir = new File(args[0]);
        String[] list = dir.list();

        String[] specific = null;
        int[] specific_ids = null;
        if (args.length == 3) {
            specific = TextFile.readtoArray(args[2]);
            specific_ids = new int[specific.length];
            for (int i = 0; i < specific.length; i++) {
                int start = specific[i].lastIndexOf("__");
                int stop = specific[i].indexOf("_expdata.txt");
                int x = Integer.parseInt(specific[i].substring(start + 2, stop));
                specific_ids[i] = x;
            }
        }

        int max_bic_ind = 0;
        int[] bic_index = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            if (list[i].indexOf(".DS_Store") == -1 && list[i].indexOf(".Rhistory") == -1 && list[i].indexOf(".RData") == -1) {//list[i] != ".DS_Store" && list[i] != ".Rhistory" &&
                //results_disease__by__HPO_matrix_v3_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_meangreat0.2.txt__945_expdata.txt_heat.png
                int[] start_stop_ind = get_start_stop(list[i]);

                System.out.println(list[i] + "\t" + start_stop_ind[0] + "\t" + start_stop_ind[1]);

                int cur_bic_index = Integer.parseInt(list[i].substring(start_stop_ind[0], start_stop_ind[1]));
                bic_index[i] = cur_bic_index;
                max_bic_ind = Math.max(max_bic_ind, cur_bic_index);
            }
        }


        //String[] out = makeIndexOrig(args, list, specific, specific_ids);
        String[] out = makeIndex(args, list, specific, specific_ids, max_bic_ind, bic_index);


        //String[] outstr = MoreArray.convtoStringArray(out);

        TextFile.write(out, args[1]);
        System.out.println("wrote " + out);
    }

    /**
     * @param args
     * @param list
     * @param specific
     * @param specific_ids
     * @param max_bic_ind
     * @param bic_index
     * @return
     */
    private String[] makeIndex(String[] args, String[] list, String[] specific, int[] specific_ids, int max_bic_ind, int[] bic_index) {
        //ArrayList out = MoreArray.initArrayListList(list.length);

        String[] out = new String[max_bic_ind + 1];
        int count = 0;

        for (int m = 0; m <= max_bic_ind; m++) {
            //for (int i = 0; i < list.length; i++) {
            //if (list[i] != ".DS_Store") {

            int[] hits = new int[0];
            try {
                hits = MoreArray.getArrayIndAll(bic_index, m);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            int noimage = 0;
            String cur = "bicluster " + m + " ";
            for (int h = 0; h < hits.length; h++) {
                int specific_index = -1;

                if (specific != null) {
                    try {
                        specific_index = MoreArray.getArrayInd(specific_ids, m);
                        //System.out.println("specific_index "+specific_index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (specific == null || (specific != null && specific_index != -1)) {
                    String type = "";
                    if (list[hits[h]].endsWith("heat.png") && !list[hits[h]].endsWith("cluster_heat.png")) {
                        type = "image";
                        cur += "[" + type + "](https://github.com/realmarcin/MAK_results/blob/master/" + args[0] + "/" + list[hits[h]] + ") ";
                        h = hits.length;
                        break;
                    } else {
                        noimage++;
                    }
                }
            }

            if (noimage == hits.length) {

                for (int h = 0; h < hits.length; h++) {
                    int specific_index = -1;

                    if (specific != null) {
                        try {
                            specific_index = MoreArray.getArrayInd(specific_ids, m);
                            //System.out.println("specific_index "+specific_index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (specific == null || (specific != null && specific_index != -1)) {
                        String type = "";
                        if (list[hits[h]].endsWith("cluster_heat.png")) {
                            type = "image";
                            cur += "[" + type + "](https://github.com/realmarcin/MAK_results/blob/master/" + args[0] + "/" + list[hits[h]] + ") ";
                            h = hits.length;
                            break;
                        } else {
                            noimage++;
                        }
                    }
                }

            }

            for (int h = 0; h < hits.length; h++) {
                int specific_index = -1;

                if (specific != null) {
                    try {
                        specific_index = MoreArray.getArrayInd(specific_ids, m);
                        //System.out.println("specific_index "+specific_index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (specific == null || (specific != null && specific_index != -1)) {
                    String type = "";
                    if (list[hits[h]].endsWith("expdata.txt")) {
                        type = "data";
                        cur += "[" + type + "](https://github.com/realmarcin/MAK_results/blob/master/" + args[0] + "/" + list[hits[h]] + ") ";
                        h = hits.length;
                        break;
                    }
                }
            }

            for (int h = 0; h < hits.length; h++) {
                int specific_index = -1;

                if (specific != null) {
                    try {
                        specific_index = MoreArray.getArrayInd(specific_ids, m);
                        //System.out.println("specific_index "+specific_index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (specific == null || (specific != null && specific_index != -1)) {
                    String type = "";
                    if (list[hits[h]].endsWith("colorder.txt")) {
                        type = "columns";
                        cur += "[" + type + "](https://github.com/realmarcin/MAK_results/blob/master/" + args[0] + "/" + list[hits[h]] + ") ";
                        h = hits.length;
                        break;
                    }
                }
            }

            for (int h = 0; h < hits.length; h++) {
                int specific_index = -1;

                if (specific != null) {
                    try {
                        specific_index = MoreArray.getArrayInd(specific_ids, m);
                        //System.out.println("specific_index "+specific_index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (specific == null || (specific != null && specific_index != -1)) {
                    String type = "";
                    if (list[hits[h]].endsWith("roworder.txt")) {
                        type = "rows";
                        cur += "[" + type + "](https://github.com/realmarcin/MAK_results/blob/master/" + args[0] + "/" + list[hits[h]] + ") ";
                        h = hits.length;
                        break;
                    }
                }
            }

            cur += "\n";
            out[count] = cur;//((5 * bic_index + (pos - 1)) + "\t" +
            //[I'm an inline-style link with title](https://www.google.com "Google's Homepage")
            count++;
        }
        return out;
    }

    /**
     * @param s
     * @return
     */
    private int[] get_start_stop(String s) {

        System.out.println("get_start_stop " + s);
        int[] start_stop_ind = {-1, -1};
        String start = ".txt__";
        String stop = "_expdata";
        start_stop_ind[0] = s.indexOf(start) + start.length();
        start_stop_ind[1] = s.indexOf(stop);

        if (start_stop_ind[1] == -1) {
            start = ".txt___";
            stop = ".pdf";
            start_stop_ind[0] = s.indexOf(start) + start.length();
            start_stop_ind[1] = s.indexOf(stop);

            if (start_stop_ind[1] == -1) {
                start = ".txt___";
                stop = "_heat.png";
                start_stop_ind[0] = s.indexOf(start) + start.length();
                start_stop_ind[1] = s.indexOf(stop);
            }
        }
        return start_stop_ind;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            MarkDownIndex rm = new MarkDownIndex(args);
        } else {
            System.out.println("syntax: java DataMining.util.MarkDownIndex\n" +
                    "<input bicluster file dir> <output index file> <OPTIONAL file list>"
            );
        }
    }
}
