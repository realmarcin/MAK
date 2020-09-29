package util;

import dtype.SystemResource;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Extendable Super class for Java program.
 */
public class Program {

    public String name;

    public Date date;
    long min, hrs, sec;

    long startms = 0, endms = 0;

    util.GiveDate give;
    public String dat;

    public SystemResource sysRes;

    public boolean term = true;

    String out = "";

    public LogFile lf;
    public String logfile;

    boolean log_on = true;

    /**
     *
     */
    public Program() {
        init();
        startTime();
        logfile = "Program_" + dat + ".log";
        logfile = TestFile.uniqFile(logfile);
        startLog();
    }

    /**
     * @param n
     */
    public Program(String n) {
        name = n;
        init();
        startTime();
        logfile = "Program_" + dat + ".log";
        logfile = TestFile.uniqFile(logfile);
    }

    /**
     * @param n
     */
    public Program(String n, boolean l) {
        name = n;
        init();
        startTime();
        logfile = "Program_" + dat + ".log";
        logfile = TestFile.uniqFile(logfile);
        log_on = l;
        startLog();
    }

    /**
     * @param l
     */
    public Program(boolean l) {
        init();
        startTime();
        logfile = "Program_" + dat + ".log";
        logfile = TestFile.uniqFile(logfile);
        log_on = l;
        if (log_on) {
            startLog();
        }
    }

    /**
     *
     */
    public void startLog() {
        try {
            lf = new LogFile(logfile, log_on);
        } catch (IOException e) {
            lf = new LogFile();
        }
    }


    /**
     *
     */
    private void init() {
        sysRes = initSystemResources();
        startTime();
        endms = 0;

//System.out.println("Program "+dat);
    }

    /**
     *
     */
    public final static void quit() {
        System.exit(0);
    }

    /**
     *
     */
    public void printTime() {
        System.out.println(timeString());
    }

    /**
     *
     */
    public String timeString() {
        return "start ms " + startms + "\tend ms " + endms + "\tms diff " + (endms - startms);
    }

    /**
     *
     */
    public long timeElapsed() {
        return (endms - startms);
    }

    /**
     *
     */
    public void printStats() {
        if ((endms + startms) > 0) {
            sec = ((endms - startms) / (long)
                    (1000));
            min = ((endms - startms) / ((long)
                    (1000 * 60)));
            hrs = ((endms - startms) / ((long) 1000 * 60 * 60));
            out += ("Hours: " + hrs + "\n");
            out += ("Minutes: " + min + "\n");
            out += ("Seconds: " + sec + "\n");
            if (term) {
                System.out.println("Hours: " + hrs);
                System.out.println("Minutes: " + min);
                System.out.println("Seconds: " + sec);
            }
        }
    }

    /**
     *
     */
    public String retPrintStats() {
        String o = "";
        if ((endms + startms) > 0) {
            sec = ((endms - startms) / (long)
                    (1000));
            min = ((endms - startms) / ((long)
                    (1000 * 60)));
            hrs = ((endms - startms) / ((long) 1000 * 60 * 60));
            o += ("Hours: " + hrs + "\n");
            o += ("Minutes: " + min + "\n");
            o += ("Seconds: " + sec + "\n");
        }
        return o;
    }

    /**
     *
     */
    public void startTime() {
        startms = System.currentTimeMillis();
    }

    /**
     *
     */
    public void endTime() {
        endms = System.currentTimeMillis();
    }

    /**
     *
     */
    public void monitorTime() {
        endTime();
        printTime();
    }

    /**
     *
     */
    public final static SystemResource initSystemResources() {
        SystemResource sr = new SystemResource();
        sr.os = System.getProperty("os.name");
        sr.os_version = System.getProperty("os.version");
        sr.java_version = System.getProperty("java.version");
        sr.file_separator = System.getProperty("file.separator");
        sr.path_separator = System.getProperty("path.separator");
        sr.defaultpath = System.getProperty("user.dir");
        if (sr.os.indexOf("indows") != -1)
            sr.jarpath = sr.defaultpath + "\\";
        else
            sr.jarpath = sr.defaultpath + "/";
//System.out.println(sysRes.defaultpath+"\t"+sysRes.jarpath);
        return sr;
    }

    /**
     * @param out
     */
    public void log_write(String out) {
        if (lf != null)
            lf.w(out);
    }

    /**
     *
     */
    public void log_exit() {
        if (lf != null)
            lf.e();
    }

    /**
     * 
     * @param process
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static int getPID(Process process)
            throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException, SecurityException {
        Field field = process.getClass().getDeclaredField("pid");
        field.setAccessible(true);
        return field.getInt(process);
    }

    /**
     * 
     * @return
     */
    public String getPIDStr() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }
    
    /**
     *
     * @return
     */
    public int getPID() {
        String get = ManagementFactory.getRuntimeMXBean().getName();
        int i = Integer.parseInt(get.substring(0,get.indexOf("@")));
        return i;
    }

}
