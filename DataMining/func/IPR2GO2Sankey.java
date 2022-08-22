package DataMining.func;


import util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class IPR2GO2Sankey {

    /**
     * @param args
     */
    public IPR2GO2Sankey(String[] args) {

        String[] text = TextFile.readtoArray(args[0]);

        ArrayList out = new ArrayList();

        HashMap count = new HashMap<String, Integer>();
        for (int i = 0; i < text.length; i++) {
            System.out.println(i+"\t"+text[i]);
            String[] split1 = text[i].split(" ");
            Object o = count.get(split1[0]);
            if(o == null) {
                count.put(split1[0], 1);
            }
            else {
                count.put(split1[0], (Integer)o + 1);
            }
        }

        for (int i = 0; i < text.length; i++) {
            System.out.println(i+"\t"+text[i]);
            String[] split1 = text[i].split(" ");
            String[] split2 = text[i].split(" > ");
            String[] split3 = split2[1].split(" ; ");

            String curstr = split1[0] + " [1] " + split3[1];
            System.out.println(curstr);
            Object o = count.get(split1[0]);
            if((Integer)o > 5) {
                out.add(curstr);
            }

        }

        TextFile.write(out, args[0] + "_sankeymatic.txt");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            IPR2GO2Sankey rm = new IPR2GO2Sankey(args);
        } else {
            System.out.println("syntax: java DataMining.func.IPR2GO2Sankey\n" +
                    "<input>\n"
            );
        }
    }
}
