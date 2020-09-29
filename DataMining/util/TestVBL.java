package DataMining.util;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 5/16/16
 * Time: 9:02 PM
 */
public class TestVBL {


    public TestVBL(String[] args) {


        System.out.println("reading " + args[0]);

        ValueBlockList vbl = new ValueBlockList();

        try {
            vbl = ValueBlockList.read(args[0], true);
            System.out.println("vbl size " + vbl.size());

        } catch (Exception e) {
            System.out.println("failed to read exclusion vbl");
            e.printStackTrace();
            try {
                vbl = ValueBlockListMethods.readBIC(args[0], false);
                System.out.println("vbl size " + vbl.size());

            } catch (Exception e1) {
                System.out.println("failed to read exclusion bic");
                e1.printStackTrace();
            }
        }


    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            TestVBL rm = new TestVBL(args);
        } else {
            System.out.println("syntax: java DataMining.util.TestVBL\n" +
                    "<vbl file>"

            );
        }
    }
}
