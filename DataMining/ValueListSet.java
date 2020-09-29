package DataMining;

/**
 * User: marcin
 * Date: Aug 9, 2007
 * Time: 5:38:20 PM
 */
public class ValueListSet {

    //public ValueBlockList MSEpval_top100;
    public ValueBlockList features_top100;
    public ValueBlockList inter_top100;
    public ValueBlockList expr_top100;
    public ValueBlockList full_top100;
    public ValueBlockList trajectory;

    /**
     *
     */
    public ValueListSet() {
        init();
    }

    /**
     *
     */
    public void init() {
        //MSEpval_top100 = new ValueBlockList();//initToN(1);
        features_top100 = new ValueBlockList();//initToN(1);
        inter_top100 = new ValueBlockList();//initToN(1);
        expr_top100 = new ValueBlockList();//initToN(1);
        full_top100 = new ValueBlockList();//initToN(1);
        trajectory = new ValueBlockList();//initToN(1);
    }

}
