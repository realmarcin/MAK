package DataMining.func;

import DataMining.ValueBlock;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/11/13
 * Time: 1:45 PM
 */
public class BuildMetaNet {


    //0 expr
    //1 expr_TF
    //2 expr_TF_inter_feat
    int mode = 1;
    double block_overlap_cutoff = 0.01;//0.25;
    HashMap<String, String> funmap = new HashMap<String, String>();
    HashMap<String, String> TFmap = new HashMap<String, String>();
    HashMap<String, String> localmap = new HashMap<String, String>();
    HashMap<String, String> overmap = new HashMap<String, String>();
    HashMap<String, String> genenamesmap = new HashMap<String, String>();
    HashMap<String, String> GOmap = new HashMap<String, String>();
    HashMap<String, String> pathmap = new HashMap<String, String>();
    HashMap<String, String> TIGRrolemap = new HashMap<String, String>();
    HashMap<String, String> nodemap = new HashMap<String, String>();

    String[][] biclustermotifs;
    String[][] tabfile;

    double TFcor_cutoff = 0.7;
    double meanbinding_cutoff = 0.8;
    ArrayList exp_profiles;


    /**
     * @param args
     */
    public BuildMetaNet(String[] args) {

        loadMaps(args);

        outTab();

        //outSIF();
    }

    /**
     * node1 node2 val type1 type2
     */
    private void outTab() {
        ArrayList outar = new ArrayList();

        ArrayList vals = new ArrayList();
        Set s = overmap.keySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String val = (String) overmap.get(key);
            vals.add(Double.parseDouble(val));
            //System.out.println("over "+key);
            String[] split = key.split("_");
            outar.add(split[0] + "\t" + split[1] + "\t" + val + "\tb\tb\tb2b\t1\t" + 0 + "\t" + 0);

            Object o = nodemap.get(split[0]);
            if (o == null) {
                nodemap.put(split[0], "bicluster");
            }

            Object o2 = nodemap.get(split[1]);
            if (o2 == null) {
                nodemap.put(split[1], "bicluster");
            }
        }
        double[] data = MoreArray.ArrayListtoDouble(vals);
        double mean = stat.avg(data);

        System.out.println("added overlap " + outar.size());


        Set s3 = TFmap.keySet();
        Iterator it3 = s3.iterator();
        while (it3.hasNext()) {
            String key = (String) it3.next();
            String val = (String) TFmap.get(key);
            //outar.add(key + "\t" + val + "\t" + mean + "\tb\tt");

            String[] all = val.split("_");
            for (int i = 0; i < all.length; i++) {
                String TFstr = all[i].substring(0, all[i].length() - 1).toUpperCase();
                outar.add(key + "\t" + TFstr + "\t" + mean + "\tb\tt\tb2t\t4\t" + 0 + "\t" + 0);

                Object o = nodemap.get(TFstr);
                if (o == null) {
                    System.out.print("" + TFstr + ",");
                    nodemap.put(TFstr, "TF");
                }
            }
        }
        System.out.println("added TF " + outar.size());

        for (int i = 0; i < biclustermotifs.length; i++) {
            double meanprob = Double.parseDouble(biclustermotifs[i][4]);
            double TFcor = Double.parseDouble(biclustermotifs[i][7]);

            if (meanprob > meanbinding_cutoff && Math.abs(TFcor) > TFcor_cutoff) {
                //System.out.println("biclustermotifs[i][6] " + biclustermotifs[i][6]);
                //System.out.println("biclustermotifs[i] " + MoreArray.toString(biclustermotifs[i], ","));
                String suffix = biclustermotifs[i][6].substring(biclustermotifs[i][6].indexOf("_"));
                String s1 = genenamesmap.get(biclustermotifs[i][5]);
                if (s1 != null) {
                    String motifStr = s1 + suffix;
                    outar.add((Integer.parseInt(biclustermotifs[i][1]) + 1) + "\t" + motifStr + "\t" + mean + "\tb\tm\tb2m\t2\t" + Math.abs(TFcor) + "\t" + (TFcor > 0 ? "pos" : "neg"));
                    Object o = nodemap.get(motifStr);
                    if (o == null) {
                        nodemap.put(motifStr, "motif");
                    }
                }
            }
        }
        System.out.println("added bicluster-motif " + outar.size());

