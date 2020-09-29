package dtype;

/**
 * Class of double[4], for coding u-branches of the input tree.
 */
public class Branch {

    public double[] x = null;
    public double[] y = null;
    public int open, close = -1;
    public int openmin, openmax, closemin, closemax = -1;
    public String s;
    public int index = -1;

    /**
     *
     */
    public Branch() {
        x = new double[4];
        y = new double[4];
        open = -1;
        close = -1;
        openmin = -1;
        openmax = -1;
        closemin = -1;
        closemax = -1;
        index = -1;
        s = null;
    }

    /**
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */

    public Branch(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
        x = new double[4];
        ;
        y = new double[4];
        ;
        x[0] = x0;
        x[1] = x1;
        x[2] = x2;
        x[3] = x3;
        y[0] = y0;
        y[1] = y1;
        y[2] = y2;
        y[3] = y3;
        open = -1;
        close = -1;
        openmin = -1;
        openmax = -1;
        closemin = -1;
        closemax = -1;
        index = -1;
        s = null;
    }

    /**
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param t
     */
    public Branch(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, String t) {
        x = new double[4];
        y = new double[4];
        x[0] = x0;
        x[1] = x1;
        x[2] = x2;
        x[3] = x3;
        y[0] = y0;
        y[1] = y1;
        y[2] = y2;
        y[3] = y3;
        open = -1;
        close = -1;
        openmin = -1;
        openmax = -1;
        closemin = -1;
        closemax = -1;
        index = -1;
        s = t;
    }

    /**
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param t
     */
    public Branch(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, int i, String t) {
        x = new double[4];
        y = new double[4];
        x[0] = x0;
        x[1] = x1;
        x[2] = x2;
        x[3] = x3;
        y[0] = y0;
        y[1] = y1;
        y[2] = y2;
        y[3] = y3;
        open = -1;
        close = -1;
        openmin = -1;
        openmax = -1;
        closemin = -1;
        closemax = -1;
        index = i;
        s = t;
    }

    /**
     * @param b
     */
    public Branch(Branch b) {
        if (b != null) {
            x = new double[4];
            y = new double[4];
            for (int i = 0; i < 4; i++) {
                x[i] = b.x[i];
                y[i] = b.y[i];
            }
            open = b.open;
            close = b.close;

            openmin = b.openmin;
            openmax = b.openmax;
            closemin = b.closemin;
            closemax = b.closemax;
            index = b.index;
            s = b.s;
        }
    }

    /**
     * @return
     */
    public String toString() {
        String str = s + "\n";
        for (int i = 0; i < 4; i++) {

            String c = (i == 3) ? "" : " ";
            str += "(" + x[i] + "," + y[i] + ")" + c;
        }
        return (str += " open: " + open + " close: " + close + " index: " + index);
    }

    /**
     * @return
     */
    public String fulltoString() {
        String str = new String();
        for (int i = 0; i < 4; i++) {
            String c = (i == 3) ? "" : " ";
            str += "(" + x[i] + "," + y[i] + ")" + c;
        }

        return (str += "\nopen:" + open + "  close:" + close + "  openmin:" + openmin + "  closemin:" + closemin + "  openmax:" + openmax + "  closemax:" + closemax + " index: " + index);
    }

    /**
     *
     */
    public void setDefaultMinMax() {
        openmin = open;
        openmax = open;
        closemin = close;
        closemax = close;
    }

    /**
     *
     * @param c
     * @return
     */
    /*
	public boolean equals(Branch c)
	{
		boolean ret = false;
		if(x[0] == c.x[0] && x[1] == c.x[1] && x[2] == c.x[2] && x[3] == c.x[3])
			ret = true;
		return ret;
		}
        */
}
