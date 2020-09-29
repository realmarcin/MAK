package DataMining;

/**
 * User: mjoachimiak
 * Date: Jul 8, 2009
 * Time: 11:57:40 PM
 */
public class AnalyzeBlockList {

    /**
     * @return
     */
    public final static double[] analyzePair(ValueBlockList vl1, ValueBlockList vl2) {
        int v1si = vl1.size();
        int v2si = vl2.size();
        double[] ret = new double[Math.min(v1si, v2si)];

        if (v1si < v2si) {
            double ratio = (double) v2si / (double) v1si;
            int j = 0;
            for (int i = 0; i < v1si; i++) {
                ValueBlock vb1 = (ValueBlock) vl1.get(i);
                for (int k = 0; k < ratio; k++) {
                    ValueBlock vb2 = (ValueBlock) vl2.get(j + k);
                    ret[i] += BlockMethods.computeBlockF1(vb1, vb2, false);
                }
                ret[i] /= ratio;
                j += ratio;
            }
        } else if (v1si > v2si) {
            double ratio = (double) v1si / (double) v2si;
            int j = 0;
            for (int i = 0; i < v2si; i++) {
                ValueBlock vb2 = (ValueBlock) vl2.get(i);
                for (int k = 0; k < ratio; k++) {
                    ValueBlock vb1 = (ValueBlock) vl1.get(j + k);
                    ret[i] += BlockMethods.computeBlockF1(vb2, vb1, false);
                }
                ret[i] /= ratio;
                j += ratio;
            }
        } else if (v1si == v2si) {
            for (int i = 0; i < v1si; i++) {
                ValueBlock vb1 = (ValueBlock) vl1.get(i);
                ValueBlock vb2 = (ValueBlock) vl2.get(i);
                ret[i] += BlockMethods.computeBlockF1(vb1, vb2, false);
            }
        }
        return ret;
    }
}
