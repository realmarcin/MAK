package DataMining.util;

import util.StringUtil;
import util.TabFile;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 5/10/18
 * Time: 2:46 PM
 */
public class RNAseqDBlabels {

    boolean debug = false;

    /**
     * @param args
     */
    public RNAseqDBlabels(String[] args) {

        String[][] raw_labels = TabFile.readtoArray(args[0]);
        String[] samp_labels = StringUtil.replace(raw_labels[0], "\"", "");

        String[][] diseaseData = TabFile.readtoArray(args[1] + "/diseaseStudy.txt");
        String[][] sampleData = TabFile.readtoArray(args[1] + "/sampleType.txt");
        String[][] tissueData = TabFile.readtoArray(args[1] + "/tissueSourceSite.txt");
        String[][] listData = TabFile.readtoArray(args[1] + "/TCGA_samples.txt");
        String[][] RSD_tissueData = TabFile.readtoArray(args[1] + "/RNAseqDB_tissues.txt");

        //GTEx_v7_Annotations_SampleAttributesDS.txt
        String[][] GTExdata = TabFile.readtoArray(args[2] + "/GTEx_v7_Annotations_SampleAttributesDS.txt");

        System.out.println("samples " + raw_labels.length + "\t" + raw_labels[0].length);

        String[][] outstr = new String[samp_labels.length][2];

        for (int i = 0; i < samp_labels.length; i++) {
            outstr[i][0] = samp_labels[i];
            if (samp_labels[i].startsWith("TCGA.")) {

                //if (debug)
                System.out.println("samp " + samp_labels[i]);

                for (int a = 0; a < listData.length; a++) {
                    if (listData[a][0].equals(samp_labels[i])) {
                        if (debug)
                            System.out.println(listData[a][1]);
                        break;
                    }
                }

                String[] terms = samp_labels[i].split("\\.");

                if (debug)
                    System.out.println("terms " + terms.length + "\t" + samp_labels[i].indexOf("."));

                //for (int j = 1; j < 4; j++) {
                //System.out.println("term " + terms[j]);
                for (int a = 0; a < diseaseData.length; a++) {
                    if (diseaseData[a][0].equals(terms[1])) {
                        if (debug)
                            System.out.println("matched disease: " + diseaseData[a][0]);
                        break;
                    }
                        /*if (terms[1].length() > 1 && diseaseData[a][0].equals(terms[1].substring(0, 2))) {
                            System.out.println("matched disease: " + diseaseData[a][0]);
                            System.out.println("substring of: " + terms[1]);
                            break;
                        }*/
                }

                for (int b = 0; b < sampleData.length; b++) {
                        /*if (sampleData[b][0].equals(terms[2])) {
                            System.out.println("matched sample: " + sampleData[b][0]);
                            break;
                        }*/
                    if (terms[3].length() > 1 && sampleData[b][0].equals(terms[3].substring(0, 2))) {
                        if (debug) {
                            System.out.println("matched sample: " + sampleData[b][0] + "\t" + sampleData[b][1]);
                            System.out.println("substring of: " + terms[3]);
                        }
                        break;
                    }
                }

                    /*for (int c = 0; c < tissueData.length; c++) {
                        if (tissueData[c][0].equals(terms[j])) {
                            System.out.println("matched tissue: " + tissueData[c][0] + "\t" +
                                    tissueData[c][1] + "\t" + tissueData[c][2]);
                            break;
                        }
                        if (terms[j].length() > 1 && tissueData[c][0].equals(terms[j].substring(0, 2))) {
                            System.out.println("matched tissue: " + tissueData[c][0]);
                            System.out.println("substring of: " + terms[j]);
                            break;
                        }
                    }
                    */
                for (int c = 0; c < RSD_tissueData.length; c++) {
                    if (RSD_tissueData[c][0].equals(terms[1])) {
                        //if (debug)
                        System.out.println("matched RDB tissue: " + RSD_tissueData[c][0] + "\t" + RSD_tissueData[c][2]);
                        outstr[i][1] = RSD_tissueData[c][2];
                        break;
                    }
                       /* if (terms[1].length() > 1 && RSD_tissueData[c][0].equals(terms[1].substring(0, 2))) {
                            System.out.println("matched RDB tissue: " + RSD_tissueData[c][0] + "\t" +
                                    RSD_tissueData[c][1] + "\t" + RSD_tissueData[c][2]);
                            System.out.println("substring of: " + terms[1]);
                            break;
                        }*/
                }
                //}
            } else {
                String repl = samp_labels[i].replace(".", "-");

                //System.out.println(samp_labels[i]+"\t"+repl);
                //System.out.println("_"+GTExdata[1][0]+"_");

                for (int a = 0; a < GTExdata.length; a++) {
                    if (GTExdata[a][0].equals(repl)) {
                        //if (debug)
                        outstr[i][1] = GTExdata[a][6];
                        System.out.println("GTEx " + GTExdata[a][0] + "\t" + GTExdata[a][6]);
                        break;
                    }
                }

            }
        }


        TabFile.write(outstr, "RNAseqDB_sample_terms.txt");
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 3) {
            RNAseqDBlabels rm = new RNAseqDBlabels(args);
        } else {
            System.out.println("syntax: java DataMining.util.RNAseqDBlabels\n" +
                    "<data columns labels>\n" +
                    "<TCGA_metadata dir>\n" +
                    "<GTeX metadata>"
            );
        }
    }
}
