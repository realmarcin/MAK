package DataMining.func;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.io.File;
import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 6/12/13
 * Time: 12:18 PM
 */
public class GetYeastAnnot {


    /**
     * @param args
     */
    public GetYeastAnnot(String[] args) {

        String[][] tabdata = TabFile.readtoArray(args[1]);
        String[] geneids = MoreArray.extractColumnStr(tabdata, 4);
        String[] genenames = MoreArray.extractColumnStr(tabdata, 5);
        String[] def = MoreArray.extractColumnStr(tabdata, 16);

        //MoreArray.printArray(tabdata[0]);
        //MoreArray.printArray(def);
        //System.out.println(geneids[0] + "\t" + def[0]);

        File dir = new File(args[0]);
        String[] list = dir.list();

        for (int i = 0; i < list.length; i++) {
            String f1 = args[0] + "/" + list[i];
            if (f1.endsWith(".txt")) {
                System.out.println("reading genes " + f1);
                String[][] genes = TabFile.readtoArray(f1);
                MoreArray.printArray(genes[0]);
                String[][] out = new String[genes.length][3];
                for (int j = 0; j < genes.length; j++) {
                    System.out.println(genes[j][0]);
                    out[j][0] = genes[j][0];
                    int index = StringUtil.getFirstEqualsIndex(geneids, out[j][0]);
                    if (index != -1) {
                        out[j][1] = genenames[index];
                        out[j][2] = def[index];
                    }
                }
                String f = args[0] + "/" + list[i] + "_SGDdef.txt";
                System.out.println("writing " + f);
                TabFile.write(out, f);
            }
        }
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 2) {
            GetYeastAnnot rm = new GetYeastAnnot(args);
        } else {
            System.out.println("syntax: java DataMining.func.GetYeastAnnot\n" +
                    "<dir of bicluster gene sets>\n" +
                    "<yeast genome feature SGD file>"
            );
        }
    }
}