        for (int i = 0; i < biclustermotifs.length; i++) {
            double meanprob = Double.parseDouble(biclustermotifs[i][4]);
            double TFcor = Double.parseDouble(biclustermotifs[i][7]);
            if (meanprob > meanbinding_cutoff && Math.abs(TFcor) > TFcor_cutoff) {
                String suffix = biclustermotifs[i][6].substring(biclustermotifs[i][6].indexOf("_"));
                String s1 = genenamesmap.get(biclustermotifs[i][5]);
                if (s1 != null) {
                    outar.add(s1 + "\t" + s1 + suffix + "\t" + mean + "\tt\tm\tt2m\t3\t" + 0 + "\t" + 0);
                    Object o = nodemap.get(s1);
                    if (o == null) {
                        nodemap.put(s1, "TF");
                    }
                }
            }
        }
        System.out.println("added motif-TF " + outar.size());


        Set s2 = funmap.keySet();
        Iterator it2 = s2.iterator();
        while (it2.hasNext()) {
            String key = (String) it2.next();
            String val = (String) funmap.get(key);
            String[] all = val.split("_");
            for (int i = 0; i < all.length; i++) {
                if (!all[i].equals("other")) {
                    outar.add(key + "\t" + all[i] + "\t" + mean + "\tb\tfc\tb2fc\t5\t" + 0 + "\t" + 0);
                    Object o = nodemap.get(all[i]);
                    if (o == null) {
                        nodemap.put(all[i], "functional_category");
                    }
                }
            }
        }
        System.out.println("added functional_category " + outar.size());

        Set s4 = localmap.keySet();
        Iterator it4 = s4.iterator();
        while (it4.hasNext()) {
            String key = (String) it4.next();
            String val = (String) localmap.get(key);
            //outar.add(key + "\t" + val + "\t" + mean + "\tb\tl\t4");

            String[] all = val.split("_");
            for (int i = 0; i < all.length; i++) {
                outar.add(key + "\t" + all[i] + "\t" + mean + "\tb\tl\tb2l\t6\t" + 0 + "\t" + 0);

                Object o = nodemap.get(all[i]);
                if (o == null) {
                    System.out.println("adding localization " + all[i]);
                    nodemap.put(all[i], "localization");
                }

                int index = MoreArray.getArrayIndexOf(all[i], AssignFunClass.functional_terms);
                if (index != -1) {
                    String fc = AssignFunClass.functional_groups[AssignFunClass.functional_group_index[index]];
                    outar.add(all[i] + "\t" + fc + "\t" + mean + "\tl\tfc\tl2fc\t6\t" + 0 + "\t" + 0);

                    Object o2 = nodemap.get(fc);
                    if (o2 == null) {
                        //System.out.println("adding functional category " + all[i]);
                        nodemap.put(fc, "functional_category");
                    }
                }

                int index2 = MoreArray.getArrayIndexOf(all[i], Localize.localization_terms);
                if (index2 != -1) {
                    String lc = Localize.localization_groups[Localize.localization_group_index[index2]];
                    outar.add(all[i] + "\t" + lc + "\t" + mean + "\tl\tlc\tl2lc\t6\t" + 0 + "\t" + 0);

                    Object o2 = nodemap.get(lc);
                    if (o2 == null) {
                        //System.out.println("adding functional category " + all[i]);
                        nodemap.put(lc, "localization_category");
                    }
                }


            }
        }
        System.out.println("added localization " + outar.size());


