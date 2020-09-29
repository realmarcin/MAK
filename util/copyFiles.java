package util;

import java.io.File;

/**
 * compares a dir of files to a protein set, and makes a new protein set of missing
 * proteins
 */

public class copyFiles {
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

    public copyFiles(String[] args) {
        copyFilesback cfb = new copyFilesback(args[0], args[1], args[2]);
    }


    /**
     * Main function
     */
    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("SYNTAX: java util.copyFiles <dir to copy from> <master compare dir (what is not found in master) or id list (what is found in list)> <out dir>" + "\n");
        } else if (args.length == 3) {
            copyFiles kr = new copyFiles(args);
        }
    }
}

