package bioobj;

/**
 * User: marcin
 * Date: Oct 13, 2009
 * Time: 3:31:46 PM
 */
public class FragmentOverlap {


    /**
     * @return
     */
    public final static double[] computeOverlap(int start1, int stop1, int start2, int stop2) {
        double[] ret = new double[2];
        ret[0] = Double.NaN;
        ret[1] = Double.NaN;
        String error = "";
        if (start1 == 3251999)
            System.out.println("computeOverlap 1: " + start1 + "-" + stop1 + "\t2: " + start2 + "-" + stop2);
        //gene contained in stretch
        if (start1 <= start2 && stop1 >= stop2) {
            if (start1 == 3251999)
                System.out.println("computeOverlap start1 <= start2 && stop1 >= stop2 " +
                        (stop2 - start1) + "\t" + (stop2 - start2) + "\t" + (stop1 - start1));
            ret[0] = (double) (stop2 - start2) / (double) Math.abs(stop2 - start2);
            ret[1] = (double) (stop2 - start2) / (double) Math.abs(stop1 - start1);
            error += "overlap1 (stop2 - start2) " + (stop2 - start2) + "\t" + Math.abs(stop2 - start2) + "\n";
            error += "overlap2 (stop2 - start2) " + (stop2 - start2) + "\t" + Math.abs(stop1 - start1) + "\n";
        }
        //stretch contained in gene
        else if (start2 <= start1 && stop2 >= stop1) {
            if (start1 == 3251999)
                System.out.println("computeOverlap start2 <= start1 && stop2 >= stop1 " +
                        (stop2 - start1) + "\t" + (stop2 - start2) + "\t" + (stop1 - start1));
            ret[0] = (double) (stop1 - start1) / (double) Math.abs(stop2 - start2);
            ret[1] = (double) (stop1 - start1) / (double) Math.abs(stop1 - start1);
            error += "overlap1 (stop1 - start1) " + (stop1 - start1) + "\t" + Math.abs(stop2 - start2) + "\n";
            error += "overlap2 (stop1 - start1) " + (stop1 - start1) + "\t" + Math.abs(stop1 - start1) + "\n";
        }
        //5' overlap
        else if (start1 <= start2 && stop1 >= start2) {
            if (start1 == 3251999)
                System.out.println("computeOverlap start1 <= start2 && stop1 >= start2 " +
                        (stop2 - start1) + "\t" + (stop2 - start2) + "\t" + (stop1 - start1));
            ret[0] = (double) (stop1 - start2) / (double) Math.abs(stop2 - start2);
            ret[1] = (double) (stop1 - start2) / (double) Math.abs(stop1 - start1);
            error += "overlap1 (stop1 - start2) " + (stop1 - start2) + "\t" + Math.abs(stop2 - start2) + "\n";
            error += "overlap2 (stop1 - start2) " + (stop1 - start2) + "\t" + Math.abs(stop1 - start1) + "\n";
            //skip2 = true;
        }
        //3' overlap
        else if (start1 <= stop2 && stop1 >= stop2) {
            if (start1 == 3251999)
                System.out.println("computeOverlap start1 <= stop2 && stop1 >= stop2 " +
                        (stop2 - start1) + "\t" + (stop2 - start2) + "\t" + (stop1 - start1));
            ret[0] = (double) (stop2 - start1) / (double) Math.abs(stop2 - start2);
            ret[1] = (double) (stop2 - start1) / (double) Math.abs(stop1 - start1);
            error += "overlap1 (stop2 - start1) " + (stop2 - start1) + "\t" + Math.abs(stop2 - start2) + "\n";
            error += "overlap2 (stop2 - start1) " + (stop2 - start1) + "\t" + Math.abs(stop1 - start1) + "\n";
            //skip2 = true;
        }
        if (!Double.isNaN(ret[0])) {
            ret[0] *= 100;
            if (ret[0] > 100) {
                System.out.println("computeOverlap overlap1 > 100 " + ret[0]);
                System.out.print(error);
            }
        }
        if (!Double.isNaN(ret[1])) {
            ret[1] *= 100;
            if (ret[1] > 100) {
                System.out.println("computeOverlap overlap2 > 100  " + ret[1]);
                System.out.print(error);
            }
        }
        return ret;
    }
}