        Set s5 = GOmap.keySet();
        Iterator it5 = s5.iterator();
        while (it5.hasNext()) {
            String key = (String) it5.next();
            String val = (String) GOmap.get(key);
            //outar.add(key + "\t" + val + "\t" + mean + "\tb\tl\t5");

            String[] all = val.split("_");
            for (int i = 0; i < all.length; i++) {
                outar.add(key + "\t" + all[i] + "\t" + mean + "\tb\tg\tb2g\t7\t" + 0 + "\t" + 0);
                Object o = nodemap.get(all[i]);
                if (o == null) {
                    nodemap.put(all[i], "GO");
                }

                int index = MoreArray.getArrayIndexOf(all[i], AssignFunClass.functional_terms);
                if (index != -1) {
                    String fc = AssignFunClass.functional_groups[AssignFunClass.functional_group_index[index]];
                    outar.add(all[i] + "\t" + fc + "\t" + mean + "\tg\tfc\tg2fc\t6\t" + 0 + "\t" + 0);

                    Object o2 = nodemap.get(fc);
                    if (o2 == null) {
                        //System.out.println("adding functional category " + all[i]);
                        nodemap.put(fc, "functional_category");
                    }
                }
            }
        }
        System.out.println("added GO " + outar.size());

        Set s5a = GOmap.keySet();
        Iterator it5a = s5a.iterator();
        while (it5a.hasNext()) {
            String key = (String) it5a.next();
            String val = (String) GOmap.get(key);

            String[] all = val.split("_");
            for (int i = 0; i < all.length; i++) {
                String local = Localize.makeGroupLabel(all[i]);
                String[] all2 = local.split("_");
                for (int j = 0; j < all2.length; j++) {
                    if (!all2[j].equals("none")) {
                        //System.out.println("GO localization " + i + "\t" + j + "\t" + all[i] + "\t" + all2[j]);
                        outar.add(key + "\t" + all2[j] + "\t" + mean + "\tb\tl\tb2l\t6\t" + 0 + "\t" + 0);
                        Object o2 = nodemap.get(all2[j]);
                        if (o2 == null) {
                            nodemap.put(all2[j], "localization");
                        } else {
                            nodemap.remove(all2[j]);
                            nodemap.put(all2[j], "localization");
                        }

                        int index = MoreArray.getArrayIndexOf(all2[j], AssignFunClass.functional_terms);
                        if (index != -1) {
                            String fc = AssignFunClass.functional_groups[AssignFunClass.functional_group_index[index]];
                            outar.add(all2[j] + "\t" + fc + "\t" + mean + "\tg\tfc\tg2fc\t6\t" + 0 + "\t" + 0);

                            Object o3 = nodemap.get(fc);
                            if (o3 == null) {
                                //System.out.println("adding functional category " + all[i]);
                                nodemap.put(fc, "functional_category");
                            }
                        }

                        int index2 = MoreArray.getArrayIndexOf(all[i], Localize.localization_terms);
                        if (index2 != -1) {
                            String lc = Localize.localization_groups[Localize.localization_group_index[index2]];
                            outar.add(all[i] + "\t" + lc + "\t" + mean + "\tg\tlc\tg2lc\t6\t" + 0 + "\t" + 0);

                            Object o4 = nodemap.get(lc);
                            if (o4 == null) {
                                //System.out.println("adding functional category " + all[i]);
                                nodemap.put(lc, "localization_category");
                            }
                        }


                    }
                }
            }
        }
        System.out.println("added localization " + outar.size());

        Set s6 = pathmap.keySet();
        Iterator it6 = s6.iterator();
        while (it6.hasNext()) {
            String key = (String) it6.next();
            String val = (String) pathmap.get(key);
            //outar.add(key + "\t" + val + "\t" + mean + "\tb\tl\t6");

            String[] all = val.split("_");
            for (int i = 0; i < all.length; i++) {
                outar.add(key + "\t" + all[i] + "\t" + mean + "\tb\tp\tb2p\t8\t" + 0 + "\t" + 0);
                Object o = nodemap.get(all[i]);
                if (o == null) {
                    nodemap.put(all[i], "pathway");
                }

                int index = MoreArray.getArrayIndexOf(all[i], AssignFunClass.functional_terms);
                if (index != -1) {
                    String fc = AssignFunClass.functional_groups[AssignFunClass.functional_group_index[index]];
                    outar.add(all[i] + "\t" + fc + "\t" + mean + "\tp\tfc\tp2fc\t6\t" + 0 + "\t" + 0);

                    Object o2 = nodemap.get(fc);
                    if (o2 == null) {
                        //System.out.println("adding functional category " + all[i]);
                        nodemap.put(fc, "functional_category");
                    }
                }
            }
        }
        System.out.println("added path " + outar.size());


