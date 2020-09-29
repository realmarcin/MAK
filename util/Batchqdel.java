package util;

import java.util.ArrayList;

/**
 *
 */
public class Batchqdel {

    /**
     * @param a
     */
    public Batchqdel(String[] a) {

        String user = a[0];
        String pattern = a[1];

        String qstatlist = user + "_qstat.list";

        String grepqstat = "qstat -f | grep " + user + " | grep " + pattern + " > " + qstatlist;

        runShell rs = new runShell();
        System.out.println(grepqstat);
        rs.execute(grepqstat);

        ArrayList data = TextFile.readtoList(qstatlist);
        for (int i = 0; i < data.size(); i++) {
            String cur = (String) data.get(i);
            //String jobid = (string)cur.get(0);
            int i1 = cur.indexOf(" ");
            if (i1 == 0)
                i1 = cur.indexOf(" ", i1 + 1);
            String jobid = cur.substring(0, i1);
            String qdelid = "qdel " + jobid;
            System.out.println(qdelid);
            rs.execute(qdelid);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 2) {
            Batchqdel bq = new Batchqdel(args);

        } else {
            System.out.println("usage: java util.Batchqdel <user name> <quoted job id string>");
        }
    }

}