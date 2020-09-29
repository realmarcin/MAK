package bioobj;

public class RPSBlastHit {
    public int identity;
    public String def;
    public String dbtype;

    public RPSBlastHit() {
        this.identity = -1;
        this.def = null;
    }

    public RPSBlastHit(int i, String s) {
        this.identity = i;
        this.def = new String(s);
    }

    public RPSBlastHit(RPSBlastHit r) {
        this.identity = r.identity;
        this.def = new String(r.def);
    }

}
