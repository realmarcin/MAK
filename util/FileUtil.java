package util;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * User: marcinjoachimiak
 * Date: Apr 26, 2010
 * Time: 11:38:04 PM
 */
public class FileUtil {

    public static boolean copyFile(File in, File out)
            throws IOException {
        boolean ret = false;
        FileChannel inChannel = new
                FileInputStream(in).getChannel();
        FileChannel outChannel = new
                FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(),
                    outChannel);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
             if (inChannel != null && outChannel != null) {
                  ret = true;
             }
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
        return ret;
    }

}
