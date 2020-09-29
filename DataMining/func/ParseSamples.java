package DataMining.func;

import util.StringUtil;
import util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: marcin
 * Date: 11/1/18
 * Time: 9:12 AM
 */
public class ParseSamples {


    String header = "Sample Name\tFactor Value[limiting nutrient]\tOntology Term Accession Number\tFactor Value[rate]\tUnit\tOntology Term Accession Number";

    /**
     * @param args
     */
    public ParseSamples(String[] args) {

        String[] rawdata = TextFile.readtoArray(args[0]);

        HashMap words = new HashMap();
        HashMap sample_factor_index = new HashMap();

        for (int i = 1; i < rawdata.length; i++) {
            String[] parsed1 = rawdata[i].split("\t");
            parsed1[1] = StringUtil.replace(parsed1[1], "\"", "");
            System.out.println(parsed1[1]);
            String[] parsed = parsed1[1].split("\\.");
            System.out.println(parsed.length);
            for (int j = 0; j < parsed.length; j++) {
                //System.out.println(parsed[j]);

                //excluding integers
                if (!parsed[j].matches("-?\\d+")) {
                    Object o = words.get(parsed[j]);
                    if (o == null) {
                        words.put(parsed[j], 1);
                        //System.out.println("adding 1 " + parsed[j]);
                        sample_factor_index.put(parsed1[1], parsed[j]);
                    } else {
                        int cur = (Integer) o;
                        words.put(parsed[j], cur + 1);
                        //System.out.println("adding 1 " + parsed[j] + "\t" + cur);
                        sample_factor_index.put(parsed1[1], parsed[j]);
                    }
                }
            }
        }

       /* for (int i = 1; i < rawdata.length; i++) {
            String[] parsed1 = rawdata[i].split("\t");
            parsed1[1] = StringUtil.replace(parsed1[1], "\"", "");
            System.out.println(parsed1[1]);
            String[] parsed = data.split("(?<=\\G\\d+\\.\\d+)\\.");//parsed1[1].split("\\.");
            System.out.println(parsed.length);
            for (int j = 0; j < parsed.length; j++) {
                System.out.println(parsed[j]);
                Object o = words.get(parsed[j]);
                if (o == null) {
                    words.put(parsed[j], 1);
                    System.out.println("adding 2 " + parsed[j]);
                } else {
                    int cur = (Integer) o;
                    words.put(parsed[j], cur + 1);
                    System.out.println("adding 2 " + parsed[j] + "\t" + cur);
                }
            }
        }*/


        Iterator it = words.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if ((Integer) pair.getValue() > 1)
                System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }

        ArrayList out = new ArrayList();
        out.add(header);
        Iterator it2 = sample_factor_index.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pair = (Map.Entry) it2.next();
            System.out.println(pair.getKey() + "\t" + pair.getValue());
            try {
                int count = (Integer) words.get("" + pair.getValue());
                System.out.println(pair.getValue() + "\t" + count);
                if (count >= 5) {
                    out.add(pair.getKey() + "\t" + pair.getValue() + "\t\t\t\t");
                    System.out.println(pair.getKey() + " = " + pair.getValue());
                }
            } catch (Exception e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //it2.remove(); // avoids a ConcurrentModificationException
        }

        TextFile.write(out, "sample_attributes.txt");
        System.out.println("wrote sample_attributes.txt");
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 1) {
            ParseSamples cde = new ParseSamples(args);
        } else {
            System.out.println("syntax: fungen.ParseSamples\n" +
                    "<sample strings>\n");
        }
    }

}
