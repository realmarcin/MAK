package DataMining.eval;

import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SummarizeUniBic {

    String[] patterns = {"square", "narrow", "overlap"};
    String[] types = {"typeI_", "typeII_", "typeIII_", "typeIV_", "typeV_", "typeVI_"};
    String[] size = {"15_15", "20_20", "25_25"};
    String[] recov_relev = {"recovery", "relevance"};


    int[][] pattern_size_type_recrel_mask;


    /**
     * @param args
     */
    public SummarizeUniBic(String[] args) {

        File dir = new File(args[0]);
        String[] list = dir.list();

        ArrayList pattern_size_type_mask_AR = new ArrayList();
        ArrayList mean_data = new ArrayList();
        ArrayList paths = new ArrayList();
        ArrayList labels = new ArrayList();

        for (int i = 0; i < list.length; i++) {
            //System.out.println(list[i]);
            int[] mask = getMask(list[i]);

            try {
                String inpath = args[0] + "/" + list[i];
                Path filePath = Paths.get(inpath);
                Scanner scanner = new Scanner(filePath);
                ArrayList<Double> data = new ArrayList<Double>();
                while (scanner.hasNext()) {
                    double test = scanner.nextDouble();
                    if (test != Double.NaN) {
                        try {
                            data.add(test);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        scanner.next();
                    }
                }

                double[] array = MoreArray.ArrayListtoDouble(data);
                //MoreArray.printArray(array);
                double mean = stat.avg(array);
                pattern_size_type_mask_AR.add(mask);
                mean_data.add(mean);

                String nowprefix = patterns[mask[0]] + "_bic_" + size[mask[1]] + "_";
                int end_index = list[i].indexOf(recov_relev[mask[3]]);//patterns[mask[0]] + "_" + size[mask[2]]);
                String label = list[i].substring(nowprefix.length()+1, end_index - 1);
                //System.out.println(label);
                labels.add(label);
                paths.add(inpath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        double[][][] max__patterns_size_types = new double[patterns.length][size.length][types.length];
        String[][][] maxlabel__patterns_size_types = new String[patterns.length][size.length][types.length];
        for (int a = 0; a < patterns.length; a++) {
            for (int b = 0; b < size.length; b++) {
                for (int c = 0; c < types.length; c++) {

                    for (int i = 0; i < pattern_size_type_mask_AR.size(); i++) {
                        double curmax = 0;
                        int[] indices = (int[]) pattern_size_type_mask_AR.get(i);

                        //if matching case
                        if (indices[0] == a && indices[1] == b && indices[2] == c) {

                            String curlabel = (String) labels.get(i);
                            //System.out.println("curlabel1 " + i + "\t\t" + curlabel + "\t" + paths.get(i));
                            if (indices[3] == 0) {
                                curmax = (double) mean_data.get(i);
                                //System.out.println("relevance "+(double) mean_data.get(i));
                            } else if (indices[3] == 1) {
                                curmax = (double) mean_data.get(i);
                                //System.out.println("recovery "+(double) mean_data.get(i));
                            }
                            for (int j = 0; j < pattern_size_type_mask_AR.size(); j++) {
                                if (i != j) {
                                    String test_label = (String) labels.get(j);
                                    //System.out.println("test_label " + j + "\t" + test_label);
                                    if (test_label.equals(curlabel)) {
                                        int[] indices2 = (int[]) pattern_size_type_mask_AR.get(j);
                                        //System.out.println("curlabel2 " + curlabel + "\t" + paths.get(j));
                                        if (indices2[3] == 0) {
                                            curmax += (double) mean_data.get(j);
                                            //System.out.println("relevance "+(double) mean_data.get(i));
                                        } else if (indices2[3] == 1) {
                                            curmax += (double) mean_data.get(j);
                                            //System.out.println("recovery "+(double) mean_data.get(i));
                                        }
                                        //System.out.println("max " + labels.get(i) + "\t" + curmax);//+ "\t" + labels.get(j)
                                        if (curmax > max__patterns_size_types[a][b][c] ) {
                                            System.out.println("NEW MAX " + patterns[a] + "\t" + size[b] + "\t" + types[c] + "\t\t" + curmax);
                                            max__patterns_size_types[a][b][c] = curmax;
                                            maxlabel__patterns_size_types[a][b][c] = test_label;
                                            System.out.println("NEW MAX " + paths.get(i));
                                            System.out.println("NEW MAX " + test_label);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int a = 0; a < patterns.length; a++) {
            for (int b = 0; b < size.length; b++) {
                for (int c = 0; c < types.length; c++) {
                    if(max__patterns_size_types[a][b][c] > 0) {
                        System.out.println("FINAL " + patterns[a] + "\t" + size[b] + "\t" + types[c] + "\t\t" + max__patterns_size_types[a][b][c]);
                        System.out.println("FINAL " + maxlabel__patterns_size_types[a][b][c] );
                    }
                }
            }
        }

    }


    /**
     * @param s
     * @return
     */
    public int[] getMask(String s) {

        int[] ret = new int[4];

        for (int i = 0; i < patterns.length; i++) {
            if (s.indexOf(patterns[i]) != -1) {
                ret[0] = i;
                break;
            }
        }
        for (int i = 0; i < size.length; i++) {
            if (s.indexOf(size[i]) != -1) {
                ret[1] = i;
                break;
            }
        }
        for (int i = 0; i < types.length; i++) {
            if (s.indexOf(types[i]) != -1) {
                ret[2] = i;
                break;
            }
        }

        for (int i = 0; i < recov_relev.length; i++) {
            if (s.indexOf(recov_relev[i]) != -1) {
                ret[3] = i;
                break;
            }
        }
        return ret;
    }


    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 1) {
            SummarizeUniBic rm = new SummarizeUniBic(args);
        } else {
            System.out.println("syntax: java DataMining.eval.SummarizeUniBic\n" +
                    "< dir of dirs >"
            );
        }
    }
}