        Set s7 = TIGRrolemap.keySet();
        Iterator it7 = s7.iterator();
        while (it7.hasNext()) {
            String key = (String) it7.next();
            String val = (String) TIGRrolemap.get(key);
            //outar.add(key + "\t" + val + "\t" + mean + "\tb\tl\t7");

            String[] all = val.split("_");
            for (int i = 0; i < all.length; i++) {
                outar.add(key + "\t" + all[i] + "\t" + mean + "\tb\ttr\tb2tr\t9\t" + 0 + "\t" + 0);
                Object o = nodemap.get(all[i]);
                if (o == null) {
                    nodemap.put(all[i], "TIGRrole");
                }

                int index = MoreArray.getArrayIndexOf(all[i], AssignFunClass.functional_terms);
                if (index != -1) {
                    String fc = AssignFunClass.functional_groups[AssignFunClass.functional_group_index[index]];
                    outar.add(all[i] + "\t" + fc + "\t" + mean + "\ttr\tfc\ttr2fc\t6\t" + 0 + "\t" + 0);

                    Object o2 = nodemap.get(fc);
                    if (o2 == null) {
                        //System.out.println("adding functional category " + all[i]);
                        nodemap.put(fc, "functional_category");
                    }
                }
            }
        }
        System.out.println("added TIGRrole " + outar.size());


        for (int i = 0; i < Localize.localization_terms.length; i++) {
            if (!Localize.localization_terms[i].equals("none")) {
                //System.out.println(i + "\t" + Localize.localization_terms[i] + "\t" + Localize.localization_group_index[i]);
                //System.out.println(Localize.localization_groups[Localize.localization_group_index[i]]);
                Object o = nodemap.get(Localize.localization_terms[i]);
                Object o2 = nodemap.get(Localize.localization_groups[Localize.localization_group_index[i]]);
                if (o2 == null) {//o == null ||
                    //nodemap.put(Localize.localization_terms[i], "localization");
                    if (o == null)
                        System.out.println("unused localization " + Localize.localization_terms[i]);
                    if (o2 == null)
                        System.out.println("unused localization_category " + Localize.localization_groups[Localize.localization_group_index[i]]);
                } else if (o != null) {//o2 != null ||
                    //nodemap.put(Localize.localization_terms[i], "localization");
                    nodemap.put(Localize.localization_groups[Localize.localization_group_index[i]], "localization_category");
                    outar.add(Localize.localization_terms[i] + "\t" + Localize.localization_groups[Localize.localization_group_index[i]] + "\t" + mean + "\tg\tlc\tg2lc\t10\t0\t0");
                    System.out.println("added localization to localization_category " + Localize.localization_terms[i] + "\t" + Localize.localization_groups[Localize.localization_group_index[i]]);
                }
            }
        }
        System.out.println("added extra localization and localization_category " + outar.size());

