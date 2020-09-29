package DataMining.func;

import util.MoreArray;
import util.TabFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Apr 7, 2011
 * Time: 1:13:35 PM
 */
public class RelabelSGDPathways {

    /**
     * @param args
     */
    public RelabelSGDPathways(String[] args) {

        String[][] pathdata = TabFile.readtoArray(args[0]);
        String[][] featdata = TabFile.readtoArray(args[1]);

        for (int i = 0; i < pathdata.length; i++) {
            //MoreArray.printArray(pathdata[i]);
            System.out.println("pathdata " + MoreArray.toString(pathdata[i], ","));
            if (!pathdata[i][3].equals("NA")) {
                for (int j = 0; j < featdata.length; j++) {
                    //MoreArray.printArray(featdata[j]);
                    try {
                        if (featdata[j][4] != null && pathdata[i][3].equals(featdata[j][4])) {
                            //System.out.println("pathdata " + MoreArray.toString(pathdata[i], ","));
                            System.out.println("found " + pathdata[i][3] + "\t" + featdata[j][3]);
                            pathdata[i][3] = featdata[j][3];
                            break;
                        }
                        if (featdata[j][5] != null && pathdata[i][3].equals(featdata[j][5])) {
                            //System.out.println("pathdata " + MoreArray.toString(pathdata[i], ","));
                            System.out.println("found " + pathdata[i][3] + "\t" + featdata[j][3]);
                            pathdata[i][3] = featdata[j][3];
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR pathdata " + MoreArray.toString(pathdata[i], ","));
                        System.out.println("ERROR featdata " + MoreArray.toString(featdata[j], ","));
                        e.printStackTrace();
                    }
                }
            }
        }

        TabFile.write(pathdata, args[0] + "_relabel.txt");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            RelabelSGDPathways rm = new RelabelSGDPathways(args);
        } else {
            System.out.println("syntax: java DataMining.func.RelabelSGDPathways\n" +
                    "<SGD pathway file>\n" +
                    "<SGD features file>"
            );
        }
    }

}
