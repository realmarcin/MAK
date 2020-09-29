package bioobj;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 3:36:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class RNASet extends DNASet {


    public RNASet() {

        super();
    }


    /**
     * Returns the complement of this set.
     *
     * @return
     */
    public Object complementSet() {

        findSize();
        RNASet ret = new RNASet();

        for (int i = 0; i < setsize; i++) {

            RNA old = (RNA) seqs.get(i);
            RNA now = new RNA(old.complement());
            now.name = old.name;
            int go = now.name.indexOf("; Antisense;");

            if (go == -1)
                go = now.name.lastIndexOf(";");

            now.name = now.name.substring(0, go);
            now.name += "; Sense;";
            ret.seqs.add(now);

        }
        return ret;
    }
}
