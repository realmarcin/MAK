package DataMining;

import mathy.SimpleMatrix;
import util.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Get the gene labels for the last block in the trajectory.
 * <p/>
 * User: marcin
 * Date: Aug 25, 2010
 * Time: 9:47:54 PM
 */
public class LabelTrajGenes {

    String[][] allids;
    String[] positions = {"f"};
    String dir;
    CheckTraj ct;
    SimpleMatrix sm;

    String outbicat = "";

    boolean requireOverlap = false;

    /**
     *
     */
    public LabelTrajGenes(String[] args) {

        File test = new File(args[0]);
        allids = TabFile.readtoArray(args[1]);
        allids = MoreArray.replaceAll(allids, "\"", "");

        ct = new CheckTraj(0.5, true);

        if (args.length >= 3) {
            positions = StringUtil.parsetoArray(args[2], ",");
            //MoreArray.printArray(positions);
        }
        if (args.length == 4) {
            sm = new SimpleMatrix(args[3]);
        }

        if (test.isDirectory()) {
            dir = args[0];
            String[] list = test.list();

            for (int i = 0; i < list.length; i++) {
                if (list[i].indexOf("toplist.txt") != -1)
                    doOne(args[0] + "/" + list[i]);
            }

            TextFile.write(outbicat, "DataMining_outbicat.txt");
        } else {

            doOne(args[0]);
        }
    }

    /**
     * @param in
     */
    private void doOne(String in) {
        boolean test = ct.run(in, requireOverlap);
        if (test) {
            if (positions != null) {
                for (int i = 0; i < positions.length; i++) {
                    //System.out.println("doOne "+positions[i]+"\t"+positions.length);
                    int cur = -1;
                    try {
                        cur = Integer.parseInt(positions[i]);
                    } catch (NumberFormatException e) {

                        if (positions[i].equalsIgnoreCase("s")) {
                            cur = 0;
                        }
                        if (positions[i].equalsIgnoreCase("m")) {
                            //System.out.println("doOne "+in);
                            cur = ValueBlockListMethods.findLastBFRandom(ct.vbl);//int) ((ct.vbl.size() - 1.0) / 2.0);

                            /*ValueBlock vt = (ValueBlock) ct.vbl.get(cur);
                            System.out.println("mid move class " + vt.move_class);
                            if (vt.move_class.startsWith("r_")) {
                                cur--;
                            }*/
                        } else if (positions[i].equalsIgnoreCase("f")) {
                            cur = ct.vbl.size() - 1;
                            ValueBlock v = (ValueBlock) ct.vbl.get(cur);
                            outbicat += "" + v.genes.length + " " + v.exps.length + "\n";
                            outbicat += MoreArray.toString(v.genes, " ") + "\n";
                            outbicat += MoreArray.toString(v.exps, " ") + "\n";
                        }
                    }
                    //ValueBlock pass = (ValueBlock) ct.vbl.get(cur);


                    int[] posindex = ct.getPos(cur, sm.data);
                    int[] negindex = ct.getNeg(cur, sm.data);

                    String[] gidspos = getIds(allids, posindex);
                    System.out.println(posindex.length + "\t" + gidspos.length);
                    if (gidspos.length > 0) {
                        ParsePath pp = new ParsePath(in);
                        String out = pp.getName() + "_" + positions[i] + "_plus.ids";
                        System.out.println("writing " + out);
                        TextFile.write(gidspos, out);
                    }
                    String[] gidsneg = getIds(allids, negindex);
                    System.out.println(posindex.length + "\t" + gidspos.length);
                    if (gidsneg.length > 0) {
                        ParsePath pp = new ParsePath(in);
                        String out = pp.getName() + "_" + positions[i] + "_minus.ids";
                        System.out.println("writing " + out);
                        TextFile.write(gidsneg, out);
                    }
                }
            } else {
                ValueBlock last = (ValueBlock) ct.vbl.get(ct.vbl.size() - 1);
                getIds(allids, last);
            }
        } else {
            System.out.println("WARNING " + in);
        }
    }

    /**
     * @param ids
     * @param last
     */
    private String[] getIds(String[][] ids, ValueBlock last) {
        ArrayList get = new ArrayList();
        for (int g = 0; g < last.genes.length; g++) {
            String curg = "" + last.genes[g];
            for (int i = 0; i < ids.length; i++) {
                //System.out.println(ids[i][0]);
                //System.out.println(ids[i].length);
                if (ids[i][0].equals(curg)) {
                    get.add(ids[i][1]);
                    //System.out.println(StringUtil.replace(ids[i][1], "\"", ""));
                }
            }
        }
        return MoreArray.ArrayListtoString(get);
    }

    /**
     * @param ids
     * @param gind
     */
    private String[] getIds(String[][] ids, int[] gind) {
        ArrayList get = new ArrayList();
        for (int g = 0; g < gind.length; g++) {
            String curg = "" + gind[g];
            for (int i = 0; i < ids.length; i++) {
                //System.out.println(ids[i][0] + "\t" + curg);
                //System.out.println(ids[i].length);
                if (ids[i][0].equals(curg)) {
                    get.add(ids[i][1]);
                    //System.out.println(StringUtil.replace(ids[i][1], "\"", ""));
                }
            }
        }
        return MoreArray.ArrayListtoString(get);
    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 2 || args.length == 3 || args.length == 4) {
            LabelTrajGenes rm = new LabelTrajGenes(args);
        } else {
            System.out.println("syntax: java DataMining.LabelTrajGenes\n" +
                    "<toplist file or dir>\n" +
                    "<gene ids file>\n" +
                    "<OPTIONAL list of trajectory positions>\n" +
                    "<OPTIONAL expression data file>\n"
            );
        }
    }
}
