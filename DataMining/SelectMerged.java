package DataMining;

import util.TextFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Dec 20, 2011
 * Time: 3:52:53 PM
 */
public class SelectMerged {

    String header = "#\n";
    double cutoff = 0.99;

    /**
     * @param args
     */
    public SelectMerged(String[] args) {
        ValueBlockList traj = null, merged = null;
        try {
            traj = ValueBlockList.read(args[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            merged = ValueBlockList.read(args[1], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String outfile = args[2];

        if (args.length == 4)
            cutoff = Double.parseDouble(args[3]);

        System.out.println("0 traj " + traj.size() + "\tmerged " + merged.size());
        //remove all blocks below cutoff
        traj = ValueBlockListMethods.applyThreshold(traj, cutoff);
        merged = ValueBlockListMethods.applyThreshold(merged, cutoff);

        if (merged.size() > 0 && traj.size() > 0) {
            System.out.println("0 traj a/f threshold " + traj.size() + "\tmerged " + merged.size());

            ValueBlock vtmp = (ValueBlock) merged.get(0);
            System.out.println("bf sort " + vtmp.toStringShort());

            System.out.println("0.5 traj " + traj.size() + "\tmerged " + merged.size());

            //order by block area descending, KEY FOR NEXT STEPS
            traj = ValueBlockListMethods.sort("area", traj);
            merged = ValueBlockListMethods.sort("area", merged);

            System.out.println("1 traj " + traj.size() + "\tmerged " + merged.size());
            //originally this list is sorted by full criterion better to sort by size
            //first select containing from merged list
            for (int i = 0; i < merged.size(); i++) {
                ValueBlock m1 = (ValueBlock) merged.get(i);
                System.out.println("af sort area " + i + "\t" + m1.block_area);
                if (i == 0) {
                    System.out.println("af sort " + m1.toStringShort());
                }
                for (int j = i + 1; j < merged.size(); j++) {
                    //if (j != i) {
                    ValueBlock m2 = (ValueBlock) merged.get(j);
                    if (m1.contains(m2)) {
                        merged.remove(j);
                        j--;
                    }
                    //}
                }
            }

            System.out.println("2 traj " + traj.size() + "\tmerged " + merged.size());
            //select endpoints containing endpoints
            for (int i = 0; i < traj.size(); i++) {
                ValueBlock m = (ValueBlock) traj.get(i);
                for (int j = i + 1; j < traj.size(); j++) {
                    //if (j != i) {
                    ValueBlock t = (ValueBlock) traj.get(j);
                    if (m.contains(t)) {
                        traj.remove(j);
                        j--;
                    }
                    //}
                }
            }

            System.out.println("3 traj " + traj.size() + "\tmerged " + merged.size());
            //finally select endpoints not contained in merged list
            for (int i = 0; i < merged.size(); i++) {
                ValueBlock m = (ValueBlock) merged.get(i);
                for (int j = 0; j < traj.size(); j++) {
                    ValueBlock t = (ValueBlock) traj.get(j);
                    if (m.contains(t)) {
                        traj.remove(j);
                        j--;
                    }
                }
            }
            System.out.println("3.5 traj " + traj.size() + "\tmerged " + merged.size());
            traj.addElements(merged);
            System.out.println("4 traj " + traj.size() + "\tmerged " + merged.size());
            //ensure that all contained blocks removed
            /*for (int i = 0; i < traj.size(); i++) {
       ValueBlock m = (ValueBlock) traj.get(i);
       for (int j = i + 1; j < traj.size(); j++) {
           ValueBlock t = (ValueBlock) traj.get(j);
           if (m.contains(t)) {
               traj.remove(j);
               j--;
           }
       }
   }
   System.out.println("5 traj " + traj.size() + "\tmerged " + merged.size());*/

            System.out.println("writing " + outfile);
            String s = header + MINER_STATIC.HEADER_VBL;//ValueBlock.toStringShortColumns();
            String data = traj.toString(s);
            TextFile.write(data, outfile);
            //topNlist.writeBIC(outbic);
            System.out.println("wrote " + outfile);
        } else {
            if (merged.size() == 0)
                System.out.println("Merged block set empty after applying threshold " + cutoff);
            if (traj.size() == 0)
                System.out.println("Trajectory block set empty after applying threshold " + cutoff);

        }
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 3 || args.length == 4) {
            SelectMerged rm = new SelectMerged(args);
        } else {
            System.out.println("syntax: java DataMining.SelectMerged\n" +
                    "<trajectory output>\n" +
                    "<scored, merged list>\n" +
                    "<outfile>\n" +
                    "<OPTIONAL threshold, default = 0.99>"
            );
        }
    }
}
