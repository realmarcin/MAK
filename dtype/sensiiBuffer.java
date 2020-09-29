package dtype;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Feb 24, 2007
 * Time: 9:19:07 PM
 */
public class sensiiBuffer {

        public int x;
        public int y;
        public StringBuffer s1;
        public StringBuffer s2;

        /**
         * @param a
         * @param b
         * @param t
         * @param z
         */
        public sensiiBuffer(int a, int b, StringBuffer t, StringBuffer z) {
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
            if (((sensiiBuffer) expr).x > x)
                return -1;
            else if (((sensiiBuffer) expr).x == x)
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
                a.add((sensiiBuffer)s[i]);
            }
            return a;
        }

}