        /*for (int i = 0; i < AssignFunClass.functional_terms.length; i++) {
            Object o = nodemap.get(AssignFunClass.functional_terms[i]);
            Object o2 = nodemap.get(AssignFunClass.functional_groups[AssignFunClass.functional_group_index[i]]);
            if (o == null) {//|| o2 == null
                if (o == null)
                    System.out.println("extra function " + AssignFunClass.functional_terms[i]);
                if (o2 == null)
                    System.out.println("extra functional_category " + AssignFunClass.functional_groups[AssignFunClass.functional_group_index[i]]);
            } else if (o != null) {//|| o2 != null
                //nodemap.put(AssignFunClass.functional_terms[i], "function");
                Object test = (Object) nodemap.get(AssignFunClass.functional_groups[AssignFunClass.functional_group_index[i]]);
                if (test == null) {
                    if (!((String) test).equals("functional_category")) {
                        nodemap.put(AssignFunClass.functional_groups[AssignFunClass.functional_group_index[i]], "functional_category");
                        System.out.println("added function to functional_category " + AssignFunClass.functional_groups[AssignFunClass.functional_group_index[i]]);
                    }
                }
                outar.add(AssignFunClass.functional_terms[i] + "\t" + AssignFunClass.functional_groups[AssignFunClass.functional_group_index[i]] + "\t" + mean + "\tf\tfc\tf2fc\t11\t0\t0");
                System.out.println("added function to functional_category " + AssignFunClass.functional_terms[i] + "\t" + AssignFunClass.functional_groups[AssignFunClass.functional_group_index[i]]);
            }

        }
        System.out.println("added extra functional terms and functional_category " + outar.size());*/


        for (int i = 0; i < exp_profiles.size(); i++) {
            String[][] expdata = (String[][]) exp_profiles.get(i);
            System.out.println("doing " + i);
            if (expdata == null) {
                System.out.println("expdata is null");
            }
            for (int j = 0; j < expdata.length; j++) {
                //System.out.println(MoreArray.toString(expdata[j], ","));
                if (Integer.parseInt(StringUtil.replace(expdata[j][1], "\t", "")) > 1) {
                    outar.add((i + 1) + "\t" + expdata[j][0] + "\t" + mean + "\tb\tc\tb2c\t9\t" + 0 + "\t" + 0);
                    Object o = nodemap.get(expdata[j][0]);
                    if (o == null) {
                        nodemap.put(expdata[j][0], "condition");
                    }
                } else {
                    System.out.println("excluding " + MoreArray.toString(expdata[j], ","));
                }
            }
        }


        String[] extrafun = null;
        String[] extraTF = null;

        ArrayList<String[]> get = null;
        if (mode == 0)
            get = addforExpr(outar, mean);
        else if (mode == 1)
            get = addforExprTF(outar, mean);
       /* else if (mode == 2)
            get = addforExprTFInterFeat(outar, nodemap, mean);*/

        extrafun = get.get(0);
        extraTF = get.get(1);
        System.out.println("added TF-to-function " + outar.size());

        String outpath1 = "meta_graph.txt";//" + this.meanbinding_cutoff + "_" + this.TFcor_cutoff + "
        System.out.println("writing " + outpath1);
        TextFile.write(outar, outpath1);


        ArrayList outnoa = new ArrayList();
        outnoa.add("nodeType");
        Set s8 = nodemap.keySet();
        Iterator it8 = s8.iterator();
        while (it8.hasNext()) {
            String key = (String) it8.next();
            String val = nodemap.get(key);
            outnoa.add(key + " = " + val);
        }

