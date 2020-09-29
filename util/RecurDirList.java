package util;

import java.io.File;
import java.util.Vector;

public class RecurDirList {

    Vector finaldirlist = new Vector();
    boolean done = false;

    public RecurDirList(String[] pass) {
        done = false;

//first dirlist
        String[] result = getDirList(pass[0]);

        for (int i = 0; i < result.length; i++) {
            if (result[i].charAt(result[i].length() - 1) == '/') {
                String send = new String(pass[0] + result[i]);

                //looped dir list for result dirlisting
                String[] thisdir = getDirList(send);

                for (int j = 0; j < thisdir.length; j++) {
                    if (thisdir[j].charAt(thisdir[j].length() - 1) == '/') {

                        String lowdir = new String(pass[0] + result[i] + thisdir[j]);
                        //second loop for dirlist of result dirlist
                        String[] testdir = getDirList(lowdir);
                        for (int k = 0; k < testdir.length; k++)
                            if (testdir[k].charAt(testdir[k].length() - 1) != '/') {
                                finaldirlist.addElement(lowdir);

                            }
                    } else if (thisdir[j].charAt(thisdir[j].length() - 1) != '/') {
                        finaldirlist.addElement(thisdir);

                    }
                }
            } else if (result[i].charAt(result[i].length() - 1) != '/') {
                finaldirlist.addElement(pass[0]);
            }
        }

        done = true;

        cleanList();
        for (int i = 0; i < finaldirlist.size(); i++)
            System.out.println(i + "  " + finaldirlist.elementAt(i).toString());
    }

    private void cleanList() {
        for (int i = 0; i < finaldirlist.size(); i++) {
            String one = finaldirlist.elementAt(i).toString();
            for (int j = i + 1; j < finaldirlist.size(); j++) {
                String two = finaldirlist.elementAt(j).toString();
                if (one == two) {
                    finaldirlist.removeElementAt(j);
                    j--;
                }
            }
        }
    }

    private String[] getDirList(String d) {
        System.out.println("getting dir  " + d);
        File director = new File(d);

        String[] files = director.list();

        return files;
    }

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 1) {
            System.out.println("Syntax:  java util.RecurDirList <dir>");
        } else {
            RecurDirList rdl = new RecurDirList(args);
        }
    }
}
