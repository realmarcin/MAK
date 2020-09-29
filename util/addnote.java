package util;

public class addnote {
    String dat;
    String path = "/n/mint/home/httpd/mint/pub/notebook/Year2003/";
    String user = "JoachimiakMP";

    public addnote(String[] args) {

        util.GiveDate gd = new util.GiveDate();

        this.dat = gd.giveShortDate();
        System.out.println(dat);
        int firstdot = this.dat.indexOf(".");
        int lastdot = this.dat.lastIndexOf(".");

        String year = this.dat.substring(lastdot + 1, this.dat.length());
        String month = this.dat.substring(0, firstdot);
        String day = this.dat.substring(firstdot + 1, lastdot);
        if (day.length() == 1)
            day = "0" + day;
        if (month.length() == 1)
            month = "0" + month;

        System.out.println(year + "\t" + month + "\t" + day);

        String notedate = new String(year + "\t" + month + "\t" + day);
    }

    public static void main(String[] args) {
        //if(args.length == 0)
        addnote an = new addnote(args);
        //else
        //    System.out.println("faulty syntax.");
    }

}
