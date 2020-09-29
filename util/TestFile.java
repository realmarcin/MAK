package util;

import java.io.File;

/**
 * Class implementing various file methods.
 */
public class TestFile {


    /**
     * Tests whether a file with this String rep exists.
     *
     * @param f
     * @return
     */
    public final static boolean test(String f) {

        File t = new File(f);
        if (t.exists())
            return true;
        else
            return false;
    }


    /**
     * Returns a unique file name (based on integer increments b/f file extension).
     *
     * @param f
     * @return
     */
    public final static String uniqFile(String f) {

        //System.out.println("testing unique "+f);

        ParsePath p = new ParsePath(f);

        int i = 0;
        boolean exists = TestFile.test(f);

        int und = p.getName().lastIndexOf("_");
        int test = -1;

        try {

            test = Integer.parseInt(p.getName().substring(und + 1, p.getName().length()));
        } catch (Exception e) {
        }


        String name = p.getName();
        if (test != -1 && und != -1)
            name = p.getName().substring(0, und);
        else
            name = p.getName();

        while (exists) {

            i++;
            f = name + "_" + i + "." + p.getExt();
            exists = TestFile.test(f);
        }

        //System.out.println("returning unique "+f);

        return f;
    }
}