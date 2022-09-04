package DataMining.func;

import util.MoreArray;
import util.TabFile;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 3/10/17
 * Time: 5:02 PM
 */
public class CompareSummary {

    boolean debug = false;

    HashMap<String, Integer> split_matches = new HashMap<String, Integer>();
    HashMap<String, Integer> split_nomatches = new HashMap<String, Integer>();

    /**
     * @param args
     */
    public CompareSummary(String[] args) {


        File dir = new File(args[0]);

        String[] list = dir.list();

        System.out.println("file list");
        MoreArray.printArray(list);

        for (int i = 0; i < list.length; i++) {

            String[][] dataA = TabFile.readtoArray(args[0] + "/" + list[i]);

            String[] TIGRA = MoreArray.extractColumnStr(dataA, 11);
            String[] GOA = MoreArray.extractColumnStr(dataA, 15);
            String[] PATHA = MoreArray.extractColumnStr(dataA, 17);
            String[] TFA = MoreArray.extractColumnStr(dataA, 19);

            for (int j = 0; j < list.length; j++) {

                if (i != j) {

                    System.out.println("doing " + i + "\t" + j);
                    String[][] dataB = TabFile.readtoArray(args[0] + "/" + list[j]);

                    //11,15,17,19

                    String[] TIGRB = MoreArray.extractColumnStr(dataB, 11);
                    String[] GOB = MoreArray.extractColumnStr(dataB, 15);
                    String[] PATHB = MoreArray.extractColumnStr(dataB, 17);
                    String[] TFB = MoreArray.extractColumnStr(dataB, 19);


                    //MoreArray.printArray(TIGRB);
                    //System.exit(0);

                    doOne(i, TIGRA, j, TIGRB, "TIGRrole: ", "TIGRrole");
                    doOne(i, GOA, j, GOB, "GO: ", "GO");
                    doOne(i, PATHA, j, PATHB, "Path: ", "Path");
                    doOne(i, TFA, j, TFB, "TF: ", "TF");

                    //System.exit(0);

              /*          System.out.println("start GO");
                        for (int goc = 1; goc < GOA.length; goc++) {
                            if (!GOA[goc].equals("none")) {

                                String replaced = GOA[goc].replace("GO: ", "");
                                String[] split = replaced.split("_");
                                if (debug)
                                    System.out.println("GO " + goc + "\t" + replaced);

                                for (int z = 0; z < split.length; z++) {
                                    int match = MoreArray.getArrayInd(GOB, split[z]);

                                    String key = "" + i + "-" + j + "_GO:" + split[z];
                                    if (match > -1) {

                                        int get = 0;
                                        try {
                                            get = (Integer) split_matches.get(key);
                                        } catch (Exception e) {
                                            if (debug)
                                                System.out.println("GO found match " + i + "\t" + j + "\t" + split[z]);
                                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                        split_matches.put(key, get + 1);
                                    } else {

                                        int get = 0;
                                        try {
                                            get = (Integer) split_nomatches.get(key);
                                        } catch (Exception e) {
                                            //System.out.println("GO no match " + i + "\t" + j + "\t" + split[z]);
                                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                        //System.out.println("GO no match " + i + "\t" + j + "\t" + split[z] + "\t" + get);
                                        split_nomatches.put(key, get + 1);
                                    }
                                }
                            }
                        }


                        System.out.println("start PATH");
                        for (int pac = 1; pac < PATHA.length; pac++) {
                            if (!PATHA[pac].equals("none")) {

                                String replaced = PATHA[pac].replace("Path: ", "");

                                String[] split = replaced.split("_");
                                if (debug)
                                    System.out.println("PATH " + pac + "\t" + replaced);

                                for (int z = 0; z < split.length; z++) {
                                    //System.out.println("split "+z+"\t"+split[z]);
                                    int match = MoreArray.getArrayInd(PATHB, split[z]);
                                    String key = "" + i + "-" + j + "_Path:" + split[z];
                                    if (match > -1) {

                                        int get = 0;
                                        try {
                                            get = (Integer) split_matches.get(key);
                                        } catch (Exception e) {
                                            if (debug)
                                                System.out.println("PATH found match " + i + "\t" + j + "\t" + split[z]);
                                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                        split_matches.put(key, get + 1);
                                    } else {

                                        int get = 0;
                                        try {
                                            get = (Integer) split_nomatches.get(key);
                                        } catch (Exception e) {
                                            if (debug)
                                                System.out.println("PATH no match " + i + "\t" + j + "\t" + split[z]);
                                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                        split_nomatches.put(key, get + 1);
                                    }
                                }
                            }
                        }

                        System.out.println("start TF");
                        for (int tfc = 1; tfc < TFA.length; tfc++) {
                            if (!TFA[tfc].equals("none")) {
                                String replaced = TFA[tfc].replace("TF: ", "");
                                String[] split = replaced.split("_");
                                if (debug)
                                    System.out.println("TF " + tfc + "\t" + replaced);

                                if (debug)

                                    for (int z = 0; z < split.length; z++) {
                                        int match = MoreArray.getArrayInd(TFB, split[z]);
                                        String key = "" + i + "-" + j + "_TF:" + split[z];
                                        if (match > -1) {

                                            int get = 0;
                                            try {
                                                get = (Integer) split_matches.get(key);
                                            } catch (Exception e) {
                                                if (debug)
                                                    System.out.println("TF found match " + i + "\t" + j + "\t" + split[z]);
                                                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                            }
                                            split_matches.put(key, get + 1);
                                        } else {

                                            int get = 0;
                                            try {
                                                get = (Integer) split_nomatches.get(key);
                                            } catch (Exception e) {
                                                if (debug)
                                                    System.out.println("TF no match " + i + "\t" + j + "\t" + split[z]);
                                                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                            }
                                            split_nomatches.put(key, get + 1);
                                        }
                                    }
                            }
                        }*/
                }
            }
        }

        System.out.println("split_matches.size()");
        System.out.println(split_matches.size());
        System.out.println(split_matches.keySet().size());
        System.out.println("split_nomatches.size()");
        System.out.println(split_nomatches.size());
        System.out.println(split_nomatches.keySet().size());


        Set keysmatch = split_matches.keySet();
        Set keysnomatch = split_nomatches.keySet();


        Iterator<String> iterator = keysmatch.iterator();
        HashMap ylabels = new HashMap();
        HashMap xlabels = new HashMap();
        while (iterator.hasNext()) {
            String key = iterator.next();
            //System.out.println(key + "\t" + split_matches.get(key));
            String[] split = key.split("_");
            ylabels.put(split[1], 1);
            xlabels.put(split[0], 1);
        }

        String[] ylabels_ar = (String[]) (ylabels.keySet()).toArray(new String[ylabels.size()]);
        String[] xlabels_ar = (String[]) (xlabels.keySet()).toArray(new String[xlabels.size()]);

        for (int i = 0; i < xlabels_ar.length; i++) {
            xlabels_ar[i] = "\"comp" + xlabels_ar[i] + "\"";
        }

        //MoreArray.printArray(ylabels_ar);

        String[][] match_overlap = new String[ylabels_ar.length][xlabels_ar.length];

        iterator = keysmatch.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            //System.out.println(key + "\t" + split_matches.get(key));
            String[] split = key.split("_");

            //String[] ind = split[0].split("-");
            //int i = Integer.parseInt(ind[0]);
            //int j = Integer.parseInt(ind[1]);

            int yindex = MoreArray.getArrayInd(ylabels_ar, split[1]);
            int xindex = MoreArray.getArrayInd(xlabels_ar, "\"comp" + split[0] + "\"");
            match_overlap[yindex][xindex] = "" + split_matches.get(key);
        }


