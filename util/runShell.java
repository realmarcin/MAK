package util;

import java.io.*;
import java.util.Random;

public class runShell {

    int cmdcnt;
    /**
     * The default mode is 0 - does not use command counting.
     */
    int mode = 0;

    int random = -1;


    /**
     * Default instance of runShell, does not use command counting.
     */
    public runShell() {
        Random r = new Random();
        random = r.nextInt();
        mode = 0;
        random = -1;
    }

    /**
     * Instance to use a mode with implicit counting of executed commands. Each output commmand file is appended with the curent command count.
     */
    public runShell(int a) {
        if (a > -1 && a < 2)
            mode = a;

        if (mode == 1)
            cmdcnt = 0;

        Random r = new Random();
        random = r.nextInt();
    }

    /**
     * Instance to execute a single command with runShell.
     */
    public runShell(String run) {
        mode = 0;
        Random r = new Random();
        random = r.nextInt();
        execute(run);
    }

    /**
     * Full variable control implementation.
     */
    public boolean execute(String str, int jobid, String writecmd) {
        boolean ret = execute(str, new String("" + random + "_" + jobid), writecmd);
        return ret;
    }

    /**
     * Full variable control implementation.
     */
    public boolean execute(String str, String jobid, String writecmd) {
        if (writecmd.length() > 0)
            writecmd = writecmd + "_" + jobid;
        else
            writecmd = jobid;

        //System.out.println(writecmd);

        boolean ret = false;
        Runtime R = Runtime.getRuntime();
        Process P = null;

        //System.out.println("runShell cmd " + writecmd);
        try {
            PrintWriter out = new PrintWriter(new FileWriter(writecmd));
            out.print(str);
            out.close();
            try {
                //possibly use nohup for linux
                P = R.exec("/bin/sh " + writecmd + " > " + jobid + ".error");
                P.waitFor();
                P = R.exec("rm " + writecmd);
                P.waitFor();
                
                InputStream e = P.getErrorStream();
                e.close();
                InputStream i = P.getInputStream();
                i.close();
                OutputStream o = P.getOutputStream();
                o.close();
                
                P = null;
                ret = true;
            } catch (Exception ex) {
                if (P != null) P.destroy();
                System.out.println(ex);
            }
        } catch (IOException ex) {
            System.out.println("Can't write file ?!");
        }

        File delcmd = new File(writecmd);
        delcmd.delete();

        return ret;
    }

    /*
   *Medium variable control implement.
    *
    * @param str
    * @param jobid
    * @return
    */
    public boolean execute(String str, String jobid) {
        boolean ret = false;
        Runtime R = Runtime.getRuntime();
        Process P = null;
//String writecmd = "cmd_"+jobid;
        ret = execute(str, jobid, "");
        return ret;
    }

    /**
     * Low variable control implement.
     *
     * @param str
     * @return
     */
    public boolean execute(String str) {
        cmdcnt++;
        boolean ret = false;
        Runtime R = Runtime.getRuntime();
        Process P = null;
        String jobid = "123";
        String writecmd = "cmd";
        if (mode == 0)
            writecmd += "_" + jobid;
        else
            writecmd += "_" + cmdcnt;
        ret = execute(str, jobid, writecmd);
        return ret;
    }

    /**
     * Method to execute a command with a specific job number jobnum.
     *
     * @param str    command to execute
     * @param jobnum user defined job number
     */
    public boolean execute(String str, int jobnum) {

        boolean ret = false;
        Runtime R = Runtime.getRuntime();
        Process P = null;
        String writecmd = "cmd_" + jobnum;
        try {
            PrintWriter out = new PrintWriter(new FileWriter(writecmd));
            out.print(str);
            out.close();
            try {
                //nohup for linux
                P = R.exec("/bin/sh " + writecmd);//"nohup cmd_123");
                P.waitFor();
                P = R.exec("rm " + writecmd);
                P.waitFor();
                P = null;
                ret = true;
            } catch (Exception ex) {
                if (P != null) P.destroy();
                System.out.println(ex);
            }
        } catch (IOException ex) {
            System.out.println("Can't write file ?!");
        }

        return ret;
    }
}
