package dtype;

/**
 *
 */
public class FamGrp {

    public String s=null;
    public char[] c;
    public int[] x;

    /**
     *
     * @param b
     * @param y
     */
    public FamGrp(String b, int[] y) {

        s = b;
        x = y;

    }

      /**
     *
     * @param b
     * @param y
     */
    public FamGrp(char[] b, int[] y) {

        c = b;
        x = y;

    }

    /**
     *
     * @return
     */
    public String toString() {

        if(s!=null)
            return (s + "\t" + x);
        else if(c!=null) {

            String ret = "";
            for(int i=0;i<c.length;i++)
                ret+=c[i];
            return ret+"\t"+x;
        }

   return null;
    }
}
