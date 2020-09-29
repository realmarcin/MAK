package dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Class extendialog awt.Dialog to retrieve a user defined String.
 * Created by Marcin
 * Date: Jun 15, 2005
 * Time: 6:22:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringDialog extends Dialog {


    public final static Font fontplain = new Font("Lucida console", Font.PLAIN, 14);
    private int advance = -1;
    private int descent = -1;
    private int lineHeight = -1;

    public String text = "";
    TextField tf;

    public boolean changed = false;

    /**
     * Simple constructor to create the Error Dialog.
     *
     * @param f The parent Frame of the dialog box.
     * @param s The string to be displayed.
     */
    public StringDialog(Frame f, String s) {

        super(f, s, true);

        setFont(fontplain);
        FontMetrics thisfm = getFontMetrics(fontplain);
        String test = "H";
        int advance = thisfm.stringWidth(test);
        int descent = thisfm.getMaxDescent();
        int lineHeight = thisfm.getHeight() - descent;

        //setLayout(new GridLayout(3,1));
        Panel p = new Panel();
        p.setLayout(new GridLayout(3, 1));

        tf = new TextField("");
        p.add(tf);

        final Button ok = new Button("OK");

        ok.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getSource().equals(ok)) {

                    text = tf.getText();
                    changed = true;
                    hide();
                    dispose();
                }
            }
        });

        p.add(ok);

        final Button cancel = new Button("Cancel");
        cancel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getSource().equals(cancel)) {

                    hide();
                    dispose();
                }
            }
        });

        p.add(cancel);

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
