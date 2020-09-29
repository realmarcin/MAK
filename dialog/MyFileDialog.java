package dialog;

import java.awt.*;

/**
 * This class extends the FileDialog class to handle situations where the
 * GetFile method throws an exception because the file name is empty.
 * <p/>
 * All else about this class is the same.
 *
 * @author Jonathan D. Blake, Ph.D.
 * @version 0.0 4/97
 * @see java.awt.FileDialog
 */
public class MyFileDialog extends FileDialog {

    /**
     * Simple constructor, calls equivalent one in parent.
     */
    public MyFileDialog(Frame f, String s) {
        super(f, s);
    }

    /**
     * Simple constructor, calls equivalent one in parent.
     */
    public MyFileDialog(Frame f, String s, int m) {
        super(f, s, m);
    }

    /**
     * Modified GetFile function in case the file name returned by the user is null.
     */
    public String getFile() {
        String name = super.getFile();

        if (name != null)
            for (int i = 0; i < name.length(); i++) {
                if (name.charAt(i) == '*')
                    return name.substring(0, i - 1);
            }
        return name;
    }

    /**
     * Old skool java AWT.
     */
    public final boolean handleEvent(Event ev) {

        if (ev.id == Event.WINDOW_DESTROY) {

            hide();
            dispose();
            return true;
        }
        return false;
    }
}
