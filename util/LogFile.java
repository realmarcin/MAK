package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Basic class to perform logging functions.
 */

public class LogFile {

    public String logpath;
    public String log = "";
    private PrintWriter pw;


    /**
     *
     */
    public LogFile() {

    }
    /**
     * Basic constructor.
     */
    public LogFile(String p) throws IOException {

        util.ParsePath pp = new util.ParsePath(p);
        if (!pp.getExt().equals("log"))
            p += ".log";

//System.out.println("Starting log file: "+p);

        logpath = p;
        File flog = new File(p);
        if (flog.exists()) {
            flog.delete();
            //System.out.println("Log file already exists - replacing the old one.");
        }

        pw = new PrintWriter(new FileWriter(p));
    }


    /**
     * Basic constructor.
     */
    public LogFile(String p, boolean b) throws IOException {

        util.ParsePath pp = new util.ParsePath(p);
        if (!pp.getExt().equals("log"))
            p += ".log";

//System.out.println("Starting log file: "+b);

        logpath = p;

        if (b) {
            //System.out.println("Starting log file name: "+p);
            pw = new PrintWriter(new FileWriter(p));
            File flog = new File(p);
            if (flog.exists()) {
                flog.delete();
                //System.out.println("Log file already exists - replacing the old one.");
            }
        }
    }

    /**
     * Writes this String to the log file.
     *
     * @param l
     */
    public void w(String l) {

        //System.out.println(l);
        if (pw != null)
            pw.println(l);
    }

    /**
     * Writes the current version of the log data to file.
     */
    public void w() {
        if (pw != null)
            pw.print(log);
    }

    /**
     * Adds to the log data.
     */

    public void a(String s) {

        log += s;
    }

    /**
     * Adds to the log data.
     */

    public void an(String s) {

        log += s + "\n";
    }


    /**
     * Adss to the log data then writes to file.
     */
    public void aw(String s) {

        log += s;
        if (pw != null)
            pw.print(log);
    }

    /**
     * Method to exit.
     */
    public void e() {

        if (pw != null)
            pw.close();
    }

    /**
     * Method to exit.
     */
    public void exit() {

        if (pw != null)
            pw.close();
    }
}
