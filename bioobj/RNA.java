package bioobj;

/**
 * Created by IntelliJ IDEA.
 * User: Marcin
 * Date: Mar 11, 2005
 * Time: 12:48:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RNA extends DNA {

    public double calc = 0;
    public String bases = "ACGU";
    public String complement = "UGCA";

    public int polyastart;
    public String polyaseq;
    public int kozakstart;
    public String kozakseq;
    public int kozakid;
    public int transstart;
    public int transstop;
    public int translaststop;
    public String transstartseq;
    public String transstopseq;
    public String translaststopseq;


    /**
     *
     */
    public RNA() {

        super();
        init();
    }

    /**
     * @param s
     */
    public RNA(String s) {

        super(s);
        init();
    }

    /**
     * Method which initializes variable in the Sequence object.
     */
    private void init() {

        polyastart = -1;
        polyaseq = null;
        kozakstart = -1;
        kozakseq = null;
        kozakid = -1;
        transstart = -1;
        transstop = -1;
        translaststop = -1;
        transstartseq = null;
        transstopseq = null;
        translaststopseq = null;

    }


}