        Iterator<String> iterator2 = keysnomatch.iterator();
        HashMap ylabels2 = new HashMap();
        HashMap xlabels2 = new HashMap();
        while (iterator2.hasNext()) {
            String key = iterator2.next();
            //System.out.println(key + "\t" + split_matches.get(key));
            String[] split = key.split("_");
            ylabels2.put(split[1], 1);
            xlabels2.put(split[0], 1);
        }

        String[] ylabels_ar2 = (String[]) (ylabels2.keySet()).toArray(new String[ylabels2.size()]);
        String[] xlabels_ar2 = (String[]) (xlabels2.keySet()).toArray(new String[xlabels2.size()]);

        for (int i = 0; i < xlabels_ar2.length; i++) {
            xlabels_ar2[i] = "\"comp" + xlabels_ar2[i] + "\"";
        }


        //MoreArray.printArray(ylabels_ar2);
        //MoreArray.printArray(xlabels_ar2);

        String[][] nomatch_overlap = new String[ylabels_ar2.length][xlabels_ar2.length];

        iterator2 = keysnomatch.iterator();
        while (iterator2.hasNext()) {
            String key = iterator2.next();
            //System.out.println(key + "\t" + split_matches.get(key));
            String[] split = key.split("_");

            //String[] ind = split[0].split("-");
            //int i = Integer.parseInt(ind[0]);
            //int j = Integer.parseInt(ind[1]);

            int yindex = MoreArray.getArrayInd(ylabels_ar2, split[1]);
            int xindex = MoreArray.getArrayInd(xlabels_ar2, "\"comp" + split[0] + "\"");
            nomatch_overlap[yindex][xindex] = "" + split_nomatches.get(key);
        }


