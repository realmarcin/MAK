package dialog;

import java.awt.*;

/**
 * Class to create a simple error graphic error dialog (through the AWT) that
 * lists an error, halting execution of the calling program until the user hits
 * an OK button.
 *
 * @author Jonathan D. Blake, Ph.D.
 * @version 0.0 4/97
 * @see java.awt.Frame
 * @see java.awt.Dialog
 */
public class ErrorDialog extends Dialog {

    public final static Font fontplain = new Font("Lucida console", Font.PLAIN, 14);
    private int advance = -1;
    private int descent = -1;
    private int lineHeight = -1;


     /**
     * Simple constructor to create the Error Dialog.
     *
     * @param f The parent Frame of the dialog box.
     * @param s The string to be displayed.
     */
    public ErrorDialog(Frame f, String s, String t) {

        super(f, t, true);
        
        setFont(fontplain);
        FontMetrics thisfm = getFontMetrics(fontplain);
        String test = "H";
        int advance = thisfm.stringWidth(test);
        int descent = thisfm.getMaxDescent();
        int lineHeight = thisfm.getHeight() - descent;

        setLayout(new GridLayout(2, 1));
        Panel p = new Panel();
        p.add(new Label(s));
        add(p);
        p = new Panel();
        p.add(new Button("OK"));
        add(p);
        resize(advance * s.length() + 10, 100);
        setResizable(false);
    }

    /**
     * Simple constructor to create the Error Dialog.
     *
     * @param f The parent Frame of the dialog box.
     * @param s The string to be displayed.
     */
    public ErrorDialog(Frame f, String s) {

        super(f, "Jevtrace Info", true);

        setFont(fontplain);
        FontMetrics thisfm = getFontMetrics(fontplain);
        String test = "H";
        int advance = thisfm.stringWidth(test);
        int descent = thisfm.getMaxDescent();
        int lineHeight = thisfm.getHeight() - descent;

        setLayout(new GridLayout(2, 1));
        Panel p = new Panel();
        p.add(new Label(s));
        add(p);
        p = new Panel();
        p.add(new Button("OK"));
        add(p);
        resize(advance * s.length() + 10, 100);
        setResizable(false);
    }

    /**
     * Method to process mouse clicks in the Error Dialog.  Overrides the parent
     * action method, calling the parent method if necessary.
     */
    public boolean action(Event e, Object arg) {
        if (e.id == Event.ACTION_EVENT) {
            this.hide();
            this.dispose();
            return true;
        } else if (e.id == Event.WINDOW_DESTROY) {
            this.hide();
            this.dispose();
            return true;
        }
        return super.action(e, arg);
    }
}
