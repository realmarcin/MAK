package DataMining.eval;

import util.MoreArray;
import util.TextFile;

import java.util.ArrayList;

/**
 * Reformats SAMBA bicluster output (from Expander) to BiCat format.
 * <p/>
 * User: marcin
 * Date: Nov 3, 2009
 * Time: 2:53:04 PM
 */
public class SAMBAReformat {


    /**
     * @param args
     */
    public SAMBAReformat(String[] args) {
        ArrayList pass = TextFile.readtoList(args[0]);
        String cur = (String) pass.get(0);
        while (!cur.startsWith("[Bicd]")) {
            pass.remove(0);
            cur = (String) pass.get(0);
        }
        pass.remove(0);
        ArrayList out = new ArrayList();

        cur = (String) pass.get(0);
        //System.out.println(cur);
        String[] line = cur.split("\t");
        int curblock = Integer.parseInt(line[0]);
        int blockcounter = curblock;
        while (pass.size() > 0) {
            ArrayList genes = new ArrayList();
            ArrayList exps = new ArrayList();
            while (curblock == blockcounter) {
                line = cur.split("\t");
                //System.out.println(curblock+"\t"+ cur);
                curblock = Integer.parseInt(line[0]);
                int geneorexp = Integer.parseInt(line[1]);

                if (geneorexp == 1)
                    genes.add(line[2]);
                else if (geneorexp == 0)
                    exps.add(line[2]);
                pass.remove(0);
                if (pass.size() > 0) {
                    cur = (String) pass.get(0);
                    line = cur.split("\t");
                    curblock = Integer.parseInt(line[0]);
                    //System.out.println(blockcounter+"\t"+curblock+"\t"+ cur);
                } else
                    break;
            }

            out.add("" + genes.size() + " " + exps.size());
            out.add(MoreArray.arrayListtoString(genes, " "));
            out.add(MoreArray.arrayListtoString(exps, " "));

            if (pass.size() == 0)
                break;
            else {
                blockcounter = curblock;
            }
        }

        System.out.println("blocks " + out.size() / 3.0);
        TextFile.write(out, args[0] + ".reformat");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            SAMBAReformat arg = new SAMBAReformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.SAMBAReformat\n" +
                    "<SAMBA output>");
        }
    }
}
