package DataMining.func;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/4/15
 * Time: 4:46 PM
 */
public class ComparetoFitness {


    String[] valid_args = {
            "-bic", "-geneids", "-fitdata"
    };
    HashMap options;
    String[][] fit_data;
    ValueBlockList BIC;

    String[] geneids;
    String labels = "bicluster\tnum_genes\tnum_conditions\tisProt\tisHypo\tisDUF\thasData\tisEssential\thasInf\thasSig\thasStrong\thasDetrimental\thasSpec\thasCofit\thasCons\thasConsSpec\thasConsCofit";

    /**
     * @param args
     */
    public ComparetoFitness(String[] args) {

        init(args);

        ParsePath pp = new ParsePath(args[1]);

        String out = labels + "\n";
        String outratios = labels + "\n";
        for (int i = 0; i < BIC.size(); i++) {
            ValueBlock vb = (ValueBlock) BIC.get(i);

            ArrayList geneids_cur = new ArrayList();
            for (int g = 0; g < vb.genes.length; g++) {
                //System.out.println("matched "+geneids[vb.genes[g] - 1]);
                geneids_cur.add("\"" + geneids[vb.genes[g] - 1] + "\"");
            }

            //System.out.println("matched total "+geneids_cur.size());

            //5 - 14
            //isProt	isHypo	isDUF	hasData	isEssential	hasInf	hasSig	hasStrong	hasDetrimental	hasSpec	hasCofit	hasCons	hasConsSpec	hasConsCofit

            double[] curprofile = new double[14];
            for (int g = 0; g < geneids_cur.size(); g++) {
                String curg = (String) geneids_cur.get(g);
                for (int a = 0; a < fit_data.length; a++) {
                    //System.out.println("*" + fit_data[a][2] + "*");
                    if (fit_data[a][2].equals(curg)) {
                        //System.out.println("matched " + curg);
                        for (int z = 5; z < 18; z++) {
                            if (fit_data[a][z].equals("TRUE"))
                                curprofile[z - 5]++;
                        }
                    }
                }
            }
            System.out.println("finished " + i);

            out += "Bicluster" + i + "\t" + geneids_cur.size() + "\t" + vb.exps.length + "\t" + MoreArray.toString(curprofile, "\t") + "\n";
            double[] ratios = mathy.stat.norm(curprofile, (double) geneids_cur.size());
            outratios += "Bicluster" + i + "\t" + geneids_cur.size() + "\t" + vb.exps.length + "\t" + MoreArray.toString(ratios, "\t") + "\n";
        }

        TextFile.write(out, pp.getName() + "_fitness_annot_profiles.txt");
        TextFile.write(outratios, pp.getName() + "_fitness_annot_profiles_ratios.txt");
    }


    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);


        options = MapArgOptions.maptoMap(args, valid_args);

        String inBIC = null;


        if (options.get("-bic") != null) {
            inBIC = (String) options.get("-bic");
            try {
                BIC = ValueBlockListMethods.readAny(inBIC, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (options.get("-geneids") != null) {
            try {
                String[][] sarray = TabFile.readtoArray((String) options.get("-geneids"));
                System.out.println("geneids g " + sarray.length + "\t" + sarray[0].length);
                int col = 2;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                geneids = MoreArray.replaceAll(n, "\"", "");
                System.out.println("geneids gene " + geneids.length + "\t" + geneids[0]);
            } catch (Exception e) {
                System.out.println("expecting 2 cols");
                e.printStackTrace();
            }
        }

        if (options.get("-fitdata") != null) {
            String f = (String) options.get("-fitdata");
            fit_data = TabFile.readtoArray(f);
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6) {
            ComparetoFitness rm = new ComparetoFitness(args);
        } else {
            System.out.println("syntax: java DataMining.func.ComparetoFitness\n" +
                    "<-bic valueblock list>\n" +
                    "<-geneids>\n" +
                    "<-fitdata>"
            );
        }
    }


}
