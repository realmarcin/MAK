package DataMining;

import mathy.Matrix;
import util.MoreArray;

import java.util.Random;

/**
 * User: marcin
 * Date: Nov 2, 2010
 * Time: 11:29:03 AM]
 */
public class SampleBlocks {


    int datagenemax, dataexpmax;
    double percent_allowed_missing_in_block, percent_allowed_missing_genes, percent_allowed_missing_exp;
    Random rand;

    double[][] data;

    boolean debug2 = false;

    /**
     *
     */
    public SampleBlocks(double percent_allowed_missing_in_block, double percent_allowed_missing_genes,
                        double percent_allowed_missing_exp, int datagenemax, int dataexpmax, Random rand, double[][] data, boolean d) {

        this.percent_allowed_missing_in_block = percent_allowed_missing_in_block;
        this.percent_allowed_missing_genes = percent_allowed_missing_genes;
        this.percent_allowed_missing_exp = percent_allowed_missing_exp;
        this.datagenemax = datagenemax;
        this.dataexpmax = dataexpmax;
        this.rand = rand;
        this.data = data;

        debug2 = d;

        System.out.println("SampleBlocks debug2 " + debug2);
    }

    /**
     * @return
     */
    public int[][] sampleRetInt(int g, int e, boolean debug) {

        boolean origdebug = debug2;
        if(debug)
            debug2 = true;

        //System.out.println("debug2 "+debug2);

        String prefix = "" + g + "_" + e + "\t";
        ValueBlock VBPInitial = new ValueBlock(g, e);
        VBPInitial = BlockMethods.createRandomBlock(VBPInitial, datagenemax, dataexpmax, rand, false);
        //if (expr_data.data == null)
        //    System.out.println("sampleOneBlock data is null");
        //System.out.println("sampleOneBlock "+VBPInitial.blockId());
        VBPInitial.setDataAndMean(data);
        if (debug2) {
            System.out.println("sampleOneBlock random block");
            MoreArray.printArray(VBPInitial.exp_data, MoreArray.toStringArray(VBPInitial.exps), MoreArray.toStringArray(VBPInitial.genes));
        }
        boolean aboveNaNThreshold = VBPInitial.isAboveNaN(percent_allowed_missing_in_block);
        boolean aboveNaNThresholdGene = false;
        boolean aboveNaNThresholdExp = false;
        //System.out.println("total " + aboveNaNThreshold);
        while (!aboveNaNThreshold) {
            if (!aboveNaNThreshold) {
                if (debug2) {
                    System.out.println("sampleOneBlock failed total, select random block");
                    MoreArray.printArray(VBPInitial.exp_data);
                }
                VBPInitial = new ValueBlock(BlockMethods.createRandomBlock(VBPInitial, datagenemax,
                        dataexpmax, rand, false));
                VBPInitial.setDataAndMean(data);
                aboveNaNThreshold = VBPInitial.isAboveNaN(percent_allowed_missing_in_block);
                //int[][] coords = {VBPInitial.genes, VBPInitial.exps};
                //System.out.println("createRandomBlock " + aboveNaNThreshold + "\t" + BlockMethods.IcJctoijID(coords));
            }
            //System.out.print("t");
        }

        int i = 0;
        SampleGenes sg = new SampleGenes(VBPInitial, datagenemax);
        int countbadg = 0;
        while (i < VBPInitial.genes.length && countbadg == 0) {
            int triedg = 0;
            aboveNaNThresholdGene = VBPInitial.isAboveGeneNaN(i, percent_allowed_missing_genes);
            if (!aboveNaNThresholdGene) {
                boolean passed = false;
                while (!passed) {
                    boolean added = sg.replaceGeneWithRand(i, rand);
                    if (!added) {
                        countbadg++;
                        passed = false;
                        break;
                    } else {
                        triedg++;
                        sg.sampvb.setDataAndMean(data);
                        passed = sg.sampvb.isAboveGeneNaN(i, percent_allowed_missing_genes);
                    }
                }

                if (passed) {
                    VBPInitial = new ValueBlock(sg.sampvb);
                    aboveNaNThresholdGene = VBPInitial.isAboveGeneNaN(i, percent_allowed_missing_genes);
                } else {
                    System.out.println(prefix + "gene failed " + i + "\t" + passed + "\t" + countbadg);
                }
            }
            i++;
        }

        //test genes
        /*for (int a = 0; a < VBPInitial.genes.length; a++) {
            boolean passed = VBPInitial.isAboveGeneNaN(a, percent_allowed_missing_genes);
            if (!passed) {
                System.out.println(prefix + "testing failed " + a + "\taboveNaNThresholdGene " + aboveNaNThresholdGene);
                aboveNaNThresholdGene = false;
                MoreArray.printArray(VBPInitial.exp_data[a]);
            }
        }
        */

        if (aboveNaNThresholdGene) {
            int j = 0;
            SampleExperiments se = new SampleExperiments(VBPInitial, dataexpmax);
            int countbade = 0;
            while (j < VBPInitial.exps.length && countbade == 0) {
                int triede = 0;
                aboveNaNThresholdExp = VBPInitial.isAboveExpNaN(j, percent_allowed_missing_exp);
                if (!aboveNaNThresholdExp) {
                    boolean passed = false;
                    while (!passed) {
                        boolean added = se.replaceExpWithRand(j, rand);
                        if (!added) {
                            countbade++;
                            passed = false;
                            break;
                        } else {
                            triede++;
                            se.sampvb.setDataAndMean(data);
                            passed = se.sampvb.isAboveExpNaN(j, percent_allowed_missing_exp);
                        }
                    }

                    if (passed) {
                        VBPInitial = new ValueBlock(se.sampvb);
                        aboveNaNThresholdExp = VBPInitial.isAboveExpNaN(j, percent_allowed_missing_exp);
                    } else {
                        System.out.println(prefix + "gene failed " + j + "\t" + passed + "\t" + countbade);
                    }
                }
                j++;
            }

            //test exps
            /*for (int a = 0; a < VBPInitial.exps.length; a++) {
                boolean passed = VBPInitial.isAboveExpNaN(a, percent_allowed_missing_exp);
                if (!passed) {
                    System.out.println(prefix + "testing failed " + a + "\taboveNaNThresholdExp " + aboveNaNThresholdExp);
                    aboveNaNThresholdExp = false;
                    MoreArray.printArray(Matrix.extractColumn(VBPInitial.exp_data, a + 1));
                }
            }*/
        }

        if (aboveNaNThreshold && aboveNaNThresholdGene && aboveNaNThresholdExp) {
            int[][] ret = {VBPInitial.genes, VBPInitial.exps};
            return ret;
        }
        System.out.println("SampleBlocks.sample failed g " + g + "\te " + e + "\t" +
                aboveNaNThreshold + "\t" + aboveNaNThresholdGene + "\t" + aboveNaNThresholdExp);

        debug2 = origdebug;
        return null;
    }