        String outpath2 = "meta_graph.noa";
        System.out.println("writing " + outpath2);
        TextFile.write(outnoa, outpath2);
    }

    /**
     * @param outar
     * @param mean
     * @return
     */
    private ArrayList<String[]> addforExpr(ArrayList outar, double mean) {

        outar.add("translation" + "\tFHL1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("translation" + "\tIFH1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("translation" + "\tRAP1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("energy metabolism" + "\tHAP4\t" + mean + "\tf\tt\tf2t\t12\t0\t0");
        outar.add("stress" + "\tCRZ1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("stress" + "\tMSN4\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("stress" + "\tYAP1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("cell cycle" + "\tSUM1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");

        String[] extrafun = {"translation", "energy metabolism", "stress", "cell cycle"};
        for (int i = 0; i < extrafun.length; i++) {
            Object o = nodemap.get(extrafun[i]);
            if (o == null) {
                nodemap.put(extrafun[i], "functional_category");
                System.out.println("WARNING added extra functional_category " + extrafun[i]);
            }
        }
        String[] extraTF = {"FHL1", "IFH1", "RAP1", "HAP4", "CRZ1", "MSN4", "YAP1", "SUM1"};
        for (int i = 0; i < extraTF.length; i++) {
            Object o = nodemap.get(extraTF[i]);
            if (o == null) {
                nodemap.put(extraTF[i], "TF");
                System.out.println("WARNING added extra TF " + extraTF[i]);
            }
        }

        ArrayList<String[]> ret = new ArrayList<String[]>();
        ret.add(extrafun);
        ret.add(extraTF);
        return ret;
    }

    /**
     * @param outar
     * @param mean
     * @return
     */
    private ArrayList addforExprTF(ArrayList outar, double mean) {
        //IFH1,FHL1,RAP1,SUM1,
        //GCN4,SKN7,HAP5,SOK2,MBP1,SWI4,FKH2,MET32,CBF1,FLO8,HSF1,NRG1,GIS1,BAS1,MET31
        outar.add("translation" + "\tFHL1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("translation" + "\tIFH1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("translation" + "\tRAP1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("energy metabolism" + "\tHAP5\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        //outar.add("amino acid metabolism" + "\tGCN4\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        //outar.add("cell " + "\tSKN7\t" + mean + "\tf\tt\tf2t\t12\t0\t0");
        //Nuclear response regulator and transcription factor; physically interacts with the Tup1-Cyc8 complex and recruits Tup1p to its targets; part of a branched two-component signaling system; required for optimal induction of heat-shock genes in response to oxidative stress; involved in osmoregulation; SKN7 has a paralog, HMS2, that arose from the whole genome duplication
        //outar.add("cell " + "\tSOK2\t" + mean + "\tf\tt\tf2t\t12\t0\t0");
        //Nuclear protein that negatively regulates pseudohyphal differentiation; plays a regulatory role in the cyclic AMP (cAMP)-dependent protein kinase (PKA) signal transduction pathway; SOK2 has a paralog, PHD1, that arose from the whole genome duplication (1, 2, 3)
        //outar.add("cell cycle" + "\tMBP1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("cell cycle" + "\tSWI4\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        //outar.add("cell cycle" + "\tFKH2\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        outar.add("amino acid metabolism" + "\tMET32\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        //outar.add("cell " + "\tCBF1\t" + mean + "\tf\tt\tf2t\t12\t0\t0");
        //Dual function helix-loop-helix protein; binds the motif CACRTG present at several sites including MET gene promoters and centromere DNA element I (CDEI); affects nucleosome positioning at this motif; associates with other transcription factors such as Met4p and Isw1p to mediate transcriptional activation or repression; associates with kinetochore proteins and required for efficient chromosome segregation; protein abundance increases in response to DNA replication stress
        outar.add("cell " + "\tFLO8\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        //Transcription factor required for flocculation, diploid filamentous growth, and haploid invasive growth; genome reference strain S288C and most laboratory strains have a mutation in this gene
        outar.add("stress" + "\tHSF1\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");
        //outar.add("cell " + "\tNRG1\t" + mean + "\tf\tt\tf2t\t12\t0\t0");
        //Transcriptional repressor; recruits the Cyc8p-Tup1p complex to promoters; mediates glucose repression and negatively regulates a variety of processes including filamentous growth and alkaline pH response; NRG1 has a paralog, NRG2, that arose from the whole genome duplication
        //outar.add("cell " + "\tGIS1\t" + mean + "\tf\tt\tf2t\t12\t0\t0");
        //Histone demethylase and transcription factor; regulates genes during nutrient limitation; negatively regulates DPP1, PHR1; activity modulated by limited proteasome-mediated proteolysis; has JmjC and JmjN domain in N-terminal region that interact, promoting stability and proper transcriptional activity; contains two transactivating domains downstream of Jmj domains and a C-terminal DNA binding domain; GIS1 has a paralog, RPH1, that arose from the whole genome duplication
        //outar.add("cell " + "\tBAS1\t" + mean + "\tf\tt\tf2t\t12\t0\t0");
        //Myb-related transcription factor involved in regulating basal and induced expression of genes of the purine and histidine biosynthesis pathways; also involved in regulation of meiotic recombination at specific genes
        outar.add("amino acid metabolism" + "\tMET31\t" + mean + "\tfc\tt\tfc2t\t12\t0\t0");

        String[] extrafun = {"translation", "energy metabolism", "stress", "cell cycle"};
        for (int i = 0; i < extrafun.length; i++) {
            Object o = nodemap.get(extrafun[i]);
            if (o == null) {
                nodemap.put(extrafun[i], "functional_category");
                System.out.println("WARNING added extra functional_category " + extrafun[i]);
            }
        }
        String[] extraTF = {"FHL1", "IFH1", "RAP1", "HAP4", "CRZ1", "MSN4", "YAP1", "SUM1"};
        for (int i = 0; i < extraTF.length; i++) {
            Object o = nodemap.get(extraTF[i]);
            if (o == null) {
                nodemap.put(extraTF[i], "TF");
                System.out.println("WARNING added extra TF " + extraTF[i]);
            }
        }

        ArrayList ret = new ArrayList();
        ret.add(extrafun);
        ret.add(extraTF);
        return ret;
    }

    /**
     *
     */
   /* private void outSIF() {
        ArrayList outar = new ArrayList();

        ArrayList vals = new ArrayList();
        Set s = overmap.keySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String val = (String) overmap.get(key);
            vals.add(Double.parseDouble(val));
            outar.add(key + " = " + val);
        }
        double[] data = MoreArray.ArrayListtoDouble(vals);
        double mean = stat.avg(data);

        System.out.println("added overlap " + outar.size());

        Set s2 = funmap.keySet();
        Iterator it2 = s2.iterator();
        while (it2.hasNext()) {
            String key = (String) it2.next();
            String val = (String) funmap.get(key);
            outar.add(key + " ((pp)) " + val + " = " + mean);
        }
        System.out.println("added functional class " + outar.size());

        Set s3 = TFmap.keySet();
        Iterator it3 = s3.iterator();
        while (it3.hasNext()) {
            String key = (String) it3.next();
            String val = (String) TFmap.get(key);
            outar.add(key + " ((pp)) " + val + " = " + mean);
        }
        System.out.println("added TF " + outar.size());

        Set s4 = localmap.keySet();
        Iterator it4 = s4.iterator();
        while (it4.hasNext()) {
            String key = (String) it4.next();
            String val = (String) localmap.get(key);
            outar.add(key + " ((pp)) " + val + " = " + mean);
        }
        System.out.println("added localization " + outar.size());


        outar.add("translation" + " ((pp)) FHL1 = " + mean);
        outar.add("translation" + " ((pp)) IFH1 = " + mean);
        outar.add("translation" + " ((pp)) RAP1 = " + mean);
        outar.add("amino_acid_metabolism" + " ((pp)) GCN4 = " + mean);
        outar.add("stress" + " ((pp)) CRZ1 = " + mean);
        outar.add("stress" + " ((pp)) MSN4 = " + mean);
        outar.add("stress" + " ((pp)) YAP1 = " + mean);
        outar.add("cell_cycle" + " ((pp)) SUM1 = " + mean);
        System.out.println("added TF-to-function " + outar.size());

        String outpath1 = "meta_graph.sif";
        System.out.println("writing " + outpath1);
        TextFile.write(outar, outpath1);
    }*/

    /**
     * @param args
     */
    private void loadMaps(String[] args) {
        String[] fundata = TextFile.readtoArray(args[0]);
        String[] TFdata = TextFile.readtoArray(args[1]);
        String[] localdata = TextFile.readtoArray(args[2]);
        String[] overdata = TextFile.readtoArray(args[3]);

        String[] GOdata = TextFile.readtoArray(args[6]);
        String[] pathdata = TextFile.readtoArray(args[7]);
        String[] TIGRroledata = TextFile.readtoArray(args[8]);

        for (int i = 1; i < fundata.length; i++) {
            String[] now = fundata[i].split(" = ");
            if (!now[1].equals("none")) {
                funmap.put(now[0], now[1]);
            }
        }

        for (int i = 1; i < TFdata.length; i++) {
            String[] now = TFdata[i].split(" = ");
            if (!now[1].equals("none")) {
                TFmap.put(now[0], now[1]);
            }
        }

        for (int i = 1; i < localdata.length; i++) {
            String[] now = localdata[i].split(" = ");
            if (!now[1].equals("none")) {
                localmap.put(now[0], now[1]);
            }
        }

        for (int i = 1; i < overdata.length; i++) {
            String[] now = overdata[i].split(" = ");
            //overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
            overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
        }

        biclustermotifs = TabFile.readtoArray(args[4]);

        System.out.println("biclustermotifs");
        System.out.println(MoreArray.toString(biclustermotifs[0], ","));

        tabfile = TabFile.readtoArray(args[5]);
        for (int i = 1; i < tabfile.length; i++) {
            genenamesmap.put(tabfile[i][7], tabfile[i][8]);
        }


        for (int i = 1; i < GOdata.length; i++) {
            String[] now = GOdata[i].split(" = ");
            if (!now[1].equals("none")) {
                String replace = StringUtil.replace(now[1], "GO: ", "");
                GOmap.put(now[0], replace);
            }
        }

        for (int i = 1; i < pathdata.length; i++) {
            String[] now = pathdata[i].split(" = ");
            if (!now[1].equals("none")) {
                pathmap.put(now[0], StringUtil.replace(now[1], "Path: ", ""));
            }
        }

        for (int i = 1; i < TIGRroledata.length; i++) {
            String[] now = TIGRroledata[i].split(" = ");
            if (!now[1].equals("none")) {
                TIGRrolemap.put(now[0], StringUtil.replace(now[1], "TIGRrole: ", ""));
            }
        }

        File dir = new File(args[9]);
        String[] list = dir.list();

        System.out.println("exp profile size " + (GOdata.length - 1));
        exp_profiles = MoreArray.initArrayList(GOdata.length - 1);
        for (int i = 0; i < list.length; i++) {
            if (list[i].indexOf("_exps.txt") != -1) {
                String[][] nowtabfile = TabFile.readtoArray(args[9] + "/" + list[i]);
                for (int j = 0; j < nowtabfile.length; j++) {
                   /*if (nowtabfile[j].length < 2) {
                        System.out.println(args[9] + "/" + list[i]);
                        System.out.println(MoreArray.toString(nowtabfile[j], ","));
                    }*/
                }
                int index = Integer.parseInt(list[i].substring(list[i].indexOf(".txt_") + 5, list[i].indexOf("__")));
                System.out.println("adding exp " + index);
                System.out.println("adding exp size " + nowtabfile.length);
                if (nowtabfile == null)
                    System.out.println("nowtabfile is null ");
                exp_profiles.set(index, nowtabfile);
            }
        }

        for (int i = 0; i < exp_profiles.size(); i++) {
            String[][] expdata = (String[][]) exp_profiles.get(i);
            System.out.println("doing " + i);
            if (expdata == null) {
                System.out.println("expdata is null");
            }
        }

    }


    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 10) {
            BuildMetaNet rm = new BuildMetaNet(args);
        } else {
            System.out.println("syntax: java DataMining.func.BuildMetaNet\n" +
                    "<funclass.noa>\n" +
                    "<TF.noa>\n" +
                    "<localize.noa>\n" +
                    "<overlap.eda>\n" +
                    "<bicluster motifs>\n" +
                    "<tab file>\n" +
                    "<GO noa>\n" +
                    "<Pathway noa>\n" +
                    "<TIGRrole noa>\n" +
                    "<exp profiles>"
            );
        }
    }
}
