package DataMining.eval;

import mathy.Matrix;
import util.MoreArray;
import util.TabFile;

import java.io.File;

import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;

public class MakeUniBicEvalTable {

    String[] patterns = {"square", "narrow", "overlap"};
    String[] size = {"15_15", "20_20", "25_25"};
    String[] types = {"typeI_", "typeII_", "typeIII_", "typeIV_", "typeV_", "typeVI_"};
    String[] recov_relev = {"recovery", "relevance"};

    /**
     * @param args
     */
    public MakeUniBicEvalTable(String[] args) {

        File dir = new File(args[0]);

        String[] list = dir.list();
        
        double[][] results = new double[2 * 3 * 3][2 * 6];

        for (int a = 0; a < list.length; a++) {
            String curfile = list[a];
            for (int i = 0; i < patterns.length; i++) {
                if (curfile.indexOf(patterns[i]) != -1) {
                    for (int j = 0; j < size.length; j++) {
                        if (curfile.indexOf(size[j]) != -1) {
                            for (int k = 0; k < types.length; k++) {
                                if (curfile.indexOf(types[k]) != -1) {
                                    for (int l = 0; l < recov_relev.length; l++) {
                                        if (curfile.indexOf(recov_relev[l]) != -1) {

                                            String curpath = args[0] + "/" + list[a];

                                            List<String> lines = Collections.emptyList();
                                            try {
                                                lines =
                                                        Files.readAllLines(Paths.get(curpath), StandardCharsets.UTF_8);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            double mean = Double.parseDouble(((String) lines.get(0)));
                                            double sd = Double.parseDouble(((String) lines.get(1)));

                                            System.out.println("i "+i + "\tj " + j + "\tk " + k + "\tl " + l);
                                            int row_index = i * 6 + (j * 2);
                                            int col_index = k * 2 + l;
                                            results[row_index][col_index] = mean;
                                            results[row_index + 1][col_index] = sd;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        TabFile.write(MoreArray.toString(results, "" ,""), "UniBic_eval_relevance_recovery_table.txt");
    }


    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 1) {
            MakeUniBicEvalTable rm = new MakeUniBicEvalTable(args);
        } else {
            System.out.println("syntax: java DataMining.eval.MakeUniBicEvalTable\n" +
                    "< dir of relevance and recovery stat files >"
            );
        }
    }
}