    /**
     * @return
     */
    public ValueBlock sample(int g, int e) {

        String prefix = "" + g + "_" + e + "\t";
        ValueBlock VBPInitial = new ValueBlock(g, e);
        VBPInitial = BlockMethods.createRandomBlock(VBPInitial, datagenemax, dataexpmax, rand, false);
        //if (expr_data.data == null)
        //    System.out.println("sampleOneBlock data is null");
        //System.out.println("sampleOneBlock "+VBPInitial.blockId());
        VBPInitial.setDataAndMean(data);
        if (debug2) {
            System.out.println("sampleOneBlock random block");
            MoreArray.printArray(VBPInitial.exp_data, MoreArray.toStringArray(VBPInitial.exps), MoreArray.toStringArray(VBPInitial.genes));
        }
        boolean aboveNaNThreshold = VBPInitial.isAboveNaN(percent_allowed_missing_in_block);
        boolean aboveNaNThresholdGene = false;
        boolean aboveNaNThresholdExp = false;
        //System.out.println("total " + aboveNaNThreshold);
        while (!aboveNaNThreshold) {
            if (!aboveNaNThreshold) {
                if (debug2) {
                    System.out.println("sampleOneBlock failed total, select random block");
                    MoreArray.printArray(VBPInitial.exp_data);
                }
                VBPInitial = new ValueBlock(BlockMethods.createRandomBlock(VBPInitial, datagenemax,
                        dataexpmax, rand, false));
                VBPInitial.setDataAndMean(data);
                aboveNaNThreshold = VBPInitial.isAboveNaN(percent_allowed_missing_in_block);
                //int[][] coords = {VBPInitial.genes, VBPInitial.exps};
                //System.out.println("createRandomBlock " + aboveNaNThreshold + "\t" + BlockMethods.IcJctoijID(coords));
            }
            //System.out.print("t");
        }

        int i = 0;
        SampleGenes sg = new SampleGenes(VBPInitial, datagenemax);
        int countbadg = 0;
        while (i < VBPInitial.genes.length && countbadg == 0) {
            int triedg = 0;
            aboveNaNThresholdGene = VBPInitial.isAboveGeneNaN(i, percent_allowed_missing_genes);
            if (!aboveNaNThresholdGene) {
                boolean passed = false;
                while (!passed) {
                    boolean added = sg.replaceGeneWithRand(i, rand);
                    if (!added) {
                        countbadg++;
                        passed = false;
                        break;
                    } else {
                        triedg++;
                        sg.sampvb.setDataAndMean(data);
                        passed = sg.sampvb.isAboveGeneNaN(i, percent_allowed_missing_genes);
                    }
                }

                if (passed) {
                    VBPInitial = new ValueBlock(sg.sampvb);
                    aboveNaNThresholdGene = VBPInitial.isAboveGeneNaN(i, percent_allowed_missing_genes);
                } else {
                    System.out.println(prefix + "gene failed " + i + "\t" + passed + "\t" + countbadg);
                }
            }
            i++;
        }

        //test genes
        for (int a = 0; a < VBPInitial.genes.length; a++) {
            boolean passed = VBPInitial.isAboveGeneNaN(a, percent_allowed_missing_genes);
            if (!passed) {
                System.out.println(prefix + "testing failed " + a + "\taboveNaNThresholdGene " + aboveNaNThresholdGene);
                aboveNaNThresholdGene = false;
                MoreArray.printArray(VBPInitial.exp_data[a]);
            }
        }

        if (aboveNaNThresholdGene) {
            int j = 0;
            SampleExperiments se = new SampleExperiments(VBPInitial, dataexpmax);
            int countbade = 0;
            while (j < VBPInitial.exps.length && countbade == 0) {
                int triede = 0;
                aboveNaNThresholdExp = VBPInitial.isAboveExpNaN(j, percent_allowed_missing_exp);
                if (!aboveNaNThresholdExp) {
                    boolean passed = false;
                    while (!passed) {
                        boolean added = se.replaceExpWithRand(j, rand);
                        if (!added) {
                            countbade++;
                            passed = false;
                            break;
                        } else {
                            triede++;
                            se.sampvb.setDataAndMean(data);
                            passed = se.sampvb.isAboveExpNaN(j, percent_allowed_missing_exp);
                        }
                    }

                    if (passed) {
                        VBPInitial = new ValueBlock(se.sampvb);
                        aboveNaNThresholdExp = VBPInitial.isAboveExpNaN(j, percent_allowed_missing_exp);
                    } else {
                        System.out.println(prefix + "gene failed " + j + "\t" + passed + "\t" + countbade);
                    }
                }
                j++;
            }

            //test exps
            for (int a = 0; a < VBPInitial.exps.length; a++) {
                boolean passed = VBPInitial.isAboveExpNaN(a, percent_allowed_missing_exp);
                if (!passed) {
                    System.out.println(prefix + "testing failed " + a + "\taboveNaNThresholdExp " + aboveNaNThresholdExp);
                    aboveNaNThresholdExp = false;
                    MoreArray.printArray(Matrix.extractColumn(VBPInitial.exp_data, a + 1));
                }
            }
        }

        if (aboveNaNThreshold && aboveNaNThresholdGene && aboveNaNThresholdExp) {
            return VBPInitial;
        }
        System.out.println("SampleBlocks.sample failed g " + g + "\te " + e + "\t" +
                aboveNaNThreshold + "\t" + aboveNaNThresholdGene + "\t" + aboveNaNThresholdExp);
        return null;
    }

