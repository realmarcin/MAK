package DataMining.func;

import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Apr 7, 2011
 * Time: 2:27:58 PM
 */
public class ReformatYeastOrthogroups {


    /**
     * @param args
     */
    public ReformatYeastOrthogroups(String[] args) {

        String[] orthdata = TextFile.readtoArray(args[0]);
        String[][] featdata = TabFile.readtoArray(args[1]);

        ArrayList data = new ArrayList();
        for (int i = 0; i < orthdata.length; i++) {

            if (orthdata[i].indexOf(">") == 0) {
                i += 1;
                String orthlabel = orthdata[i].split("\t")[3];
                System.out.println(i + "\t" + orthlabel);
                for (int j = 0; j < featdata.length; j++) {
                    if (orthlabel.equals("cer|" + featdata[j][3])) {
                        String s = StringUtil.replace(orthdata[i + 2], "Counts: ", "");
                        s = StringUtil.replace(s, " ", "\t");
                        System.out.println("found " + orthlabel);
                        data.add(featdata[j][3] + "\t" + s);
                        break;
                    }
                }
            }

        }

        TextFile.write(data, args[0] + "_scerevisae.txt");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            ReformatYeastOrthogroups rm = new ReformatYeastOrthogroups(args);
        } else {
            System.out.println("syntax: java DataMining.func.ReformatYeastOrthogroups\n" +
                    "<orthogroup file>\n" +
                    "<SGD feature file>"
            );
        }
    }
}
