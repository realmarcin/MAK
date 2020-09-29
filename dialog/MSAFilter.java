package dialog;

import java.io.File;
import java.io.FilenameFilter;

public class MSAFilter implements FilenameFilter {
    private String[] extension;

    public MSAFilter(String[] extension) {

        this.extension = extension;
    }

    public boolean accept(File dir, String name) {

        boolean ret = false;

        for (int i = 0; i < extension.length; i++)
            if (name.endsWith(extension[i])) {

                ret = true;
                break;
            }

        return ret;
    }
}