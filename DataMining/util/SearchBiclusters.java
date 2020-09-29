package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 11/19/13
 * Time: 6:48 PM
 */
public class SearchBiclusters {

    String[] valid_args = {
            "-q", "-d", "-g", "-m", "-a", "-h", "-o", "-t", "-c"};

    String[] refids;
    HashMap<String, String> labels_map;
    String[] labels;
    HashMap options;

    ValueBlockList query, db;
    ValueBlockList outvbl = new ValueBlockList();

    String[] ids_query;

    String prefix;

    String mode = "any";

    String outprefix = "";

    String type = "g";

    int count_threshold = 0;


    /**
     * @param args
     */
    public SearchBiclusters(String[] args) {

        init(args);

        try {
            ArrayList out2 = new ArrayList();
            System.out.println("db " + db.size() + "\tquery " + query.size());
            int matches = 0;
            if (query != null) {
                matches = doQuery(matches);
            } else if (ids_query != null) {

                for (int i = 0; i < query.size(); i++) {
                    ValueBlock cur = (ValueBlock) query.get(i);
                    System.out.println(cur.toString());
                    int[] curids = null;
                    if (type.equalsIgnoreCase("g"))
                        curids = cur.genes;
                    else if (type.equalsIgnoreCase("e"))
                        curids = cur.exps;
                    System.out.println(MoreArray.toString(curids, ","));
                    for (int j = 0; j < db.size(); j++) {
                        ValueBlock curdb = (ValueBlock) db.get(j);
                        int[] dbids = null;
                        if (type.equalsIgnoreCase("g"))
                            dbids = curdb.genes;
                        else if (type.equalsIgnoreCase("e"))
                            dbids = curdb.exps;
                        double count = 0;
                        ArrayList ids = new ArrayList();
                        for (int k = 0; k < curids.length; k++) {
                            int index = MoreArray.getArrayInd(dbids, curids[k]);
                            //System.out.println(index);
                            if (index != -1) {
                                count++;
                                ids.add(refids[curids[k] - 1]);
                            }
                        }
                        if (mode.equals("exact") && type.equalsIgnoreCase("g") && count == cur.genes.length && count == curdb.genes.length) {
                            //System.out.println("FOUND EXACT count " + j + "\td " + (count / (double) dbgenes.length) + "\tq " +
                            //        (count / (double) curids.length) + "\t" + count + "\t" + labels[j + 1]);
                            //System.out.println(MoreArray.arrayListtoString(ids, ","));

                            String newstr = translateIds(j);
                            out2.add(labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ","));
                            outvbl.add(db.get(j));
                            matches++;
                        } else if (mode.equals("any") && type.equalsIgnoreCase("g") && count > 0) {
                            //System.out.println("FOUND ANY count " + j + "\td " + (count / (double) dbgenes.length) + "\tq " +
                            //        (count / (double) curids.length) + "\t" + count + "\t" + labels[j + 1]);
                            //System.out.println(MoreArray.arrayListtoString(ids, ","));
                            String newstr = translateIds(j);
                            out2.add(labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ","));
                            outvbl.add(db.get(j));
                            matches++;
                        } else if (mode.equals("exact") && type.equalsIgnoreCase("e") && count == cur.exps.length && count == curdb.exps.length) {
                            //System.out.println("FOUND EXACT count " + j + "\td " + (count / (double) dbgenes.length) + "\tq " +
                            //        (count / (double) curids.length) + "\t" + count + "\t" + labels[j + 1]);
                            //System.out.println(MoreArray.arrayListtoString(ids, ","));

                            String newstr = translateIds(j);
                            out2.add(labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ","));
                            outvbl.add(db.get(j));
                            matches++;
                        } else if (mode.equals("any") && type.equalsIgnoreCase("e") && count > 0) {
                            //System.out.println("FOUND ANY count " + j + "\td " + (count / (double) dbgenes.length) + "\tq " +
                            //        (count / (double) curids.length) + "\t" + count + "\t" + labels[j + 1]);
                            //System.out.println(MoreArray.arrayListtoString(ids, ","));
                            String newstr = translateIds(j);
                            out2.add(labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ","));
                            outvbl.add(db.get(j));
                            matches++;
                        }
                    }
                }
            }

            String s = outvbl.toString("#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            System.out.println("outpath " + (outprefix + ".vbl"));
            util.TextFile.write(s, outprefix + ".vbl");

            TextFile.write(out2, outprefix + "_geneids.txt");
            //System.out.println("matches " + matches);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param j
     * @return
     */
    private String translateIds(int j) {
        String[] list = labels[j + 1].split("_");
        String newstr = "";
        for (int l = 0; l < list.length; l++) {
            if (l > 0)
                newstr += "_" + labels_map.get(list[l]);
            else
                newstr += labels_map.get(list[l]);
        }
        return newstr;
    }

    /**
     * @param matches
     * @return
     * @throws Exception
     */
    private int doQuery(int matches) throws Exception {
        ArrayList out = new ArrayList();

        ArrayList matchdata = new ArrayList();

        HashMap associated_count = new HashMap();

        for (int i = 0; i < query.size(); i++) {
            ValueBlock cur = (ValueBlock) query.get(i);
            int[] curids = null;
            if (type.equalsIgnoreCase("g"))
                curids = cur.genes;
            else if (type.equalsIgnoreCase("e"))
                curids = cur.exps;

            System.out.println(MoreArray.toString(curids, ","));
            for (int j = 0; j < db.size(); j++) {
                ValueBlock curdb = (ValueBlock) db.get(j);
                int[] dbgenes = curdb.genes;
                double count = 0;
                ArrayList ids = new ArrayList();
                for (int k = 0; k < curids.length; k++) {
                    int index = MoreArray.getArrayInd(dbgenes, curids[k]);
                    //System.out.println(index);
                    if (index != -1) {
                        count++;
                        ids.add(refids[curids[k] - 1]);
                        System.out.println("added hit to " + j);
                    }
                }
                if (mode.equals("exact") && count == cur.genes.length && count == curdb.genes.length) {
                    //System.out.println("FOUND EXACT count " + j + "\td " + (count / (double) dbgenes.length) + "\tq " +
                    //        (count / (double) curids.length) + "\t" + count + "\t" + labels[j + 1]);
                    //System.out.println(MoreArray.arrayListtoString(ids, ","));

                    String newstr = translateIds(j);
                    System.out.println(labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ","));
                    out.add("" + labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ","));
                    outvbl.add(db.get(j));

                    matches++;
                } else if (mode.equals("any") && count > 0) {
                    //System.out.println("FOUND ANY count " + j + "\td " + (count / (double) dbgenes.length) + "\tq " +
                    //        (count / (double) curids.length) + "\t" + count + "\t" + labels[j + 1]);
                    //System.out.println(MoreArray.arrayListtoString(ids, ","));
                    String newstr = translateIds(j);
                    System.out.println("match\t" + labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ",") + "\t" + count);

                    matchdata.add(labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ",") + "\t" + count);

                    out.add("" + labels[j + 1] + "\t" + newstr + "\t" + MoreArray.arrayListtoString(ids, ","));
                    outvbl.add(db.get(j));
                    matches++;

                    if (count > count_threshold) {
                        System.out.println("COUNT " + count + "\t" + j);
                        for (int g = 0; g < dbgenes.length; g++) {
                            String nowid = refids[dbgenes[g] - 1];
                            int index = MoreArray.getArrayInd(curids, dbgenes[g]);
                            //System.out.println(index);
                            if (index == -1) {
                                Object o = associated_count.get(nowid);
                                if (o == null) {
                                    associated_count.put(nowid, 1);
                                } else {
                                    int curcount = (Integer) o;
                                    associated_count.put(nowid, curcount + 1);
                                }
                            }
                        }
                    }
                }
            }
        }


        ArrayList associated = new ArrayList();
        Iterator it = associated_count.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String data = pair.getKey() + "\t" + pair.getValue();
            if ((Integer) pair.getValue() > 1)
                System.out.println(data);
            associated.add(data);
            //it.remove(); // avoids a ConcurrentModificationException
        }


        System.out.println("out " + outprefix + "_associated.txt");
        TextFile.write(associated, outprefix + "_associated.txt");

        System.out.println("out " + out.size());
        TextFile.write(out, outprefix + ".txt");
        //TextFile.write(out, "FA_genes_in_RNAseqDB_bicluster.txt");

        String s = outvbl.toString("#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        String out1 = outprefix + ".vbl";
        System.out.println("outpath " + out1);
        util.TextFile.write(s, out1);

        String out2 = outprefix + "_matchdata.txt";
        System.out.println("outpath " + out2);
        util.TextFile.write(matchdata, out2);

        return matches;
    }


    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-q") != null) {
            String qpath = null;
            try {
                qpath = (String) options.get("-q");
                query = ValueBlockListMethods.readAny(qpath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ParsePath pp = new ParsePath(qpath);
            prefix = pp.getName() + "_";
        } else if (options.get("-l") != null) {
            String lpath = null;
            try {
                lpath = (String) options.get("-l");
                ids_query = TextFile.readtoArray(lpath);
                //query = ValueBlockListMethods.readAny(qpath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ParsePath pp = new ParsePath(lpath);
            prefix = pp.getName() + "_";
        }
        if (options.get("-d") != null) {

            String dpath = null;
            try {
                dpath = (String) options.get("-d");
                db = ValueBlockListMethods.readAny(dpath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options.get("-g") != null) {
            String gpath = (String) options.get("-g");
            String[][] sarray = TabFile.readtoArray(gpath);
            System.out.println("refids g " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            String[] n = MoreArray.extractColumnStr(sarray, col);
            refids = MoreArray.replaceAll(n, "\"", "");
            System.out.println("refids gene " + refids.length + "\t" + refids[0]);
        }

        if (options.get("-a") != null) {
            String gpath = (String) options.get("-a");
            String[][] sarray = TabFile.readtoArray(gpath);
            System.out.println("labels " + sarray.length + "\t" + sarray[0].length);
            String[] n = MoreArray.extractColumnStr(sarray, 1);
            labels = MoreArray.replaceAll(n, "\"", "");
            for (int i = 0; i < labels.length; i++) {
                labels[i] = labels[i].toUpperCase();
            }
            System.out.println("labels " + labels.length + "\t" + labels[0]);
        }

        if (options.get("-h") != null) {
            String gpath = (String) options.get("-h");
            String[][] labels_tab = TabFile.readtoArray(gpath);

            labels_map = new HashMap();
            for (int i = 1; i < labels_tab.length; i++) {
                System.out.println(labels_tab[i][0].toUpperCase() + "\t" + labels_tab[i][1]);
                labels_map.put(labels_tab[i][0].toUpperCase(), labels_tab[i][1]);
            }
            System.out.println("labels " + labels_map.size());
        }

        if (options.get("-m") != null) {
            try {
                mode = (String) options.get("-m");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options.get("-o") != null) {
            try {
                outprefix = (String) options.get("-o");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options.get("-t") != null) {
            try {
                type = (String) options.get("-t");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options.get("-c") != null) {
            try {
                count_threshold = Integer.parseInt((String) options.get("-c"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mode.equals("any")) {
            count_threshold = 0;
            System.out.println("resetting count_threshold to 0 because mode == any");
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 10 && args.length <= 18) {
            SearchBiclusters rm = new SearchBiclusters(args);
        } else {
            System.out.println("syntax: java DataMining.util.SearchBiclusters\n" +
                    "<-q query file> OR \n" +
                    "<-l list of query ids>\n" +
                    "<-d db file>\n" +
                    "<-g index ids>\n" +
                    "<-m mode = exact, any>\n" +
                    "<-a labels>\n" +
                    "<-t type g or e>\n" +
                    "<-o outprefix>\n" +
                    "<-c hit count threshold>"
            );
        }
    }
}
