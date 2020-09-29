package DataMining.func;

import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Apr 16, 2012
 * Time: 12:27:14 PM
 */
public class CompareValid {


    /**
     * @param args
     */
    public CompareValid(String[] args) {
        String[][] MAKs = TabFile.readtoArray(args[0]);
        String[][] ISAs = TabFile.readtoArray(args[1]);
        String[][] cMons = new String[0][];
        try {
            cMons = TabFile.readtoArray(args[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String prefix = args[3];

        HashMap MAKshash = new HashMap();
        for (int i = 0; i < MAKs.length; i++) {
            String key = MAKs[i][2] + "-" + MAKs[i][5] + "-" + MAKs[i][11];
            key = StringUtil.replace(key, "\"", "");
            Object o = MAKshash.get(key);
            if (o == null) {
                MAKshash.put(key, 1);
            } else {
                int v = (Integer) o;
                MAKshash.put(key, v + 1);
            }
        }

        HashMap ISAshash = new HashMap();
        for (int i = 0; i < ISAs.length; i++) {
            String key = ISAs[i][2] + "-" + ISAs[i][5] + "-" + ISAs[i][11];
            key = StringUtil.replace(key, "\"", "");
            Object o = ISAshash.get(key);
            if (o == null) {
                ISAshash.put(key, 1);
            } else {
                int v = (Integer) o;
                ISAshash.put(key, v + 1);
            }
        }

        HashMap cMonshash = null;
        if (cMons != null) {
            cMonshash = new HashMap();
            for (int i = 0; i < cMons.length; i++) {
                String key = cMons[i][2] + "-" + cMons[i][5] + "-" + cMons[i][11];
                key = StringUtil.replace(key, "\"", "");
                Object o = cMonshash.get(key);
                if (o == null) {
                    cMonshash.put(key, 1);
                } else {
                    int v = (Integer) o;
                    cMonshash.put(key, v + 1);
                }
            }
        }


        Set keys = MAKshash.keySet();
        Iterator it = keys.iterator();

        Set keys2 = ISAshash.keySet();
        Iterator it2 = keys2.iterator();

        Set keys3 = null;
        Iterator it3 = null;
        if (cMonshash != null) {
            keys3 = cMonshash.keySet();
            it3 = keys3.iterator();
        }

        ArrayList out = new ArrayList();

        //MAK
        for (int i = 0; i < MAKs.length; i++) {
            String key = MAKs[i][2] + "-" + MAKs[i][5] + "-" + MAKs[i][11];
            String add = MoreArray.toString(MAKs[i], "\t") + "\t" + (Integer) MAKshash.get(key) + "\t";
            Object o1 = ISAshash.get(key);
            int sum = 1;
            boolean ISA = false;
            boolean cmonkey = false;
            if (o1 == null) {
                add += "0\t";
            } else {
                Integer i1 = (Integer) o1;
                add += i1 + "\t";
                sum++;
                ISA = true;
            }

            if (cMonshash != null) {
                Object o2 = cMonshash.get(key);
                if (o2 == null) {
                    add += "0";
                } else {
                    Integer i2 = (Integer) o2;
                    add += i2;
                    sum++;
                    cmonkey = true;
                }
            }

            if (sum == 1)
                add += "\tM";
            else if (sum == 2 && ISA)
                add += "\tMI";
            if (cMons != null) {
                if (sum == 2 && cmonkey)
                    add += "\tMC";
                else if (sum == 3)
                    add += "\tMIC";
            }

            out.add(add);
        }

        //ISA
        for (int i = 0; i < ISAs.length; i++) {
            String key = ISAs[i][2] + "-" + ISAs[i][5] + "-" + ISAs[i][11];
            String add = MoreArray.toString(ISAs[i], "\t") + "\t";
            Object o1 = MAKshash.get(key);
            Object o2 = null;
            if (cMonshash != null) {
                o2 = cMonshash.get(key);
            }
            int sum = 1;
            boolean cmonkey = false;
            if ((o1 == null && o2 == null) || (o1 == null && o2 != null)) {
                //not in MAK and cMonkey
                if (o1 == null && o2 == null) {
                    add += "0\t" + (Integer) ISAshash.get(key) + "\t0";

                }
                //not in MAK but in cmonkey
                else if (o1 == null && o2 != null) {
                    Integer integer = (cMonshash != null ? (Integer) cMonshash.get(key) : -1);
                    add += "0\t" + (Integer) ISAshash.get(key) + "\t" + integer;
                    cmonkey = true;
                    sum++;
                }

                if (sum == 1)
                    add += "\tI";
                else if (sum == 2 && cmonkey)
                    add += "\tIC";

                out.add(add);
            }
        }

        //cMonkey
        if (cMonshash != null) {
            for (int i = 0; i < cMons.length; i++) {
                String key = cMons[i][2] + "-" + cMons[i][5] + "-" + cMons[i][11];
                String add = MoreArray.toString(cMons[i], "\t") + "\t";
                Object o1 = MAKshash.get(key);
                Object o2 = ISAshash.get(key);
                if (o1 == null && o2 == null) {
                    //not in MAK and ISA
                    if (o1 == null && o2 == null) {
                        add += "0\t0\t" + (Integer) cMonshash.get(key);
                    }

                    add += "\tC";
                    out.add(add);
                }
            }
        }

        /*
     0 i + "\t" +
       i + "_" + split1[j] + "\t" +
       split1[j] + "\t" +
       split1pval[j] + "\t" +
       i + "_" + split1[k] + "\t" +
     5 split1[k] + "\t" +
       split1pval[k] + "\t" +
       ggenes.size() + "\t" +
       pgenes.size() + "\t" +
       pggenes.size() + "\t" +
    10 ((double) pggenes.size() / (double) v.genes.length) + "\t" +
       vnew.exp_mean+ "\t" +
       TFlabel[i] + "\t" +
       meanp + "\t" +
    14 (1 - meanp);
        */


        ArrayList meanout = new ArrayList();
        //calculate mean for each label1-label2-TF sets
        boolean[] done = new boolean[out.size()];
        for (int i = 0; i < out.size(); i++) {
            if (!done[i]) {
                String[] cur = ((String) out.get(i)).split("\t");
                //System.out.println(MoreArray.toString(cur, ","));
                String key = cur[2] + "_" + cur[5] + "_" + cur[12];
                ArrayList a3 = new ArrayList();
                try {
                    a3.add(Double.parseDouble(cur[3]));
                } catch (NumberFormatException e) {
                    a3.add(Double.NaN);
                    e.printStackTrace();
                }
                ArrayList a6 = new ArrayList();
                try {
                    a6.add(Double.parseDouble(cur[6]));
                } catch (NumberFormatException e) {
                    a6.add(Double.NaN);
                    e.printStackTrace();
                }
                ArrayList a7 = new ArrayList();
                a7.add(Double.parseDouble(cur[7]));
                ArrayList a8 = new ArrayList();
                a8.add(Double.parseDouble(cur[8]));
                ArrayList a9 = new ArrayList();
                a9.add(Double.parseDouble(cur[9]));
                ArrayList a10 = new ArrayList();
                a10.add(Double.parseDouble(cur[10]));
                ArrayList a11 = new ArrayList();
                a11.add(Double.parseDouble(cur[11]));
                ArrayList a13 = new ArrayList();
                a13.add(Double.parseDouble(cur[13]));
                ArrayList a14 = new ArrayList();
                a14.add(Double.parseDouble(cur[14]));
                ArrayList a15 = new ArrayList();
                a15.add(Double.parseDouble(cur[15]));
                ArrayList a16 = new ArrayList();
                a16.add(Double.parseDouble(cur[16]));
                ArrayList a17 = new ArrayList();
                a17.add(Double.parseDouble(cur[17]));
                //ArrayList a18 = new ArrayList();
                //a18.add(Double.parseDouble(cur[18]));
                //ArrayList a19 = new ArrayList();
                //a19.add(Double.parseDouble(cur[19]));

                HashMap TFlabs = new HashMap();
                HashMap Methodlabs = new HashMap();
                TFlabs.put(cur[12], 1);
                Methodlabs.put(cur[18], 1);
                int count = 0;
                for (int j = 0; j < out.size(); j++) {
                    if (i != j && !done[j]) {
                        String[] cur2 = ((String) out.get(j)).split("\t");
                        String key2 = cur2[2] + "_" + cur2[5] + "_" + cur2[12];

                        if (key.equals(key2)) {
                            count++;
                            System.out.println("matched key " + key + "\t" + count);

                            Object o = TFlabs.get(cur2[12]);
                            if (o == null)
                                TFlabs.put(cur2[12], 1);
                            else {
                                int v = (Integer) TFlabs.get(cur2[12]);
                                TFlabs.put(cur2[12], v + 1);
                            }

                            Object o2 = Methodlabs.get(cur2[18]);
                            if (o2 == null)
                                Methodlabs.put(cur2[18], 1);
                            //else {
                            //   int v = (Integer) o2;
                            //  Methodlabs.put(cur2[18], v + 1);
                            //}

                            try {
                                a3.add(Double.parseDouble(cur2[3]));
                            } catch (NumberFormatException e) {
                                a3.add(Double.NaN);
                                e.printStackTrace();
                            }
                            try {
                                a6.add(Double.parseDouble(cur2[6]));
                            } catch (NumberFormatException e) {
                                a6.add(Double.NaN);
                                e.printStackTrace();
                            }
                            a7.add(Double.parseDouble(cur2[7]));
                            a8.add(Double.parseDouble(cur2[8]));
                            a9.add(Double.parseDouble(cur2[9]));
                            a10.add(Double.parseDouble(cur2[10]));
                            a11.add(Double.parseDouble(cur2[11]));
                            a13.add(Double.parseDouble(cur2[13]));
                            a14.add(Double.parseDouble(cur2[14]));
                            a15.add(Double.parseDouble(cur2[15]));
                            a16.add(Double.parseDouble(cur2[16]));
                            a17.add(Double.parseDouble(cur2[17]));
                            //a18.add(Double.parseDouble(cur[18]));
                            //a19.add(Double.parseDouble(cur[19]));

                            done[j] = true;
                        }
                    }
                }
                if (a3.size() > 1)
                    System.out.println("a3 " + MoreArray.arrayListtoString(a3));
                double mean3 = stat.doubleSampAvg(a3);
                double mean6 = stat.doubleSampAvg(a6);
                double mean7 = stat.doubleSampAvg(a7);
                double mean8 = stat.doubleSampAvg(a8);
                double mean9 = stat.doubleSampAvg(a9);
                double mean10 = stat.doubleSampAvg(a10);
                double mean11 = stat.doubleSampAvg(a11);
                double mean13 = stat.doubleSampAvg(a13);
                double mean14 = stat.doubleSampAvg(a14);
                double mean15 = stat.doubleSampAvg(a15);
                double mean16 = stat.doubleSampAvg(a16);
                double mean17 = stat.doubleSampAvg(a17);

                Set TFkeys = TFlabs.keySet();
                Iterator iter = TFkeys.iterator();
                int max = 0;
                String maxTF = "";
                while (iter.hasNext()) {
                    String key2 = (String) iter.next();
                    maxTF += key2 + "-";
                    /*int vi = (Integer) TFlabs.get(key2);
                    if (vi > max) {
                        max = vi;
                        maxTF = key2;
                    }*/
                }
                maxTF = maxTF.substring(0, maxTF.length() - 2);

                Set Methodkeys = Methodlabs.keySet();
                Iterator iter2 = Methodkeys.iterator();
                int max2 = 0;
                String methods = "";
                while (iter2.hasNext()) {
                    String key2 = (String) iter2.next();
                    methods += key2;
                }
                char[] ar = methods.toCharArray();
                Arrays.sort(ar);

                if (mean3 != Double.parseDouble(cur[3]))
                    System.out.println("test mean " + mean3 + "\t" + cur[3] + "\t" + maxTF + "\t" + methods);

                String add = cur[0] + "\t" + cur[1] + "\t" + cur[2] + "\t" + mean3 + "\t" + cur[4] + "\t" +
                        cur[5] + "\t" + mean6 + "\t" +
                        mean7 + "\t" + mean8 + "\t" + mean9 + "\t" + mean10 + "\t" +
                        mean11 + "\t" + maxTF + "\t" + mean13 + "\t" +
                        mean14 + "\t" + mean15 + "\t" + mean16 + "\t" + mean17 + "\t" + methods;// "\t" + mean18 +
                meanout.add(add);
            }
        }


        String so = prefix + "_MAK_All_vs_All_compare.txt";
        TextFile.write(out, so);
        System.out.println("wrote " + so);

        String som = prefix + "_MAK_All_vs_All_compare_mean.txt";
        TextFile.write(meanout, som);
        System.out.println("wrote " + som);

        //SIMPLE OUTPUT
        ArrayList out4 = new ArrayList();

        it = keys.iterator();
        //MAK vs ISA and cMonkey
        while (it.hasNext()) {
            String s = (String) it.next();
            String add = "" + s + "\t" + (Integer) MAKshash.get(s) + "\t";
            Object o1 = ISAshash.get(s);
            int sum = 1;
            boolean ISA = false;
            boolean cmonkey = false;
            if (o1 == null) {
                add += "0\t";
            } else {
                Integer i1 = (Integer) o1;
                add += i1 + "\t";
                sum++;
                ISA = true;
            }

            Object o2 = cMonshash.get(s);
            if (o2 == null) {
                add += "0";
            } else {
                Integer i2 = (Integer) o2;
                add += i2;
                sum++;
                cmonkey = true;
            }

            if (sum == 1)
                add += "\tM";
            else if (sum == 2 && ISA)
                add += "\tMI";
            else if (sum == 2 && cmonkey)
                add += "\tMC";
            else if (sum == 3)
                add += "\tMIC";

            out4.add(add);
        }

        it2 = keys2.iterator();
        //ISA vs
        while (it2.hasNext()) {
            String s = (String) it2.next();
            String add = "" + s + "\t";
            Object o1 = MAKshash.get(s);
            Object o2 = null;
            if (cMonshash != null) {
                o2 = cMonshash.get(s);
                int sum = 1;
                boolean cmonkey = false;

                if ((o1 == null && o2 == null) || (o1 == null && o2 != null)) {
                    //not in MAK and cMonkey
                    if (o1 == null && o2 == null) {
                        add += "0\t" + (Integer) ISAshash.get(s) + "\t0";

                    }
                    //not in MAK but in cmonkey
                    else if (o1 == null && o2 != null) {
                        Integer integer = cMonshash != null ? (Integer) cMonshash.get(s) : -1;
                        add += "0\t" + (Integer) ISAshash.get(s) + "\t" + integer;
                        cmonkey = true;
                        sum++;
                    }

                    if (sum == 1)
                        add += "\tI";
                    else if (sum == 2 && cmonkey)
                        add += "\tIC";


                    out4.add(add);
                }
            }
        }

        if (cMonshash != null) {
            it3 = keys3.iterator();
            //cMonkey vs
            while (it3.hasNext()) {
                String s = (String) it3.next();
                String add = "" + s + "\t";
                Object o1 = MAKshash.get(s);
                Object o2 = ISAshash.get(s);
                if (o1 == null && o2 == null) {
                    //not in MAK and ISA
                    if (o1 == null && o2 == null) {
                        add += "0\t0\t" + (Integer) cMonshash.get(s);
                    }

                    add += "\tC";
                    out4.add(add);
                }
            }
        }


        String so4 = prefix + "_cMonkey_plus_ISA_vs_MAK.txt";
        TextFile.write(out4, so4);
        System.out.println("wrote " + so4);
    }

    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 4) {
            CompareValid rm = new CompareValid(args);
        } else {
            System.out.println("syntax: java DataMining.func.CompareValid\n" +
                    "<MAK-clust>\n" +
                    "<ISA>\n" +
                    "<cMonkey>\n" +
                    "<outfile prefix>"
            );
        }
    }
}
