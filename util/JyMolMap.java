package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: marcin
 * Date: Jun 3, 2005
 * Time: 1:40:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class JyMolMap {
    public class KeyValuePair {
        public String key;
        public Object value;

        public KeyValuePair(String s, double d) {
            key = s;
            value = new Double(d);
        }

        public KeyValuePair(String s, Double d) {
            key = s;
            value = d;
        }
    }

    public class KeyValueCall {
        HashMap hm = new HashMap();

        public KeyValueCall(KeyValuePair pair1) {
            hm.put(pair1.key, pair1.value);
        }

        public KeyValueCall(KeyValuePair pair1, KeyValuePair pair2) {
            hm.put(pair1.key, pair1.value);
            hm.put(pair2.key, pair2.value);
        }

        public Map toMap() {
            return hm;
        }
    };

    /**
     * @param keyValueCall
     */
    public void protoMethod1(KeyValueCall keyValueCall) { }// protoMethod2(keyValueCall.toMap())

    public void protoMethod2(Map map) {
        double val1;
        int val2;
        String val3;

        val1 = ((Double)map.get("key1")).doubleValue();
        if (val1==Double.NaN) val1 = 1.0; // default value

        val2 = ((Integer)map.get("key2")).intValue();
        if (val2==Double.NaN) val2 = (int)1.0; // default value

        val3 = (String)map.get("key3");
        if (val3==null) val3 = ""+1; // default value

        // jnimethod(val1,val2,val3);

    };

    public JyMolMap(String[] args) {
        protoMethod1(
                new KeyValueCall(
                        new KeyValuePair("key1", 1.0),
                        new KeyValuePair("key2", 2.0)));

        HashMap hm = new HashMap();
        hm.put((Object)"key1",(Object)new Double(1.0));
        hm.put((Object)"key2",(Object)new Double(2.0));
        protoMethod2(hm);

    }

    public static void main(String[] args) {


        if (args.length == 0) {
            JyMolMap j = new JyMolMap(args);




        } else {

            System.out.println("error");
        }
    }
}
