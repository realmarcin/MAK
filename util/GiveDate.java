package util;

import java.util.Calendar;

public class GiveDate {

    Calendar ca;

    public GiveDate() {


        refresh();

    }

    public GiveDate(String a) {

        refresh();

        String wow = giveShortDate();
        //System.out.println("short date  *" + wow);
    }

    public void refresh() {
        ca = Calendar.getInstance();
    }


    public final String giveTime() {

        refresh();

        int h = ca.get(Calendar.HOUR_OF_DAY);
        int m = ca.get(Calendar.MINUTE);
        int s = ca.get(Calendar.SECOND);
        String ret = h + ":" + m + ":" + s;
        return ret;
    }

    public final String giveTimeandDate() {

        refresh();

        String date = giveShortDate();
        String time = giveTime();

        String ret = time + " on " + date;
        return ret;
    }

    public final String giveShortDate() {

        refresh();

        String ret = "";
        ret += (ca.get(Calendar.MONTH) + 1) + ".";
        ret += ca.get(Calendar.DAY_OF_MONTH) + ".";
        ret += ca.get(Calendar.YEAR) - 2000;

        return ret;
    }

    public final long giveMilli() {

        refresh();

        return ca.getTimeInMillis();
    }
}
