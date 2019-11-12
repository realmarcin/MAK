package DataMining;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: marcin
 * Date: Oct 25, 2010
 * Time: 11:44:30 PM
 */
public class SampleGenes {

    ArrayList remaining;
    ValueBlockPre sampvb;
    int max;

    /**
     *
     */
    public SampleGenes(ValueBlockPre v, int m) {
        sampvb = new ValueBlockPre(v);

        max = m;

        reset();
    }

    /**
     * @param replace_position
     * @param rand
     * @return
     */
    public boolean replaceGeneWithRand(int replace_position, Random rand) {
        if (remaining.size() > 0) {
            int next = getNextInt(rand);
            sampvb.genes[replace_position] = next;
            //Arrays.sort(sampvb.genes);
            // System.out.println("replaceGeneWithRand " + remaining.size());
            return true;
        }
        //int[][] coords = {sampvb.genes, sampvb.genes};
        //System.out.println("replaceGeneWithRand a/f " + IcJctoijID(coords));
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
        for (int i = 0; i < sampvb.genes.length; i++) {
            int index = remaining.indexOf(sampvb.genes[i]);
            if (index != -1) {
                remaining.remove(index);
            }
        }

        //System.out.println("reset " + remaining.size());
    }
}