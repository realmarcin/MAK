package bioobj;

public class ProtDBObj {
    public String[] all = {"fullname", "name", "gi", "uid", "fullprotseq", "psiprotseq", "dnaseq"};
    /*
public String fullname;
public String name;
public String gi;
public String uid;
public String fullprotseq;
public String psiprotseq;
public String dnaseq;
*/

    private String[] alldata;

    public ProtDBObj() {
        /*
         fullname = null;
         name = null;
         gi = null;
         uid = null;
         fullprotseq = null;
         psiprotseq = null;
          dnaseq = null;
          */
        alldata = new String[7];
    }

    public ProtDBObj(ProtDBObj old) {
        this.alldata = old.alldata;
    }

    public void update(String there, String which) {
        for (int i = 0; i < all.length; i++) {
            if (all[i].equals(which)) {
                alldata[i] = there;
                break;
            }
        }
    }

    public String ret(String which) {
        String ret = null;
        for (int i = 0; i < all.length; i++) {
            if (all[i].equals(which)) {
                ret = alldata[i];
                break;
            }
        }
        return ret;
    }

    /**
     * -1 means search all
     */
    public boolean hasString(String hmmm, int which) {
        boolean ret = false;

        if (alldata != null) {
            for (int i = 0; i < 4; i++) {
                if (which == i || which == -1)
                    if (alldata[i] != null) {
                        if (alldata[i].indexOf(hmmm) != -1)
                            ret = true;
                    }
            }
        }
        return ret;

    }

}
