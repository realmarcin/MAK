package DataMining.util;

import DataMining.MINER_STATIC;
import util.MoreArray;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 5/4/15
 * Time: 12:57 PM
 */
public class TestCritAssign {

    /**
     * @param args
     */
    public TestCritAssign(String[] args) {


        //System.out.println("MSEtotalCrit " + MINER_STATIC.CRIT_LABELS.length);

        //int[] get = StringUtil.occurIndexButNot(MINER_STATIC.CRIT_LABELS, "MSE", MINER_STATIC.notnonullMSERMSEC);


        // MoreArray.printArray(MINER_STATIC.MSEtotalCrit);

        //System.out.println("get");
        //MoreArray.printArray(get);


        /*System.out.println("MSEtotalCrit MINER_STATIC test");
                int[] test = StringUtil.occurIndexButNot(MINER_STATIC.CRIT_LABELS, "MSE", MINER_STATIC.notnonullMSERMSEC);
                MoreArray.printArray(test);

        System.out.println("MSEtotalCrit MINER_STATIC testadd");
        int[] testadd = stat.add(
                        StringUtil.occurIndexButNot(MINER_STATIC.CRIT_LABELS, "MSE", MINER_STATIC.notnonullMSERMSEC), 1);
        MoreArray.printArray(testadd);*/


        //System.out.println("MSEtotalCrit MINER_STATIC");
        //MoreArray.printArray(MINER_STATIC.MSEtotalCrit);
        //int[] subtr = mathy.stat.subtract(MINER_STATIC.MSEtotalCrit, 1);
        //System.out.println("MSEtotalCrit subtr");
        //MoreArray.printArray(subtr);
        String[] labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSEtotalCrit, 1), MINER_STATIC.CRIT_LABELS);
        System.out.println("MSEtotalCrit labels");
        MoreArray.printArray(labels);

        //public final static int[] MSEtotalCrit = stat.add(
        //                StringUtil.occurIndexButNot(CRIT_LABELS, "MSE", notnonullMSERMSEC), 1);  //MoreArray.add(//24 - 1),


        System.out.println("MSERCrit");
        //MoreArray.printArray(MINER_STATIC.MSERCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSERCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MSECCrit");
        //MoreArray.printArray(MINER_STATIC.MSECCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSECCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("KENDALLtotalCrit");
        //MoreArray.printArray(MINER_STATIC.KENDALLtotalCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.KENDALLtotalCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("KENDALLRnonullCrit");
        //MoreArray.printArray(MINER_STATIC.KENDALLRnonullCrit);
        MoreArray.printArray(MINER_STATIC.KENDALLRnonullCrit);

        System.out.println("KENDALLRCrit");
        //MoreArray.printArray(MINER_STATIC.KENDALLRCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.KENDALLRCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("KENDALLCnonullCrit");
        //MoreArray.printArray(MINER_STATIC.KENDALLCnonullCrit);
        MoreArray.printArray(MINER_STATIC.KENDALLCnonullCrit);

        System.out.println("KENDALLCCrit");
        //MoreArray.printArray(MINER_STATIC.KENDALLCCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.KENDALLCCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MADRCrit");
        //MoreArray.printArray(MINER_STATIC.MADRCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MADRCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MSECritAll");
        //MoreArray.printArray(MINER_STATIC.MSECritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSECritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MSERCritAll");
        //MoreArray.printArray(MINER_STATIC.MSERCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSERCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MSECCritAll");
        //MoreArray.printArray(MINER_STATIC.MSECCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSECCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("KENDALLCritAll");
        //MoreArray.printArray(MINER_STATIC.KENDALLCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.KENDALLCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MADRCritAll");
        //MoreArray.printArray(MINER_STATIC.MADRCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MADRCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MSERCritnointer");
        //MoreArray.printArray(MINER_STATIC.MSERCritnointer);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSERCritnointer, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MSECCritnointer");
        //MoreArray.printArray(MINER_STATIC.MSECCritnointer);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSECCritnointer, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("KENDALLCritnointer");
        //MoreArray.printArray(MINER_STATIC.KENDALLCritnointer);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.KENDALLCritnointer, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("KENDALLRCritnointer");
        //MoreArray.printArray(MINER_STATIC.KENDALLRCritnointer);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.KENDALLRCritnointer, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("KENDALLCCritnointer");
        //MoreArray.printArray(MINER_STATIC.KENDALLCCritnointer);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.KENDALLCCritnointer, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MSECritnointer");
        //MoreArray.printArray(MINER_STATIC.MSECritnointer);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MSECritnointer, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MEANCrit");
        //MoreArray.printArray(MINER_STATIC.MEANCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MEANCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("MEANCritAll");
        //MoreArray.printArray(MINER_STATIC.MEANCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.MEANCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("nullCritnointeract");
        //MoreArray.printArray(MINER_STATIC.nullCritnointeract);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.nullCritnointeract, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("nonullCrit");
        //MoreArray.printArray(MINER_STATIC.nonullCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.nonullCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("interactCrit");
        //MoreArray.printArray(MINER_STATIC.interactCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.interactCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("FEATURECrit");
        //MoreArray.printArray(MINER_STATIC.FEATURECrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.FEATURECrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("LARSCritAll");
        //MoreArray.printArray(MINER_STATIC.LARSCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.LARSCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("LARSRECrit");
        //MoreArray.printArray(MINER_STATIC.LARSRECrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.LARSRECrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("LARSCECrit");
        //MoreArray.printArray(MINER_STATIC.LARSCECrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.LARSCECrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("FEMCritAll");
        //MoreArray.printArray(MINER_STATIC.FEMCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.FEMCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("FEMRCrit");
        //MoreArray.printArray(MINER_STATIC.FEMRCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.FEMRCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("FEMCCrit");
        //MoreArray.printArray(MINER_STATIC.FEMCCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.FEMCCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("CORtotalCrit");
        //MoreArray.printArray(MINER_STATIC.CORtotalCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.CORtotalCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("CORRCrit");
        //MoreArray.printArray(MINER_STATIC.CORRCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.CORRCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("CORCCrit");
        //MoreArray.printArray(MINER_STATIC.CORCCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.CORCCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("EUCtotalCrit");
        //MoreArray.printArray(MINER_STATIC.EUCtotalCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.EUCtotalCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("EUCRCrit");
        //MoreArray.printArray(MINER_STATIC.EUCRCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.EUCRCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("EUCCCrit");
        //MoreArray.printArray(MINER_STATIC.EUCCCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.EUCCCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("totalCrit");
        //MoreArray.printArray(MINER_STATIC.totalCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.totalCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("CritAll");
        //MoreArray.printArray(MINER_STATIC.CritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.CritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("rowCrit");
        //MoreArray.printArray(MINER_STATIC.rowCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.rowCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("rowCritAll");
        //MoreArray.printArray(MINER_STATIC.rowCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.rowCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("colCrit");
        //MoreArray.printArray(MINER_STATIC.colCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.colCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("colCritAll");
        //MoreArray.printArray(MINER_STATIC.colCritAll);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.colCritAll, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

        System.out.println("isNoninvertCrit");
        //MoreArray.printArray(MINER_STATIC.isNoninvertCrit);
        labels = MoreArray.getIndexed(mathy.stat.subtract(MINER_STATIC.isNoninvertCrit, 1), MINER_STATIC.CRIT_LABELS);
        MoreArray.printArray(labels);

    }


    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 0) {
            TestCritAssign rm = new TestCritAssign(args);
        } else {
            System.out.println("syntax: java DataMining.util.TestCritAssign\n"
            );
        }
    }
}