    /**
     * @return
     */
    public ValueBlockPre samplePre(int g, int e) {

        String prefix = "" + g + "_" + e + "\t";
        ValueBlockPre VBPInitial = new ValueBlockPre(g, e);
        VBPInitial = BlockMethods.createRandomBlock(VBPInitial, datagenemax, dataexpmax, rand, false);
        //if (expr_data.data == null)
        //    System.out.println("sampleOneBlock data is null");
        //System.out.println("sampleOneBlock "+VBPInitial.blockId());
        VBPInitial.setDataAndMean(data);
        if (debug2) {
            System.out.println("sampleOneBlock random block");
            MoreArray.printArray(VBPInitial.exp_data, MoreArray.toStringArray(VBPInitial.exps), MoreArray.toStringArray(VBPInitial.genes));
        }
        boolean aboveNaNThreshold = VBPInitial.isAboveNaN(percent_allowed_missing_in_block);
        boolean aboveNaNThresholdGene = false;
        boolean aboveNaNThresholdExp = false;
        //System.out.println("total " + aboveNaNThreshold);
        while (!aboveNaNThreshold) {
            if (!aboveNaNThreshold) {
                if (debug2) {
                    System.out.println("sampleOneBlock failed total, select random block");
                    MoreArray.printArray(VBPInitial.exp_data);
                }
                VBPInitial = new ValueBlockPre(BlockMethods.createRandomBlock(VBPInitial, datagenemax,
                        dataexpmax, rand, false));
                VBPInitial.setDataAndMean(data);
                aboveNaNThreshold = VBPInitial.isAboveNaN(percent_allowed_missing_in_block);
                //int[][] coords = {VBPInitial.genes, VBPInitial.exps};
                //System.out.println("createRandomBlock " + aboveNaNThreshold + "\t" + BlockMethods.IcJctoijID(coords));
            }
            //System.out.print("t");
        }

        int i = 0;
        SampleGenes sg = new SampleGenes(VBPInitial, datagenemax);
        int countbadg = 0;
        while (i < VBPInitial.genes.length && countbadg == 0) {
            int triedg = 0;
            aboveNaNThresholdGene = VBPInitial.isAboveGeneNaN(i, percent_allowed_missing_genes);
            if (!aboveNaNThresholdGene) {
                boolean passed = false;
                while (!passed) {
                    boolean added = sg.replaceGeneWithRand(i, rand);
                    if (!added) {
                        countbadg++;
                        passed = false;
                        break;
                    } else {
                        triedg++;
                        sg.sampvb.setDataAndMean(data);
                        passed = sg.sampvb.isAboveGeneNaN(i, percent_allowed_missing_genes);
                    }
                }

                if (passed) {
                    VBPInitial = new ValueBlockPre(sg.sampvb);
                    aboveNaNThresholdGene = VBPInitial.isAboveGeneNaN(i, percent_allowed_missing_genes);
                } else {
                    System.out.println(prefix + "gene failed " + i + "\t" + passed + "\t" + countbadg);
                }
            }
            i++;
        }

        //test genes
        for (int a = 0; a < VBPInitial.genes.length; a++) {
            boolean passed = VBPInitial.isAboveGeneNaN(a, percent_allowed_missing_genes);
            if (!passed) {
                System.out.println(prefix + "testing failed " + a + "\taboveNaNThresholdGene " + aboveNaNThresholdGene);
                aboveNaNThresholdGene = false;
                MoreArray.printArray(VBPInitial.exp_data[a]);
            }
        }

        if (aboveNaNThresholdGene) {
            int j = 0;
            SampleExperiments se = new SampleExperiments(VBPInitial, dataexpmax);
            int countbade = 0;
            while (j < VBPInitial.exps.length && countbade == 0) {
                int triede = 0;
                aboveNaNThresholdExp = VBPInitial.isAboveExpNaN(j, percent_allowed_missing_exp);
                if (!aboveNaNThresholdExp) {
                    boolean passed = false;
                    while (!passed) {
                        boolean added = se.replaceExpWithRand(j, rand);
                        if (!added) {
                            countbade++;
                            passed = false;
                            break;
                        } else {
                            triede++;
                            se.sampvb.setDataAndMean(data);
                            passed = se.sampvb.isAboveExpNaN(j, percent_allowed_missing_exp);
                        }
                    }

                    if (passed) {
                        VBPInitial = new ValueBlockPre(se.sampvb);
                        aboveNaNThresholdExp = VBPInitial.isAboveExpNaN(j, percent_allowed_missing_exp);
                    } else {
                        System.out.println(prefix + "gene failed " + j + "\t" + passed + "\t" + countbade);
                    }
                }
                j++;
            }

            //test exps
            for (int a = 0; a < VBPInitial.exps.length; a++) {
                boolean passed = VBPInitial.isAboveExpNaN(a, percent_allowed_missing_exp);
                if (!passed) {
                    System.out.println(prefix + "testing failed " + a + "\taboveNaNThresholdExp " + aboveNaNThresholdExp);
                    aboveNaNThresholdExp = false;
                    MoreArray.printArray(Matrix.extractColumn(VBPInitial.exp_data, a + 1));
                }
            }
        }

        if (aboveNaNThreshold && aboveNaNThresholdGene && aboveNaNThresholdExp) {
            return VBPInitial;
        }
        System.out.println("SampleBlocks.sample failed g " + g + "\te " + e + "\t" +
                aboveNaNThreshold + "\t" + aboveNaNThresholdGene + "\t" + aboveNaNThresholdExp);
        return null;
    }
}