        TabFile.write(match_overlap, "match_overlap_nonr.txt", xlabels_ar, ylabels_ar);
        TabFile.write(nomatch_overlap, "nomatch_overlap_nonr.txt", xlabels_ar2, ylabels_ar2);

        System.out.println("wrote match_overlap.txt nomatch_overlap.txt");
    }

    /**
     * @param i
     * @param DATA_A
     * @param j
     * @param DATA_B
     */
    private void doOne(int i, String[] DATA_A, int j, String[] DATA_B, String label, String shortlabel) {
        System.out.println("start " + shortlabel);
        /*TODO also compare unsplit enrichment strings */

        boolean added_match = false;
        boolean added_nomatch = false;
        for (int tic = 1; tic < DATA_A.length; tic++) {
            if (!DATA_A[tic].equals("none")) {
                //String label = "TIGRrole: ";
                String replaced = DATA_A[tic].replace(label, "");
                String[] split = replaced.split("_");
                if (debug)
                    System.out.println(label + " " + tic + "\t" + replaced);

                for (int z = 0; z < split.length; z++) {

                    boolean proceed = false;
                    int match0 = MoreArray.getArrayInd(DATA_B, label + split[z]);

                    if (match0 != -1) {
                        proceed = true;
                    } else if (match0 == -1) {
                        //end multi
                        int match1 = MoreArray.getArrayIndEndsWith(DATA_B, "_" + split[z]);
                        //mid multi
                        int match2 = MoreArray.getArrayIndexOf(DATA_B, "_" + split[z] + "_");
                        //start multi
                        int match3 = -1;
                        if (z == 0) {
                            match3 = MoreArray.getArrayIndStartsWith(DATA_B, label + split[z] + "_");
                        }

                        if (match1 != -1 || match2 != -1 || match3 != -1) {
                            proceed = true;
                        }
                    }

                    if (debug)
                        System.out.println(shortlabel + " test *" + split[z] + "*\t" + proceed + "\t" + DATA_B[0] + "\t" + DATA_B[1] + "\t" + DATA_B[2]);

                    String key = "" + i + "-" + j + "_" + shortlabel + ":" + split[z];

                    if (proceed) {

                        int get = 0;
                        try {
                            get = (Integer) split_matches.get(key);
                        } catch (Exception e) {
                            if (debug)
                                System.out.println(shortlabel + " found first match " + i + "\t" + j + "\t" + split[z]);
                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        if (debug)
                            System.out.println(label + " test " + key + "\t" + get);
                        split_matches.put(key, get + 1);
                    } else {

                        int get = 0;
                        try {
                            get = (Integer) split_nomatches.get(key);
                        } catch (Exception e) {
                            if (debug)
                                System.out.println(shortlabel + " first no match " + i + "\t" + j + "\t" + split[z]);
                            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        //if (key.endsWith(":cytoplasm"))
                        //    System.out.println("NO MATCH " + key);
                        split_nomatches.put(key, get + 1);
                    }
                }
            }
        }


        if (!added_match) {
            String fake_key = "" + i + "-" + j + "_" + shortlabel + ":none";
            split_matches.put(fake_key, 0);
        }
        if (!added_nomatch) {
            String fake_key = "" + i + "-" + j + "_" + shortlabel + ":none";
            split_nomatches.put(fake_key, 0);
        }

    }


    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 1) {
            CompareSummary rm = new CompareSummary(args);
        } else {
            System.out.println("syntax: java DataMining.func.CompareSummary\n" +
                    "<dir of summary files>\n"
            );
        }
    }
}
