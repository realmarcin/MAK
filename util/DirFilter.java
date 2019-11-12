package util;

import java.io.File;
import java.io.FilenameFilter;


/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Feb 27, 2011
 * Time: 4:50:48 PM
 */
public class DirFilter implements FilenameFilter {
    protected String pattern;

    public DirFilter(String str) {
        pattern = str;
    }

    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(pattern.toLowerCase());
    }
}
