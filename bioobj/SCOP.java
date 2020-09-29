package bioobj;

public class SCOP {

    char class_;
    int fold_;
    int superfamily_;
    int family_;

    public SCOP(char a, int b, int c, int d) {
        this.class_ = a;
        this.fold_ = b;
        this.superfamily_ = c;
        this.family_ = d;
    }

    public SCOP() {
    }


}
