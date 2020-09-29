/**
 *@breakSWPROT.java
 *
 *@Author Marcin Joachimiak
 *
 *@Version 1.0
 *
 *@Date 1/28/03
 *
 */

package util;

public class UID {
    public static String extractUID(String name) {
        //System.out.println("initial  "+name);
        String uid = new String();
        boolean ok = false;
        int last = -1;
        if (name != null) {
            boolean run = true;
            int beg = name.indexOf("gb");
            if (beg == -1)
                beg = name.indexOf("emb");
            last = beg;
            while (run == true) {
                //System.out.println("beglast "+beg+"  "+last);
                if (beg == -1) {
                    run = false;
                }
                int start = name.indexOf("|", beg);
                int end = name.indexOf("|", start + 1);
                //System.out.println("startend "+start+"  "+end);
                if (start != -1 && end != -1)
                    if (end - start > 5) {
                        uid = name.substring(start + 1, end);
                        //System.out.println("uid  "+uid);
                        char t1 = uid.charAt(0);
                        char t2 = uid.charAt(1);

                        if (Character.isDigit(t2) == true) {
                            if (Character.isLetter(t1) == true) {
                                run = false;
                                ok = true;
                                break;
                            }
                        }
                        last = beg;
                        if (run) {
                            beg = name.indexOf("|", start);
                            if (beg == last) {
                                ok = false;
                                run = false;
                            }
                            if (beg == -1) {
                                ok = false;
                                run = false;
                            }
                        }
                    }
            }

        }
        if (uid.indexOf(".") != -1 && uid.length() > 5)
            uid = uid.substring(0, uid.length() - 2);
        return uid;
    }
}
