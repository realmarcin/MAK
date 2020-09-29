package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * compares a dir of files to a protein set, and makes a new protein set of missing
 * proteins
 */

public class copyFilesback {
    String outdir;
    String[] files = null;
    String[] ids = null;
    File fileone;
    File filetwo;
    String[] files1;
    String[] files2;
    String[] tfiles1;
    String[] tfiles2;
    String F1;
    String F2;

    public copyFilesback(String file1, String file2, String out) {

        this.F1 = new String(file1);

        if (file2.indexOf(".id") != -1)
            readIds(file2);
        else
            this.F2 = new String(file2);

        this.fileone = new File(file1);
        this.files1 = this.fileone.list();

        this.filetwo = new File(file2);
        this.files2 = this.filetwo.list();

        this.outdir = new String(out);
        System.out.println("Got both directory listings!");

//truncFileNames();

        if (this.ids != null)
            compareFilestoID();
        else if (this.F2 != null)
            compareFilestoFiles();
    }


    private void readIds(String fileplusdir) {
        Vector one = new Vector();
        try {
            //BufferedReader in = cohen.IO.openReader(fileplusdir);
            BufferedReader in = new BufferedReader(new FileReader(fileplusdir));
            String data = null;
            StringTokenizer tokens = null;

            data = in.readLine();

            while (data != null) {

                tokens = new StringTokenizer(data);
                String first = new String((tokens.nextToken()).toString());
                //System.out.println("line   "+data+"  token  "+first);
                int ind = first.indexOf(" ");
                while (ind != -1) {
                    if (ind != 0)
                        first = new String(first.substring(ind - 1, ind + 1));
                    else
                        first = new String(first.substring(1));
                    ind = first.indexOf(" ");
                }
                one.addElement(first);

                data = in.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error with list file.");
        }

        if (one.size() > 0) {
            ids = new String[one.size()];
            for (int i = 0; i < one.size(); i++) {
                this.ids[i] = new String((String) one.elementAt(i));
            }
        }
    }

    private void truncFileNames() {
        this.tfiles1 = new String[this.files1.length];

        for (int j = 0; j < this.files1.length; j++) {
            String thisfilej = new String(this.files1[j]);
            if (thisfilej.indexOf(".aln") != -1 && thisfilej.indexOf("FPTHGS000") != -1) {
                int ee = thisfilej.indexOf("E");
                int f = thisfilej.indexOf("FPTHGS");
                int dot = thisfilej.indexOf(".");
                if (f != -1 && ee != -1)
                    this.tfiles1[j] = new String(thisfilej.substring(ee, dot));
            } else
                this.tfiles1[j] = new String();
        }
        if (this.files2 != null) {
            this.tfiles2 = new String[this.files2.length];

            for (int i = 0; i < this.files2.length; i++) {
                String thisfilei = new String(this.files2[i]);

                int eej = thisfilei.indexOf("E");
                int fj = thisfilei.indexOf("FPTHGS");
                int dot = thisfilei.indexOf(".");
                if (fj != -1 && eej != -1)
                    this.tfiles2[i] = new String(thisfilei.substring(eej, dot));
            }
        }
    }

    private void compareFilestoFiles() {
        for (int j = 0; j < this.tfiles1.length; j++) {
            boolean found = false;
            if (this.tfiles1[j] != null) {
                String thisfilej = new String(this.tfiles1[j]);

                for (int i = 0; i < this.tfiles2.length; i++) {
                    String thisfilei = new String(this.tfiles2[i]);

                    if (thisfilej.equals(thisfilei)) {
                        this.tfiles1[j] = null;
                        found = true;
                        break;
                    }
                }
                if (!found && this.files1[j].indexOf("FPTHGS000") != -1) {
                    String add = new String(this.files1[j]);

                    runShell rs = new runShell();
                    String exec = new String("cp " + F1 + this.files1[j] + " " + outdir);
                    System.out.println(j + "  " + exec);
                    boolean get = rs.execute(exec);

                    System.out.println("copied file " + add);
                }
            }
        }
    }

    private void compareFilestoID() {
        int added = 0;

        System.out.println("comparing to IDS!");
//for(int j=0; j< this.tfiles1.length; j++)
        for (int j = 0; j < this.files1.length; j++) {
            boolean found = false;
//if(this.tfiles1[j] != null)
            if (this.files1[j] != null) {
                //String thisfilej = new String(this.tfiles1[j]);
                String thisfilej = new String(this.files1[j]);
                //if(files1[j].indexOf(".aln") != -1)
                for (int i = 0; i < this.ids.length; i++) {
                    String thisfilei = new String(this.ids[i]);
                    int sla = thisfilei.indexOf("/");
                    if (sla == -1) {
                        if (thisfilej.indexOf(thisfilei) != -1) {
                            found = true;
                            break;
                        }
                    } else if (sla != -1) {
                        thisfilej = new String(F1 + files1[j]);
                        //System.out.println("comparing "+thisfilej+"  to  "+thisfilei);
                        if (thisfilej.indexOf(thisfilei) != -1) {
                            found = true;
                            break;
                        }
                    }
                }
                //if(found && this.files1[j].indexOf("FPTHGS000") != -1)
                if (found) {
                    String add = new String(this.files1[j]);

                    runShell rs = new runShell();
                    String exec = new String("cp " + F1 + this.files1[j] + "  " + outdir);
                    System.out.println(j + "  " + added + "  " + exec);
                    boolean get = rs.execute(exec);
                    added++;

                } else //if(this.files1[j].indexOf(".aln") != -1)
                    System.out.println("DID NOT COPY! " + this.files1[j]);
            }
        }

    }

}

