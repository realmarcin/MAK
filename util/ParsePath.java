package util;

import dtype.SystemResource;


/**
 * Class to return different portions of a path to a file or directory.
 * <p/>
 * <pre>
 * Version 1.0 8/14/99
 * </pre>
 *
 * @author Marcin Joachimak
 */

public class ParsePath {
    String path;
    String delim = "\\/";
    String file_separator;
    boolean dir = false;
    int lastsla = -1;
    int lastdot = -1;
    boolean windows, unix;

    /**
     * @param s
     */
    public ParsePath(String s) {

        path = s;
//System.out.println("ParsePath "+s);
        checkDir();
        findLastDot();
    }

    /**
     * Finds the index of the last dot.
     */
    public void findLastDot() {
        lastdot = path.lastIndexOf(".");
    }

    /**
     * Checks to see if the path is to a file or dir.
     */
    public void checkDir() {

        lastsla = path.lastIndexOf("/");
        if (path.lastIndexOf("/") != -1) {
            file_separator = "/";
            windows = true;
            unix = false;
        } else if (lastsla == -1) {
            lastsla = path.lastIndexOf("\\");
            file_separator = "\\";
        }
        if (lastsla == -1) {
            dir = false;
            if (lastsla != -1) {
                windows = false;
                unix = true;
            }
        }
    }

    /**
     * Returns the name of the file - the String between the directories and the file extension (last dot).
     */
    public String getName() {
        int dot = path.lastIndexOf(".");
        if (lastsla == -1)
            getPath();
        //System.out.println("lastsla "+lastsla);
        if (dot != -1 && lastsla != -1)
            return path.substring(lastsla + 1, dot);
        else if (lastsla != -1 && dot == -1)
            return path.substring(lastsla + 1, path.length());
        else if (lastsla == -1 && dot != -1)
            return path.substring(0, dot);
        else
            return path.substring(0, path.length());
    }

    /**
     * Returns the extension of the file - the String between the last dot and the end.
     */
    public String getExt() {
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }

    /**
     * Returns the extension of the file - the String between the last dot and the end.
     */
    public final static String getExt(String p) {
        return p.substring(p.lastIndexOf(".") + 1, p.length());
    }

    /**
     * @return
     */
    public String getPath() {
        int end = path.length();
        if (lastsla != -1) {
            end = lastsla;
            return path.substring(0, lastsla);
        } else
            return "";
    }

    /**
     * @return
     */
    public String getLastDirinPath() {
        int presla = StringUtil.lastIndexBefore(path, file_separator, lastsla);
        if (lastsla != -1 && presla != -1) {
            return path.substring(presla + 1, lastsla);
        } else
            return "";
    }

    /**
     * @return
     */
    public String getFile() {
        if (lastsla != -1) {
            return path.substring(lastsla + 1, path.length());
        } else
            return path;
    }

    /**
     * @param s
     * @return
     */
    public boolean isExt(String s) {
        if (getExt().equals(s))
            return true;
        return false;
    }

}
