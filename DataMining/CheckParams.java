package DataMining;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * User: marcin
 * Date: Nov 29, 2010
 * Time: 3:19:15 PM
 */
public class CheckParams {

    /**
     * @param args
     */
    public CheckParams(String[] args) {

        Class c = null;
        try {
            c = Class.forName("DataMining.Parameters");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field[] f = c.getDeclaredFields();
        Annotation[] a = c.getAnnotations();
        for (int i = 0; i < f.length; i++) {
            //System.out.println("Annotation " + i + "\t" + a[i].toString());
            System.out.println("Field " + i + "\t" + f[i].toString());
        }

        Parameters p = new Parameters();
        p.read(args[0]);

        /*File testout = new File(p.OUTDIR);
        if (!testout.exists()) {
            System.out.println("OUTDIR DNE, creating");
            testout.mkdir();
        }*/

        p.checkParams();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            CheckParams rm = new CheckParams(args);
        } else {
            System.out.println("syntax: java DataMining.CheckParams <file or dir of files>");
        }
    }
}
