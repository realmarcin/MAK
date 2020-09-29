package dtype;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Feb 24, 2007
 * Time: 9:35:07 PM
 */
public class sensiiStringBuffer {

        public int x;
        public int y;
        public String s1;
        public StringBuffer s2;

        /**
         * @param a
         * @param b
         * @param t
         * @param z
         */
        public sensiiStringBuffer(int a, int b, String t, StringBuffer z) {
            this.x = a;
            this.y = b;
            this.s1 = t;
            this.s2 = z;
        }

        /**
         * @return
         */
        public String toString() {
            return (x + "\t" + y + "\t" + s1 + "\t" + s2);
        }

        /**
         * @param expr
         * @return
         */
        public int compareTo(Object expr) {
            if (((sensiiStringBuffer) expr).x > x)
                return -1;
            else if (((sensiiStringBuffer) expr).x == x)
                return 0;
            else
                return 1;
        }

        /**
         * @param s
         * @return
         */
        public final static ArrayList toList(Object[] s) {
            ArrayList a = new ArrayList();
            for (int i = 0; i < s.length; i++) {
                a.add((sensiiStringBuffer) s[i]);
            }
            return a;
        }


    }
