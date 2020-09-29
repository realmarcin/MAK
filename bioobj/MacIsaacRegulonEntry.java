package bioobj;

/**
 * An object representing a member of a regulon (currently based on Lee et al Science Sup Data).
 * <p/>
 * User: marcin
 * Date: Mar 29, 2008
 * Time: 3:59:18 PM
 */
public class MacIsaacRegulonEntry {

    String gene_name;
    String orf;
    String MIPS_descr;
    double[][] pval_ratio;

    /**
     *
     */
    public MacIsaacRegulonEntry() {

    }

    /**
     * @param data
     * @param offset
     * @param factors
     */
    public MacIsaacRegulonEntry(String[] data, String[] factors, int offset) {
        gene_name = data[0];
        orf = data[2];
        MIPS_descr = data[3];
        pval_ratio = new double[factors.length][2];
        for (int i = 0; i < factors.length; i++) {
            int val1 = 2 * i + offset;
            int val2 = 2 * i + 1 + offset;
            pval_ratio[i][0] = Double.parseDouble(data[val1]);
            try {
                try {
                    pval_ratio[i][1] = Double.parseDouble(data[val2]);
                } catch (NumberFormatException e) {
                    pval_ratio[i][1] = Double.NaN;
                    //e.printStackTrace();
                }
            } catch (Exception e) {
                //e.printStackTrace();
                pval_ratio[i][1] = Double.NaN;
                //System.out.println("MacIsaacRegulonEntry " + val1 + "\t" + val2 + "\t" + pval_ratio.length + "\t" + data.length);
                //System.out.println("MacIsaacRegulonEntry " + MoreArray.toString(data, ", "));
            }

        }
    }

}

