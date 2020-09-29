package DataMining;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: marcin
 * Date: Oct 26, 2010
 * Time: 12:03:14 AM
 */
public class SampleExperiments {

    ArrayList remaining;
    ValueBlockPre sampvb;
    int max;

    /**
     *
     */
    public SampleExperiments(ValueBlockPre v, int m) {
        sampvb = new ValueBlockPre(v);

        max = m;

        reset();
    }

    /**
     * @param replace_position
     * @param rand
     * @return
     */
    public boolean replaceExpWithRand(int replace_position, Random rand) {
        if (remaining.size() > 0) {
            int next = getNextInt(rand);
            sampvb.exps[replace_position] = next;
            //Arrays.sort(sampvb.exps);
            return true;
        }
        //int[][] coords = {sampvb.exps, sampvb.exps};
        //System.out.println("replaceExpWithRand a/f " + IcJctoijID(coords));
        return false;
    }

    /**
     * @param rnd
     * @return
     */
    public int getNextInt(Random rnd) {
        int i = rnd.nextInt(remaining.size());
        int ret = (Integer) remaining.get(i);
        remaining.remove(i);
        //System.out.println("getNextInt "+remaining.size());
        return ret;
    }

    /**
     *
     */
    public void reset() {
        remaining = new ArrayList();
        for (int i = 1; i < max + 1; i++) {
            remaining.add(i);
        }
        for (int i = 0; i < sampvb.exps.length; i++) {
            int index = remaining.indexOf(sampvb.exps[i]);
            if (index != -1) {
                remaining.remove(index);
            }
        }
    }
}