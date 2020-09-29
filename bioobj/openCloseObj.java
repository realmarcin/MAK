package bioobj;

public class openCloseObj {
    int open;
    int close;
    double d;
    char state;

    public openCloseObj() {
        this.open = -1;
        this.close = -1;
    }

    public openCloseObj(int o, int c, double dist, char s) {
        this.open = o;
        this.close = c;
        this.d = dist;
        this.state = s;
    }


}