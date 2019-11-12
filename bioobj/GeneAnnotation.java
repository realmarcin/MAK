package bioobj;

/**
 * User: marcin
 * Date: Jan 31, 2008
 * Time: 7:29:42 PM
 */
public class GeneAnnotation {

    public String yid;
    public String gene_name;
    public String SGD_id;
    public String chr;
    public int start, stop;
    public String type;
    public String annotation;

    public final static int SGGID_prefix_len = "SGDID:".length();

    /**
     * @param data
     */
    public GeneAnnotation(String data) {
        //System.out.println("GeneAnnotation " + data);
        int spa1 = data.indexOf(" ");
        yid = data.substring(0, spa1);
        int spa2 = data.indexOf(" ", spa1 + 1);
        gene_name = data.substring(spa1 + 1, spa2);
        int spa3 = data.indexOf(", ", spa2 + 1);
        SGD_id = data.substring(spa2 + 1 + SGGID_prefix_len, spa3);
        String from_str = " from ";
        int spa4 = data.indexOf(from_str, spa3 + 1);
        chr = data.substring(spa3 + 2, spa4);
        int spa5 = data.indexOf("-", spa4 + 1);
        start = Integer.parseInt(data.substring(spa4 + from_str.length(), spa5));
        int spa6 = Math.min(data.indexOf(", ", spa5 + 1), data.indexOf(",", spa5 + 1));
        stop = Integer.parseInt(data.substring(spa5 + 1, spa6));
        int spa7 = data.indexOf(", reverse", spa4 + 1);
        int spa7a = data.indexOf(", Verified", spa4 + 1);
        int spa7b = data.indexOf(", transposable_element_gene", spa4 + 1);
        if (spa7 != -1 && spa7a != -1 && spa7b != -1)
            spa7 = Math.min(spa7, Math.min(spa7a, spa7b));
        else if (spa7 == -1 && spa7a != -1 && spa7b != -1)
            spa7 = Math.min(spa7a, spa7b);
        else if (spa7 != -1 && spa7a == -1 && spa7b != -1)
            spa7 = Math.min(spa7, spa7b);
        else if (spa7 != -1 && spa7a != -1 && spa7b == -1)
            spa7 = Math.min(spa7, spa7a);
        else if (spa7 == -1 && spa7a == -1 && spa7b != -1)
            spa7 = spa7b;
        else if (spa7 == -1 && spa7a != -1 && spa7b == -1)
            spa7 = spa7a;
        else if (spa7 != -1 && spa7a == -1 && spa7b == -1) {
        }
        int spa8 = -1;
        if (spa7 != -1) {
            //System.out.println("GeneAnnotation " + spa7);
            spa8 = data.indexOf(", \"", spa7 + 1);
            type = data.substring(spa7 + 2, spa8);
        } else {
            spa8 = data.indexOf(", \"", spa6 + 1);
        }
        annotation = data.substring(spa8 + 3, data.length() - 1);

        //String test = ":" + StringUtil.replace(toString(), "\t", ":") + ":";
        //System.out.println("GeneAnnotation " + test);
    }

    /**
     * @return
     */
    public String toString() {
        return yid + "\t" + gene_name + "\t" + SGD_id + "\t" + chr + "\t" + start + "\t" + stop + "\t" + type + "\t" + annotation;
    }

}
