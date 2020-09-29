package DataMining.util;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: 5/19/18
 * Time: 6:10 PM
 */
public class MapTermsToIds {

    /**
     * @param args
     */
    public MapTermsToIds(String[] args) {
        String[][] in_data = TabFile.readtoArray(args[0]);
        String[][] map_data = TabFile.readtoArray(args[1]);

        String[] map_data_terms = MoreArray.extractColumnStr(map_data, 2);
        ArrayList out = new ArrayList();
        out.add(in_data[0][0] + "\t" + in_data[0][1]);
        for (int i = 1; i < in_data.length; i++) {
            String cur = in_data[i][1];

            int index = StringUtil.getFirstEqualsIndex(map_data_terms, cur);
            System.out.println(cur + "\t" + index);
            if (index != -1) {
                out.add(in_data[i][0] + "\t" + map_data[index][0]);
            } else {
                System.out.println("did not map");
                out.add(in_data[i][0] + "\t" + in_data[i][1]);
            }
        }

        String outpath = args[0] + "_mapped.txt";
        System.out.println("writing " + outpath);
        TextFile.write(out, outpath);
    }


    /**
     * @param args
     */

    public final static void main(String[] args) {

        if (args.length == 2) {
            MapTermsToIds rm = new MapTermsToIds(args);
        } else {
            System.out.println("syntax: java DataMining.util.MapTermsToIds\n" +
                    "<input for source id-term mapping>\n" +
                    "<term-id mapping>"
            );
        }
    }
}
