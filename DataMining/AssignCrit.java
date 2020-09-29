package DataMining;

import util.MoreArray;
import util.StringUtil;

/**
 * Class to assign the criterion index and TF and mean booleans based on a criterion label string.
 */
public class AssignCrit {

    public int[] CRITindex;
    public int[] CRITmean;
    public int[] CRITTF;
    public int[] CRITFEAT;
    public int len;
    public String[] labels;


    /**
     * @param l
     */
    public AssignCrit(String l) {
        labels = new String[1];
        labels[0] = l;
        len = labels.length;
        assignCrit();
    }

    /**
     * @param l
     */
    public AssignCrit(String[] l) {
        labels = l;
        len = labels.length;
        assignCrit();
    }

    /**
     *
     */
    void assignCrit() {
        CRITindex = MoreArray.initArray(len, -1);//new int[len];
        CRITTF = MoreArray.initArray(len, -1);
        CRITFEAT = MoreArray.initArray(len, -1);
        CRITmean = MoreArray.initArray(len, -1);

        for (int i = 0; i < len; i++) {
            String cur = labels[i];
            System.out.println("assignCrit assigning " + labels[i]);
            if (cur.equalsIgnoreCase("MAXTF")) {
                CRITindex[i] = -10;
                CRITTF[i] = 1;
                CRITmean[i] = 0;
            } else if (cur.equalsIgnoreCase("feat")) {
                CRITindex[i] = StringUtil.getFirstEqualsIgnoreCaseIndex(MINER_STATIC.CRIT_LABELS, "feat") + 1;//-10;
                CRITFEAT[i] = 1;
                CRITmean[i] = 0;
            } else {
                if (labels[i].indexOf("MAXTF") != -1 || labels[i].indexOf("maxtf") != -1) {
                    CRITTF[i] = 1;
                    cur = StringUtil.replace(cur, "_MAXTF", "");
                    cur = StringUtil.replace(cur, "MAXTF", "");
                }
                if (labels[i].indexOf("feat") != -1 || labels[i].indexOf("FEAT") != -1) {
                    CRITFEAT[i] = 1;
                    //cur = StringUtil.replace(cur, "_feat", "");
                    //cur = StringUtil.replace(cur, "feat", "");
                }

                //cases if only mean criterion
                if (labels[i].equalsIgnoreCase("MEDRMEAN")) {
                    CRITmean[i] = 1;
                } else if (labels[i].equalsIgnoreCase("MEDCMEAN")) {
                    CRITmean[i] = 1;
                } else if (labels[i].equalsIgnoreCase("MEAN")) {
                    CRITmean[i] = 1;
                }
                //cases if mean criterion combined with others 
                else if (labels[i].indexOf("_MEDRMEAN") != -1 || labels[i].indexOf("MEDRMEAN_") != -1 || labels[i].indexOf("_medrmean") != -1 || labels[i].indexOf("medrmean_") != -1) {
                    CRITmean[i] = 1;
                    cur = StringUtil.replace(cur, "_MEDRMEAN", "");
                    cur = StringUtil.replace(cur, "MEDRMEAN", "");
                } else if (labels[i].indexOf("_MEDCMEAN") != -1 || labels[i].indexOf("_MEDCMEAN") != -1 || labels[i].indexOf("_medcmean") != -1 || labels[i].indexOf("_medcean") != -1) {
                    CRITmean[i] = 1;
                    cur = StringUtil.replace(cur, "_MEDCMEAN", "");
                    cur = StringUtil.replace(cur, "MEDCMEAN", "");
                } else if (labels[i].indexOf("_MEAN") != -1 || labels[i].indexOf("_mean") != -1) {
                    CRITmean[i] = 1;
                    cur = StringUtil.replace(cur, "_MEAN", "");
                    cur = StringUtil.replace(cur, "MEAN", "");
                }
                System.out.println("assignCrit testing " + cur);
                CRITindex[i] = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, cur) + 1;
                System.out.println("assignCrit " + i + "\t" + CRITindex[i]);
                labels[i] = cur;
            }
        }
        System.out.println("assignCrit labels");
        MoreArray.printArray(labels);
        System.out.println("assignCrit CRIT_INDEX");
        MoreArray.printArray(CRITindex);
        System.out.println("assignCrit CRITmean");
        MoreArray.printArray(CRITmean);
    }
}