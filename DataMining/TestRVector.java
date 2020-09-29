package DataMining;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import util.MoreArray;

/**
 * User: marcin
 * Date: Dec 18, 2009
 * Time: 7:24:42 PM
 */
public class TestRVector {

    REXP Rexpr;
    Rengine Rengine;

    /**
     *
     */
    public TestRVector() {
        String[] R_args = {"--no-save"};
        Rengine = new Rengine(R_args, false, new TextConsole());
        Rexpr = Rengine.eval("current <- list(1:10,5:15,10:20)");
        System.out.println(Rengine.eval("current"));
        RVector tmpvect = Rexpr.asVector();
        for (int i = 0; i < tmpvect.size(); i++) {
            int[] tmp = (tmpvect.at(i)).asIntArray();
            if (tmp != null)
                System.out.println(tmp[0]);
            else
                System.out.println("array is null");
        }
    }

    /**
     * Main function
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            TestRVector kr = new TestRVector();
        }
    }

}
