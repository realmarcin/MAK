package util;

import java.io.FilenameFilter;
import java.io.File;

/**
 * User: marcin
 * Date: May 5, 2009
 * Time: 6:48:10 PM
 */
public class FileFilter implements FilenameFilter {
    protected String pattern;

    public FileFilter(String str) {
        pattern = str;
    }

    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(pattern.toLowerCase());
    }
}
