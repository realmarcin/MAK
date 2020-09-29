package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 5/16/18
 * Time: 3:28 PM
 */
public class BiclustersforAPI {

    boolean debug = false;
    String header = "index\tscore\tcol_enrich\trow_enrich\tcol_types\tcol_ids\trow_ids";

    /**
     * @param args
     */
    public BiclustersforAPI(String[] args) {
        ValueBlockList vbl = null;
        try {
            vbl = ValueBlockList.read(args[0], debug);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] col_lab = TextFile.readtoArray(args[1]);
        System.out.println("col_lab " + col_lab.length);
        System.out.println("col_lab " + col_lab[0]);//[0]+"\t"+col_lab[1][1]);
        System.out.println("col_lab " + col_lab[1]);//[0]+"\t"+col_lab[1][1]);
        for (int i = 0; i < col_lab.length; i++) {
            col_lab[i] = col_lab[i].substring(col_lab[i].indexOf(" = ") + 3);
        }

        String[] row_lab = null;

        String[][] col_ids = TabFile.readtoArray(args[2]);
        String[][] row_ids = TabFile.readtoArray(args[3]);


        System.out.println("col_ids " + col_ids[0][0] + "\t" + col_ids[0][1]);
        System.out.println("row_ids " + row_ids[0][0] + "\t" + row_ids[0][1]);


        if (args.length > 4) {
            row_lab = TextFile.readtoArray(args[4]);
            System.out.println("row_lab " + row_lab.length);
            System.out.println("row_lab " + row_lab[0]);//[0]+"\t"+row_lab[1][1]);
            System.out.println("row_lab " + row_lab[1]);//[0]+"\t"+row_lab[1][1]);

            for (int i = 0; i < row_lab.length; i++) {
                int index = row_lab[i].indexOf(" = ") + 3;
                row_lab[i] = row_lab[i].substring(index);
            }
        }

        String[][] col_map = null;
        String[] col_map_ids = null;
        if (args.length == 6) {
            col_map = TabFile.readtoArray(args[5]);
            col_map_ids = MoreArray.extractColumnStr(col_map, 1);
            col_map_ids = StringUtil.replace(col_map_ids, "\"", "");
            System.out.println("col_map_ids " + col_map_ids[1]);
        }


        ArrayList out = new ArrayList();
        out.add(header);
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);

            System.out.println("col_lab[i+1] " + col_lab[i + 1]);
            String str = "" + v.index + "\t" + v.full_criterion + "\t" + col_lab[i + 1].substring(Math.min(6, col_lab[i + 1].length()));
            if (args.length > 4)
                str += "\t" + row_lab[i + 1];
            else
                str += "\tnone";

            if (col_map != null) {
                String col_map_str = "";
                HashMap tm = new HashMap();
                for (int a = 0; a < v.exps.length; a++) {
                    int index = StringUtil.getFirstEqualsIndex(col_map_ids, StringUtil.replace(col_ids[v.exps[a] - 1][1], "\"", ""));
                    System.out.println("index " + index + "\t" + col_map.length + "\t" + col_map[0].length + "\t" + col_ids[v.exps[a] - 1][1]);
                    String cand = col_map[index][1];
                    Object test = tm.put(cand, 1);
                    if (test == null) {
                        col_map_str += cand;
                        if (a < v.exps.length - 1)
                            col_map_str += ",";
                    }
                }
                str += "\t" + col_map_str;
            } else
                str += "\tnone";

            String col_id_str = "";
            for (int a = 0; a < v.exps.length; a++) {
                col_id_str += col_ids[v.exps[a] - 1][1];
                if (a < v.exps.length - 1)
                    col_id_str += ",";
            }

            String row_id_str = "";
            for (int a = 0; a < v.genes.length; a++) {
                row_id_str += row_ids[v.genes[a] - 1][1];
                if (a < v.genes.length - 1)
                    row_id_str += ",";
            }
            col_id_str = StringUtil.replace(col_id_str, "\"", "");
            row_id_str = StringUtil.replace(row_id_str, "\"", "");
            str += "\t" + col_id_str + "\t" + row_id_str;

            out.add(str);
        }

        String outpath = args[0].substring(0, args[0].length() - 4) + "_SmartAPI.txt";
        System.out.println("writing " + outpath);
        TextFile.write(out, outpath);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 4 || args.length == 5 || args.length == 6) {
            BiclustersforAPI rm = new BiclustersforAPI(args);
        } else {
            System.out.println("syntax: java DataMining.util.BiclustersforAPI\n" +
                    "<bicluster file>\n" +
                    "<column labeling file>\n" +
                    "<column ids file>\n" +
                    "<row ids file>\n" +
                    "<OPTIONAL row labeling file>\n" +
                    "<OPTIONAL column label map>"
            );
        }
    }

}
